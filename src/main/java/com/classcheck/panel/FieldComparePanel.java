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
import com.classcheck.panel.event.ClassLabelMouseAdapter;
import com.classcheck.panel.event.ClickedLabel;
import com.classcheck.type.ReferenceType;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

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
		JComboBox<String> fieldComboBox = null;

		//同じシグネチャーが選択されているかどうかを調べる
		this.boxList = new ArrayList<JComboBox<String>>();
		boolean isSameFieldSelected = false;

		//ポップアップテキスト

		//コメントを取り除く
		String regex = "(?s)/\\*.*\\*/";
		Pattern patern = Pattern.compile(regex);
		Matcher matcher;

		if (isAllChange) {
			panelList.clear();

			//説明のパネルを加える
			//（左）astah	:（右)	ソースコード
			JPanel explainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			JLabel explainLabel = new JLabel("(左)クラス図で定義したィールド : (右)ソースコードのフィールド");
			explainLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
			explainLabel.setAlignmentX(CENTER_ALIGNMENT);
			explainPanel.add(explainLabel);
			panelList.add(explainPanel);

			if (codeVisitor != null){
				codeFieldList = codeVisitor.getFieldList();
			}

			for (Field umlField : umlFieldList){
				matcher = patern.matcher(umlField.toString());
				JLabel l = new JLabel(matcher.replaceAll("").replaceAll(";", ""));
				JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
				String popToolTip_str = popToolTips_UML(umlField);

				l.setAlignmentX(CENTER_ALIGNMENT);
				l.setToolTipText(popToolTip_str);
				l.setCursor(new Cursor(Cursor.HAND_CURSOR));

				//クラス図を表示
				l.addMouseListener(new ClassLabelMouseAdapter(myClass, l, getParent(),ClickedLabel.FieldDefinition));

				//ソースコードが定義されていない場合
				if (codeVisitor == null) {
					//空のボックスを作成
					fieldComboBox = new JComboBox<String>();
					fieldComboBox.setToolTipText("<html>"+
							"<p>"+
							"対応するフィールドがありません<br>"+
							"</p>"+
							"</html>");
				}else{
					ArrayList<String> strList = new ArrayList<String>();
					StringBuilder fieldDefinition_sb = new StringBuilder();
					StringBuilder fieldError_sb = new StringBuilder();
					int itemCount = 0;

					for (FieldDeclaration codeField : codeFieldList) {
						//型を比較するメソッドを作る（型はソースコードに依存する、また基本型の場合も考えるようにする)
						//ソースコードのメソッドの修飾子と
						//スケルトンコードの修飾子の一致
						//スケルトンコードの修飾子には「public 」のようにスペースが入り込むので削除する
						String umlField_modify_str = umlField.getModifiers();
						String codeField_modify_str = Modifier.toString(codeField.getModifiers());
						boolean isCorrectModifiy = false;
						boolean isCorrectType = false;

						//初期化
						fieldDefinition_sb.setLength(0);
						if (Modifier.toString(codeField.getModifiers()) != null) {
							fieldDefinition_sb.append(Modifier.toString(codeField.getModifiers()));
							fieldDefinition_sb.append(" ");
						}
						if (codeField.getType() != null) {
							fieldDefinition_sb.append(codeField.getType());
							fieldDefinition_sb.append(" ");
						}
						List<VariableDeclarator> varList = codeField.getVariables();
						for(int i_varList = 0;i_varList<varList.size();i_varList++){
							VariableDeclarator variableDeclarator = varList.get(i_varList);
							fieldDefinition_sb.append(variableDeclarator.getId().getName());

							if (i_varList < varList.size() - 1) {
								fieldDefinition_sb.append(" ");
							}
						}


						//最後の空白スペースを取り除く
						if (umlField_modify_str.endsWith(" ")) {
							umlField_modify_str = umlField_modify_str.substring(0, umlField_modify_str.lastIndexOf(" "));
						}

						isCorrectModifiy = umlField_modify_str.equals(codeField_modify_str);
						//型一致（型はソースコードに依存する、また基本型の場合も考えるようにする)
						isCorrectType = new ReferenceType(this.javaPackage,this.tableModel, umlField, fieldDefinition_sb).evaluate();

						if (isCorrectModifiy && isCorrectType){
							strList.add(fieldDefinition_sb.toString());
							itemCount++;
						}else{
							fieldError_sb.append(codeField.toString()+"<br>");
							if (isCorrectType == false) {
								fieldError_sb.append("=>"+"型が合っていません"+"<br>");
							}
							if (isCorrectModifiy == false) {
								fieldError_sb.append("=>"+"修飾子があっていません"+"<br>");
							}
						}
					}

					fieldComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
					//ボックスに一つも選択するアイテムがない場合はヒントを表示する用意をする
					if (itemCount == 0) {
						String tooltipText = "";
						tooltipText +="<html>";
						tooltipText +="<p>";
						tooltipText +=fieldError_sb.toString();
						tooltipText +="</p>";
						tooltipText +="</html>";
						fieldComboBox.setToolTipText(tooltipText);
					}
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
				}

				p.add(l);
				p.add(fieldComboBox);
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

	private String popToolTips_UML(Field umlField){
		StringBuilder popSb = new StringBuilder();
		//フィールドの説明を加える
		IComment[] comments;
		IConstraint[] constraints;
		IAttribute attr;

		//ポップアップテキストを加える
		popSb = new StringBuilder();
		popSb.append("<html>");
		popSb.append("<p>");
		popSb.append("定義:<br>");

		attr = umlField.getAttribute();
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

		return popSb.toString();
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