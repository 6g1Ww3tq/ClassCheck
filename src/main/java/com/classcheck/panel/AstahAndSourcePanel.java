package com.classcheck.panel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class AstahAndSourcePanel extends JPanel {
	Map<MyClass, List<JPanel>> mapPanelList;
	List<CodeVisitor> codeVisitorList;
	Map<MyClass, CodeVisitor> codeMap;

	private StatusBar astahAndSourceStatus;
	private SetTabPane stp;
	private boolean existSameMethod = false;

	public AstahAndSourcePanel() {
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		codeMap = new HashMap<MyClass, CodeVisitor>();
		astahAndSourceStatus = null;

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public AstahAndSourcePanel(SetTabPane stp,
			ClassBuilder cb,
			List<CodeVisitor> codeVisitorList) {
		this();
		this.stp = stp;
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
		final List<JComboBox<String>> boxList = new ArrayList<JComboBox<String>>();
		boolean isSameMethodSelected = false;

		if (isAllChange) {
			panelList.clear();

			//説明のパネルを加える
			//（左）astah	:（右)	ソースコード
			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			l = new JLabel("(左)astahのメソッド,コンストラクタのシグネチャ : (右)ソースコードのシグネチャ");
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
					l = new JLabel(method.getSignature() + " : ");
					l.setAlignmentX(CENTER_ALIGNMENT);

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
		for (int i=0; i < boxList.size() ; i++){
			box_1 = boxList.get(i);

			for(int j=0; j < boxList.size() ; j++){
				box_2 = boxList.get(j);

				if (i==j) {
					continue ;
				}

				if (box_1.getSelectedItem().equals(box_2.getSelectedItem())) {
					isSameMethodSelected = true;
					break;
				}
			}

			if (isSameMethodSelected) {
				break;
			}
		}

		//イベント登録
		//アイテムが変更された瞬間に同じメソッドが選択されているかどうかを確認する
		for (int i=0; i < boxList.size() ; i++){
			box_1 = boxList.get(i);

			box_1.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					JComboBox  box_1,box_2;
					boolean isSame = false;
					existSameMethod = false;

					for (int i=0; i < boxList.size() ; i++){
						box_1 = boxList.get(i);

						for(int j=0; j < boxList.size() ; j++){
							box_2 = boxList.get(j);

							if (i==j) {
								continue ;
							}

							if (box_1.getSelectedItem().equals(box_2.getSelectedItem())) {
								isSame = true;
								break;
							}
						}

						if (isSame) {
							break;
						}
					}

					setExsitSameMethod(isSame);
					stp.setGeneratable(myClass, !isSame);
				}
			});
		}

		return isSameMethodSelected;
	}

	private void setExsitSameMethod(boolean b){
		existSameMethod = b;

		if (astahAndSourceStatus != null) {
			if (b) {
				astahAndSourceStatus.setColor(Color.red);
				astahAndSourceStatus.setText("同じシグネチャーを選択しないでください");
			}else{
				astahAndSourceStatus.setColor(Color.green);
				astahAndSourceStatus.setText("OK");
			}
		}
	}

	public boolean getExsitSameMethod(){
		return existSameMethod;
	}

	/**
	 * パネルからもステータスのテキストを変更可能にする
	 * @param text
	 */
	public void setStatusText(String text){
		astahAndSourceStatus.setText(text);
	}

	public void setStatus(StatusBar astahAndSourceStatus) {
		this.astahAndSourceStatus = astahAndSourceStatus;
	}

}