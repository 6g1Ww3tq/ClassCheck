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

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IComment;
import com.change_vision.jude.api.inf.model.IConstraint;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Field;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.event.LabelMouseAdapter;
import com.classcheck.type.ReferenceType;
import com.github.javaparser.ast.body.FieldDeclaration;

public class FieldComparePanel extends JPanel{

	private List<IClass> javaPackage;
	private ClassBuilder cb;
	StatusBar fcpSourceStatus;
	private List<CodeVisitor> codeVisitorList;
	private HashMap<MyClass, List<JPanel>> mapPanelList;
	private HashMap<MyClass, CodeVisitor> codeMap;
	private ArrayList<JComboBox<String>> boxList;
	private DefaultTableModel tableModel;

	public FieldComparePanel(ClassBuilder cb, HashMap<MyClass, CodeVisitor> codeMap) {
		this.cb = cb;
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		this.codeMap = codeMap;
		fcpSourceStatus = null;

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public FieldComparePanel(List<IClass> javaPackage, ClassBuilder cb,
			List<CodeVisitor> codeVisitorList,
			HashMap<MyClass, CodeVisitor> codeMap) {
		this(cb,codeMap);
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
		List<Field> umlFieldList = myClass.getFields();
		List<FieldDeclaration> codeFieldList = null;
		CodeVisitor codeVisitor = this.codeMap.get(myClass);
		JLabel l = null;
		JPanel p = null;
		JComboBox<String> fieldComboBox = null;

		//同じシグネチャーが選択されているかどうかを調べる
		this.boxList = new ArrayList<JComboBox<String>>();
		boolean isSameFieldSelected = false;

		//フィールドの説明を加える
		IComment[] comments;
		IConstraint[] constraints;
		IAttribute attr;

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
			l = new JLabel("(左)スケルトンコードのフィールド : (右)ソースコードのフィールド");
			l.setFont(new Font("SansSerif", Font.BOLD, 12));
			l.setAlignmentX(CENTER_ALIGNMENT);
			p.add(l);
			panelList.add(p);

			if (codeVisitor != null){
				codeFieldList = codeVisitor.getFieldList();

				for (Field umlField : umlFieldList){

					ArrayList<String> strList = new ArrayList<String>();

					for (FieldDeclaration codeField : codeFieldList) {
						//型を比較するメソッドを作る（型はソースコードに依存する、また基本型の場合も考えるようにする)
						//ソースコードのメソッドの修飾子と
						//スケルトンコードの修飾子の一致
						//スケルトンコードの修飾子には「public 」のようにスペースが入り込むので削除する
						String umlField_modify_str = umlField.getModifiers();
						String codeField_modify_str = Modifier.toString(codeField.getModifiers());
						
						//最後の空白スペースを取り除く
						if (umlField_modify_str.endsWith(" ")) {
							umlField_modify_str = umlField_modify_str.substring(0, umlField_modify_str.lastIndexOf(" "));
						}
						
						if (umlField_modify_str.contains("static") || 
								codeField_modify_str.contains("static")) {
							System.out.println("===static===");
							System.out.println("umlField_modify_str:"+umlField_modify_str);
							System.out.println("codeField_modify_str:"+codeField_modify_str);
							System.out.println("code_is_static:"+Modifier.isStatic(codeField.getModifiers()));
						}

						if (umlField_modify_str.equals(codeField_modify_str) && 
								//型一致（型はソースコードに依存する、また基本型の場合も考えるようにする)
								new ReferenceType(this.javaPackage,this.tableModel, umlField, codeField).evaluate()){
							strList.add(codeField.toString().replaceAll(";", ""));
						}
					}


					popSb = new StringBuilder();
					fieldComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
					fieldComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
					this.boxList.add(fieldComboBox);
					//レーベンシュタイン距離を初期化
					distance = 0;
					maxDistance = 0;
					keyStr = null;

					for (String str : strList){

						distance = levensteinAlgorithm.getDistance(umlField.toString(), str);
						if (maxDistance < distance) {
							maxDistance = distance;
							keyStr = str;
						}
					}

					fieldComboBox.setSelectedItem(keyStr);
					p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
					matcher = patern.matcher(umlField.toString());
					l = new JLabel(matcher.replaceAll("").replaceAll(";", ""));
					l.setAlignmentX(CENTER_ALIGNMENT);

					attr = umlField.getAttribute();
					//ポップアップテキストを加える
					popSb.append("<html>");
					popSb.append("<p>");

					popSb.append("定義:<br>");
					if (attr.getDefinition().length() == 0) {
						popSb.append("なし<br>");
					}else{
						String[] strs = attr.getDefinition().split("\\n", 0);

						for (String comment : strs) {
							popSb.append(comment + "<br>");
						}
					}

					popSb.append("コメント:<br>");
					if (attr.getComments().length == 0) {
						popSb.append("なし<br>");
					}else{
						comments = attr.getComments();

						for (IComment comment : comments){
							popSb.append("・"+comment.toString()+"<br>");
						}
					}

					popSb.append("制約:<br>");
					if (attr.getConstraints().length == 0) {
						popSb.append("なし<br>");
					}else{
						constraints = attr.getConstraints();

						for (IConstraint constraint : constraints){
							popSb.append("・"+constraint.toString()+"<br>");
						}
					}

					popSb.append("</p>");
					popSb.append("</html>");

					l.setToolTipText(popSb.toString());
					l.setCursor(new Cursor(Cursor.HAND_CURSOR));

					//クラス図を表示
					l.addMouseListener(new LabelMouseAdapter(myClass, l, getParent()));

					p.add(l);
					p.add(fieldComboBox);
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

		for (int i=0; i < this.boxList.size() ; i++){
			box_1 = this.boxList.get(i);

			obj = box_1.getSelectedItem();

			if (obj == null) {
				continue ;
			}

			strBox_1 = obj.toString();

			for(int j=0; j < this.boxList.size() ; j++){
				box_2 = this.boxList.get(j);

				obj = box_2.getSelectedItem();

				if (obj == null) {
					continue ;
				}

				strBox_2 = obj.toString();

				if (i==j) {
					continue ;
				}

				if (strBox_1.equals(strBox_2)) {
					isSameFieldSelected = true;
					break;
				}
			}

			if (isSameFieldSelected) {
				break;
			}
		}

		return isSameFieldSelected;
	}

	/**
	 * パネルからもステータスのテキストを変更可能にする
	 * @param text
	 */
	public void setStatusText(String text){
		fcpSourceStatus.setText(text);
	}

	public void setStatus(StatusBar fcpSourceStatus) {
		this.fcpSourceStatus = fcpSourceStatus;
	}

	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
}