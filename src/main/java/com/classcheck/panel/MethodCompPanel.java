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
import com.classcheck.type.BasicType;
import com.classcheck.type.ParamCheck;
import com.classcheck.type.ReferenceType;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodCompPanel extends JPanel {
	private List<IClass> javaPackage;
	Map<MyClass, List<JPanel>> mapPanelList;
	List<CodeVisitor> codeVisitorList;
	Map<MyClass, CodeVisitor> codeMap;

	private StatusBar mtpSourceStatus;

	private List<JComboBox<String>> boxList;
	private DefaultTableModel tableModel;

	public MethodCompPanel() {
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		codeMap = new HashMap<MyClass, CodeVisitor>();
		mtpSourceStatus = null;

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public MethodCompPanel(List<IClass> javaPackage, ClassBuilder cb,
			List<CodeVisitor> codeVisitorList) {
		this();
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
		List<JPanel> panelList = mapPanelList.get(myClass);
		LevensteinDistance levensteinAlgorithm = new LevensteinDistance();
		//tmp
		double distance = 0;
		//最も大きかった距離
		double maxDistance = 0;
		//最も距離が近かった文字列
		String keyStr=null;
		List<Method> methodList = myClass.getMethods();
		List<MethodDeclaration> codeMethodList = null;
		List<ConstructorDeclaration> codeConstructorList = null;
		CodeVisitor codeVisitor = codeMap.get(myClass);
		JLabel l = null;
		JPanel p = null;
		JComboBox<String> methodComboBox = null;

		//同じシグネチャーが選択されているかどうかを調べる
		boxList = new ArrayList<JComboBox<String>>();
		boolean isSameMethodSelected = false;
		//コンストラクタかどうか判定する
		boolean isConstructor = false;

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
			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			l = new JLabel("(左)スケルトンコードのメソッド : (右)ソースコードのメソッド");
			l.setFont(new Font("SansSerif", Font.BOLD, 12));
			l.setAlignmentX(CENTER_ALIGNMENT);
			p.add(l);
			panelList.add(p);

			if (codeVisitor != null){
				codeMethodList = codeVisitor.getMethodList();
				codeConstructorList = codeVisitor.getConstructorList();

				for (Method method : methodList) {
					popSb = new StringBuilder();
					ArrayList<String> strList = new ArrayList<String>();
					isConstructor = false;

					/*
					 * コンストラクタは読み込まない
					 * =>メソッド名とクラス名が同類のメソッドはコンストラクタ
					 */
					if (method.getName().equals(myClass.getName())) {
						isConstructor = true;
					}

					if (isConstructor) {

						System.out.println("*** ConstructorDeclaration ***");

						//ソースコードのコンストラクタを追加
						for (ConstructorDeclaration constructorDeclaration : codeConstructorList) {

							System.out.println(Modifier.toString(constructorDeclaration.getModifiers()));

							//ソースコードのメソッドのパラメータ数と
							//スケルトンコードのパラメータ個数の一致

							if (constructorDeclaration.getParameters().size() == method.getParams().length && 
									//ソースコードのコンストラクタの修飾子と
									//スケルトンコードの修飾子の一致
									method.getModifiers().contains(
											Modifier.toString(constructorDeclaration.getModifiers())
											)){

								strList.add(constructorDeclaration.getDeclarationAsString());
							}
						}

					}else{
						System.out.println("*** method ***");
						System.out.println("type is :"+method.getModifiers());

						System.out.println("*** MethodDeclaration ***");
						//ソースコードのメソッドを追加
						for (MethodDeclaration methodDeclaration : codeMethodList) {


							System.out.println(Modifier.toString(methodDeclaration.getModifiers()));

							//ソースコードのメソッドのパラメータ数と
							//スケルトンコードのパラメータ個数の一致
							//パラメータの型も一致させる（型はソースコードに依存する、また基本型の場合も考えるようにする)
							if (methodDeclaration.getParameters().size() == method.getParams().length && 
									//ソースコードのメソッドの修飾子と
									//スケルトンコードの修飾子の一致
									method.getModifiers().contains(Modifier.toString(methodDeclaration.getModifiers())) &&
									//返り値の型一致（型はソースコードに依存する、また基本型の場合も考えるようにする)
									(new ReferenceType(javaPackage,tableModel, method, methodDeclaration).evaluate() || 
											new BasicType(method,methodDeclaration).evaluate()
											) && 
											new ParamCheck(javaPackage,tableModel,method.getParams(),methodDeclaration.getParameters()).evaluate()){
								strList.add(methodDeclaration.getDeclarationAsString());
							}
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

						distance = levensteinAlgorithm.getDistance(method.toSignature(), str);
						if(maxDistance < distance){
							maxDistance = distance;
							keyStr = str;
						}
					}
					methodComboBox.setSelectedItem(keyStr);
					p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
					matcher = patern.matcher(method.getSignature());
					l = new JLabel(matcher.replaceAll(""));
					l.setAlignmentX(CENTER_ALIGNMENT);

					//ポップアップテキストを加える
					popSb.append("<html>");
					//popSb.append("<p width=\"500\">");
					popSb.append("<p>");

					popSb.append("定義:<br>");
					if (method.getOperation().getDefinition().length() == 0) {
						popSb.append("なし<br>");
					}else{
						String[] comments = method.getOperation().getDefinition().split("\\n", 0);

						for (String comment : comments) {
							popSb.append(comment + "<br>");
						}
					}

					popSb.append("本体条件:<br>");
					if (method.getOperation().getBodyCondition().length() == 0) {
						popSb.append("なし<br>");
					}else{
						popSb.append("・"+method.getOperation().getBodyCondition()+"<br>");
					}

					popSb.append("事前条件:<br>");
					if (method.getOperation().getPreConditions().length == 0) {
						popSb.append("なし<br>");
					}else{
						for(String text : method.getOperation().getPreConditions()){
							popSb.append("・"+text+"<br>");
						}
					}
					popSb.append("事後条件:<br>");
					if (method.getOperation().getPostConditions().length == 0) {
						popSb.append("なし<br>");
					}else{
						for(String text : method.getOperation().getPostConditions()){
							popSb.append("・"+text+"<br>");
						}
					}
					popSb.append("</p>");
					popSb.append("</html>");

					l.setToolTipText(popSb.toString());
					l.setCursor(new Cursor(Cursor.HAND_CURSOR));
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