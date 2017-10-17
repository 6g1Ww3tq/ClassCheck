package com.classcheck.panel;

import java.awt.FlowLayout;
import java.awt.Font;
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

import org.apache.lucene.search.spell.LevensteinDistance;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

/*
 * アスタのシグネチャーと
 * ソースコードのシグネチャーの比較を行うパネル
 */
public class MethodCompPanel extends JPanel {
	Map<MyClass, List<JPanel>> mapPanelList;
	List<CodeVisitor> codeVisitorList;
	Map<MyClass, CodeVisitor> codeMap;

	private StatusBar mtpSourceStatus;
	private MemberTabPane mtp;
	private boolean rxistSameMethod = false;

	private List<JComboBox<String>> boxList;

	public MethodCompPanel() {
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		codeMap = new HashMap<MyClass, CodeVisitor>();
		mtpSourceStatus = null;

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public MethodCompPanel(MemberTabPane mtp,
			ClassBuilder cb,
			List<CodeVisitor> codeVisitorList) {
		this();
		this.mtp = mtp;
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
			l = new JLabel("(左)astahのメソッド : (右)ソースコードのメソッド");
			l.setFont(new Font("SansSerif", Font.BOLD, 20));
			l.setAlignmentX(CENTER_ALIGNMENT);
			p.add(l);
			panelList.add(p);

			if (codeVisitor != null){
				codeMethodList = codeVisitor.getMethodList();
				codeConstructorList = codeVisitor.getConstructorList();
				ArrayList<String> strList = new ArrayList<String>();

				for (MethodDeclaration methodDeclaration : codeMethodList) {
					strList.add(methodDeclaration.getDeclarationAsString());
				}

				for (ConstructorDeclaration constructorDeclaration : codeConstructorList) {
					strList.add(constructorDeclaration.getDeclarationAsString());
				}

				for (Method method : methodList) {
					popSb = new StringBuilder();

					/*
					 * コンストラクタは読み込まない
					 */
					/*
					if (method.getName().equals(myClass.getName())) {
						continue;
					}
					 */

					methodComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
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
		for (int i=0; i < boxList.size() ; i++){
			box_1 = boxList.get(i);

			strBox_1 = box_1.getSelectedItem().toString();
			for(int j=0; j < boxList.size() ; j++){
				box_2 = boxList.get(j);

				strBox_2 = box_2.getSelectedItem().toString();

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

}