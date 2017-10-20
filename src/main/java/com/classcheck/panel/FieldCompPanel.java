package com.classcheck.panel;

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

import org.apache.lucene.search.spell.LevensteinDistance;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IComment;
import com.change_vision.jude.api.inf.model.IConstraint;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Field;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.FieldDeclaration;

public class FieldCompPanel extends JPanel{

	private ClassBuilder cb;
	StatusBar fcpSourceStatus;
	private MemberTabPane mtp;
	private List<CodeVisitor> codeVisitorList;
	private HashMap<MyClass, List<JPanel>> mapPanelList;
	private HashMap<MyClass, CodeVisitor> codeMap;
	private ArrayList<JComboBox<String>> boxList;

	public FieldCompPanel(ClassBuilder cb) {
		this.cb = cb;
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		codeMap = new HashMap<MyClass, CodeVisitor>();
		fcpSourceStatus = null;

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public FieldCompPanel(MemberTabPane mtp,
			ClassBuilder cb,
			List<CodeVisitor> codeVisitorList) {
		this(cb);
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
		List<Field> fieldList = myClass.getFields();
		List<FieldDeclaration> codeFieldList = null;
		CodeVisitor codeVisitor = codeMap.get(myClass);
		JLabel l = null;
		JPanel p = null;
		JComboBox<String> fieldComboBox = null;

		//同じシグネチャーが選択されているかどうかを調べる
		boxList = new ArrayList<JComboBox<String>>();
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

				for (Field field : fieldList){

					ArrayList<String> strList = new ArrayList<String>();

					for (FieldDeclaration fieldDeclaration : codeFieldList) {
						//ソースコードのメソッドの修飾子と
						//スケルトンコードの修飾子の一致
						if (field.getModifiers().contains(
								Modifier.toString(fieldDeclaration.getModifiers())
								)){
							strList.add(fieldDeclaration.toString().replaceAll(";", ""));
						}
					}


					//TODO
					//メソッド同様に
					//修飾子が合わないやつは弾く

					popSb = new StringBuilder();

					fieldComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
					boxList.add(fieldComboBox);
					//レーベンシュタイン距離を初期化
					distance = 0;
					maxDistance = 0;
					keyStr = null;

					for (String str : strList){

						distance = levensteinAlgorithm.getDistance(field.toString(), str);
						if (maxDistance < distance) {
							maxDistance = distance;
							keyStr = str;
						}
					}

					fieldComboBox.setSelectedItem(keyStr);
					p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
					matcher = patern.matcher(field.toString());
					l = new JLabel(matcher.replaceAll("").replaceAll(";", ""));
					l.setAlignmentX(CENTER_ALIGNMENT);

					attr = field.getAttribute();
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
}