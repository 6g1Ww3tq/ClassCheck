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
import com.classcheck.panel.event.LabelMouseAdapter;
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
		JLabel l = null;
		JPanel p = null;
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

		//System.out.println("***initCompoenent***(myClass):"+myClass.getName());
		//System.out.println("***initCompoenent***(CodeVisitor):"+codeVisitor.getClassName());
		if (isAllChange) {
			panelList.clear();

			//説明のパネルを加える
			//（左）astah	:（右)	ソースコード
			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			l = new JLabel("(左)スケルトンコードのメソッド : (右)ソースコードのメソッド");
			l.setFont(new Font("SansSerif", Font.BOLD, 12));
			l.setAlignmentX(CENTER_ALIGNMENT);
			p.add(l);
			panelList.add(p);

			if (codeVisitor != null){
				codeMethodList = codeVisitor.getMethodList();
				codeConstructorList = codeVisitor.getConstructorList();

				for (Method umlMethod : umlMethodList) {
					popSb = new StringBuilder();
					ArrayList<String> strList = new ArrayList<String>();
					String umlMethod_modify_str = umlMethod.getModifiers();
					//最後の空白スペースを取り除く
					if (umlMethod_modify_str.endsWith(" ")) {
						umlMethod_modify_str = umlMethod_modify_str.substring(0, umlMethod_modify_str.lastIndexOf(" "));
					}

					//ソースコードのコンストラクタを追加
					for (ConstructorDeclaration codeConstructor : codeConstructorList) {
						String codeConstructor_modify_str = Modifier.toString(codeConstructor.getModifiers());

						//ソースコードのメソッドのパラメータ数と
						//スケルトンコードのパラメータ個数の一致
						if (codeConstructor.getParameters().size() == umlMethod.getParams().length && 
								//ソースコードのコンストラクタの修飾子と
								//スケルトンコードの修飾子の一致
								//スケルトンコードの修飾子には「public 」のようにスペースが入り込むので削除する
								umlMethod_modify_str.equals(codeConstructor_modify_str) && 
								//パラメータの型の一致
								new ParamCheck(this.javaPackage, this.tableModel, umlMethod.getParams(), codeConstructor.getParameters()).evaluate()){
							strList.add(codeConstructor.getDeclarationAsString());
						}
					}

					//ソースコードのメソッドを追加
					for (MethodDeclaration codeMethod : codeMethodList) {
						String codeMethod_modify_str = Modifier.toString(codeMethod.getModifiers());

						//ソースコードのメソッドのパラメータ数と
						//スケルトンコードのパラメータ個数の一致
						//パラメータの型も一致させる（型はソースコードに依存する、また基本型の場合も考えるようにする)
						if (codeMethod.getParameters().size() == umlMethod.getParams().length && 
								//ソースコードのメソッドの修飾子と
								//スケルトンコードの修飾子の一致
								//スケルトンコードの修飾子には「public 」のようにスペースが入り込むので削除する
								umlMethod_modify_str.equals(codeMethod_modify_str) &&
								//返り値の型一致（型はソースコードに依存する、また基本型の場合も考えるようにする)
								new ReferenceType(this.javaPackage,this.tableModel, umlMethod, codeMethod).evaluate() && 
								//パラメータの型の一致
								new ParamCheck(this.javaPackage,this.tableModel,umlMethod.getParams(),codeMethod.getParameters()).evaluate()){
							strList.add(codeMethod.getDeclarationAsString());
						}
					}

					methodComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
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
					p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
					matcher = patern.matcher(umlMethod.getSignature());
					l = new JLabel(matcher.replaceAll(""));
					l.setAlignmentX(CENTER_ALIGNMENT);

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

					l.setToolTipText(popSb.toString());
					l.setCursor(new Cursor(Cursor.HAND_CURSOR));
					
					//クラス図を表示
					l.addMouseListener(new LabelMouseAdapter(myClass, l, getParent()));
					
					p.add(l);
					p.add(methodComboBox);
					panelList.add(p);
				}

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