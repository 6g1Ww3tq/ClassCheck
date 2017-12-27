package com.classcheck.panel;

import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.apache.lucene.search.spell.LevensteinDistance;

import com.change_vision.jude.api.inf.model.IClass;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.event.ClassLabelMouseAdapter;
import com.classcheck.panel.event.ClickedLabel;
import com.classcheck.type.ParamCheck;
import com.classcheck.type.ReferenceType;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodComparePanel extends JPanel {
	private List<IClass> javaPackage;
	Map<MyClass, List<JPanel>> mapPanelList;
	List<CodeVisitor> codeVisitorList;
	HashMap<MyClass, CodeVisitor> codeMap;

	private StatusBar mtpSourceStatus;

	private List<JComboBox<String>> boxList;
	private DefaultTableModel tableModel;

	public MethodComparePanel(HashMap<MyClass, CodeVisitor> codeMap) {
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		this.codeMap = codeMap;
		mtpSourceStatus = null;

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public MethodComparePanel(List<IClass> javaPackage, ClassBuilder cb,
			List<CodeVisitor> codeVisitorList,
			HashMap<MyClass, CodeVisitor> codeMap) {
		this(codeMap);
		this.javaPackage = javaPackage;
		this.codeVisitorList = codeVisitorList;

		for (MyClass myClass : cb.getClasslist()) {
			for (CodeVisitor codeVisitor : codeVisitorList) {
				if(myClass.getName().equals(codeVisitor.getClassName())){
					codeMap.put(myClass, codeVisitor);
				}
			}

			mapPanelList.put(myClass, new ArrayList<JPanel>());
		}
	}

	public List<CodeVisitor> getCodeVisitorList() {
		return codeVisitorList;
	}

	public void setCodeVisitorList(List<CodeVisitor> codeVisitorList) {
		this.codeVisitorList = codeVisitorList;
	}

	public Map<MyClass, CodeVisitor> getCodeMap() {
		return codeMap;
	}

	public Map<MyClass, List<JPanel>> getMapPanelList() {
		return mapPanelList;
	}

	public boolean initComponent(final MyClass myClass,boolean isAllChange){
		List<JPanel> panelList = this.mapPanelList.get(myClass);
		LevensteinDistance levensteinAlgorithm = new LevensteinDistance();
		//tmp
		double distance = 0;
		//最も大きかった距離
		double maxDistance = 0;
		//最も距離が近かった文字列
		String keyStr=null;
		List<Method> umlMethodList = myClass.getMethods();
		List<MethodDeclaration> codeMethodList = null;
		List<ConstructorDeclaration> codeConstructorList = null;
		CodeVisitor codeVisitor = this.codeMap.get(myClass);
		JComboBox<String> methodComboBox = null;

		//同じシグネチャーが選択されているかどうかを調べる
		boxList = new ArrayList<JComboBox<String>>();
		boolean isSameMethodSelected = false;

		//ポップアップテキスト
		StringBuilder popSb = null;

		//コメントを取り除く
		String regex = "(?s)/\\*.*\\*/";
		Pattern patern = Pattern.compile(regex);
		Matcher matcher;

		if (isAllChange) {
			panelList.clear();

			//説明のパネルを加える
			//（左）astah	:（右)	ソースコード
			JPanel explainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			JLabel explainLabel = new JLabel("(左)スケルトンコードのフィールド : (右)ソースコードのフィールド");
			explainLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			explainLabel.setAlignmentX(CENTER_ALIGNMENT);
			explainPanel.add(explainLabel);
			panelList.add(explainPanel);

			if (codeVisitor != null){
				codeMethodList = codeVisitor.getMethodList();
				codeConstructorList = codeVisitor.getConstructorList();
			}

			for (Method umlMethod : umlMethodList) {
				boolean isUmlConstructor = false;
				/*
				 * メソッドの名前がクラスの名前と同じ場合 => メソッドはコンストラクタとする
				 */
				if (umlMethod.getName().equals(myClass.getName())) {
					isUmlConstructor = true;
				}

				matcher = patern.matcher(umlMethod.getSignature());
				JLabel l = new JLabel(matcher.replaceAll(""));
				JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
				String popToolTip_str = popToolTips_UML(umlMethod);

				l.setAlignmentX(CENTER_ALIGNMENT);
				l.setToolTipText(popToolTip_str);
				l.setCursor(new Cursor(Cursor.HAND_CURSOR));

				//クラス図を表示
				l.addMouseListener(new ClassLabelMouseAdapter(myClass, l, getParent(),ClickedLabel.MethodSig));

				//ソースコードが定義されていない場合
				if (codeVisitor == null) {
					//空のボックスを作成
					methodComboBox = new JComboBox<String>();
					methodComboBox.setToolTipText("<html>"+
							"<p>"+
							"対応するフィールドがありません<br>"+
							"</p>"+
							"</html>");
				}else{
					ArrayList<String> strList = new ArrayList<String>();
					String umlMethod_modify_str = umlMethod.getModifiers();
					StringBuilder methodError_sb = new StringBuilder();
					int itemCount = 0;

					//最後の空白スペースを取り除く
					if (umlMethod_modify_str.endsWith(" ")) {
						umlMethod_modify_str = umlMethod_modify_str.substring(0, umlMethod_modify_str.lastIndexOf(" "));
					}

					//umlMethodがコンストラクタである場合
					if (isUmlConstructor) {
						//ソースコードのコンストラクタを追加
						for (ConstructorDeclaration codeConstructor : codeConstructorList) {
							String codeConstructor_modify_str = Modifier.toString(codeConstructor.getModifiers());
							//ソースコードのメソッドのパラメータ数と
							//スケルトンコードのパラメータ個数の一致
							boolean isCorrentLength = codeConstructor.getParameters().size() == umlMethod.getParams().length;
							//ソースコードのコンストラクタの修飾子と
							//スケルトンコードの修飾子の一致
							//スケルトンコードの修飾子には「public 」のようにスペースが入り込むので削除する
							boolean isCorrectModifiy = umlMethod_modify_str.equals(codeConstructor_modify_str);
							//パラメータの型の一致
							boolean isCorrectParam = new ParamCheck(isCorrentLength,this.javaPackage, this.tableModel, umlMethod.getParams(), codeConstructor.getParameters()).evaluate();

							if (isCorrentLength && isCorrectModifiy && isCorrectParam){
								strList.add(codeConstructor.getDeclarationAsString());
								itemCount++;
							}else{
								methodError_sb.append(codeConstructor.getDeclarationAsString()+"<br>");
								if (isCorrentLength == false) {
									methodError_sb.append("=>"+"パラメータの個数が合っていません"+"<br>");
								}
								if (isCorrectModifiy == false) {
									methodError_sb.append("=>"+"修飾子が合っていません"+"<br>");
								}
								if (isCorrectParam == false) {
									methodError_sb.append("=>"+"パラメータの型が合っていません"+"<br>");
								}
							}
						}
						
					//umlMethodがメソッドである場合
					}else{

						//ソースコードのメソッドを追加
						for (MethodDeclaration codeMethod : codeMethodList) {
							String codeMethod_modify_str = Modifier.toString(codeMethod.getModifiers());
							//ソースコードのメソッドのパラメータ数と
							//スケルトンコードのパラメータ個数の一致
							//パラメータの型も一致させる（型はソースコードに依存する、また基本型の場合も考えるようにする)
							boolean isCorrentLength = codeMethod.getParameters().size() == umlMethod.getParams().length;
							//ソースコードのメソッドの修飾子と
							//スケルトンコードの修飾子の一致
							//スケルトンコードの修飾子には「public 」のようにスペースが入り込むので削除する
							boolean isCorrectModifiy = umlMethod_modify_str.equals(codeMethod_modify_str);
							//返り値の型一致（型はソースコードに依存する、また基本型の場合も考えるようにする)
							boolean isCorrectRtnType = new ReferenceType(this.javaPackage,this.tableModel, umlMethod, codeMethod).evaluate();
							//パラメータの型の一致
							boolean isCorrectParam = new ParamCheck(isCorrentLength,this.javaPackage,this.tableModel,umlMethod.getParams(),codeMethod.getParameters()).evaluate();

							if (isCorrentLength && isCorrectModifiy&& isCorrectRtnType && isCorrectParam){
								strList.add(codeMethod.getDeclarationAsString());
								itemCount++;
							}else{
								methodError_sb.append(codeMethod.getDeclarationAsString()+"<br>");
								if (isCorrentLength == false) {
									methodError_sb.append("=>"+"パラメータの個数が合っていません"+"<br>");
								}
								if (isCorrectModifiy == false) {
									methodError_sb.append("=>"+"修飾子が合っていません"+"<br>");
								}
								if (isCorrectParam == false) {
									methodError_sb.append("=>"+"パラメータの型が合っていません"+"<br>");
								}
							}
						}					
					}


					methodComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
					//ボックスに一つも選択するアイテムがない場合はヒントを表示する用意をする
					if (itemCount == 0) {
						String tooltipText = "";
						tooltipText +="<html>";
						tooltipText +="<p>";
						tooltipText +=methodError_sb.toString();
						tooltipText +="</p>";
						tooltipText +="</html>";
						methodComboBox.setToolTipText(tooltipText);
					}
					methodComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
					boxList.add(methodComboBox);
					//レーベンシュタイン距離を初期化
					distance = 0;
					maxDistance = 0;
					keyStr = null;
					for (String str : strList) {

						distance = levensteinAlgorithm.getDistance(umlMethod.toSignature(), str);
						if(maxDistance < distance){
							maxDistance = distance;
							keyStr = str;
						}
					}
					methodComboBox.setSelectedItem(keyStr);

				}

				p.add(l);
				p.add(methodComboBox);
				panelList.add(p);
			}


		}

		//描画
		for (JPanel panel : panelList) {
			add(panel);
		}

		//ツリーアイテムを押してもうまく表示されないので
		//常に早く表示させるよう対策
		repaint();


		//同じメソッドが選択されているかどうかを調べる
		JComboBox  box_1,box_2;
		String strBox_1,strBox_2;
		Object obj;

		for (int i=0; i < boxList.size() ; i++){
			box_1 = boxList.get(i);

			obj = box_1.getSelectedItem();

			if (obj == null) {
				continue ;
			}

			strBox_1 = obj.toString();

			for(int j=0; j < boxList.size() ; j++){
				box_2 = boxList.get(j);

				obj = box_2.getSelectedItem();

				if (obj == null) {
					continue ;
				}

				strBox_2 = obj.toString();

				if (i==j) {
					continue ;
				}

				if (strBox_1.equals(strBox_2)) {
					isSameMethodSelected = true;
					break;
				}
			}

			if (isSameMethodSelected) {
				break;
			}
		}

		DebugMessageWindow.msgToTextArea();
		return isSameMethodSelected;
	}

	private String popToolTips_UML(Method umlMethod){
		StringBuilder popSb = new StringBuilder();

		//ポップアップテキストを加える
		popSb.append("<html>");
		//popSb.append("<p width=\"500\">");
		popSb.append("<p>");

		popSb.append("定義:<br>");
		if (umlMethod.getOperation().getDefinition().length() == 0) {
			popSb.append("なし<br>");
		}else{
			String[] comments = umlMethod.getOperation().getDefinition().split("\\n", 0);

			for (String comment : comments) {
				popSb.append(comment + "<br>");
			}
		}

		popSb.append("本体条件:<br>");
		if (umlMethod.getOperation().getBodyCondition().length() == 0) {
			popSb.append("なし<br>");
		}else{
			popSb.append("・"+umlMethod.getOperation().getBodyCondition()+"<br>");
		}

		popSb.append("事前条件:<br>");
		if (umlMethod.getOperation().getPreConditions().length == 0) {
			popSb.append("なし<br>");
		}else{
			for(String text : umlMethod.getOperation().getPreConditions()){
				popSb.append("・"+text+"<br>");
			}
		}
		popSb.append("事後条件:<br>");
		if (umlMethod.getOperation().getPostConditions().length == 0) {
			popSb.append("なし<br>");
		}else{
			for(String text : umlMethod.getOperation().getPostConditions()){
				popSb.append("・"+text+"<br>");
			}
		}
		popSb.append("</p>");
		popSb.append("</html>");

		return popSb.toString();
	}

	/**
	 * パネルからもステータスのテキストを変更可能にする
	 * @param text
	 */
	public void setStatusText(String text){
		mtpSourceStatus.setText(text);
	}

	public void setStatus(StatusBar mtpSourceStatus) {
		this.mtpSourceStatus = mtpSourceStatus;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
}