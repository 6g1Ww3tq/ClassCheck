package com.classcheck.panel;

import java.awt.FlowLayout;
import java.awt.Font;
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
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class AstahAndSourcePanel extends JPanel {
	Map<MyClass, List<JPanel>> mapPanelList;
	List<CodeVisitor> codeVisitorList;
	Map<MyClass, CodeVisitor> codeMap;

	public AstahAndSourcePanel() {
		mapPanelList = new HashMap<MyClass, List<JPanel>>();
		codeMap = new HashMap<MyClass, CodeVisitor>();

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setVisible(true);
	}

	public AstahAndSourcePanel(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList) {
		this();
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

	public void initComponent(MyClass myClass,boolean isAllChange){
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

			}else{
				p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
				l = new JLabel("該当するクラスがソースコードの中にありません");
				l.setAlignmentX(CENTER_ALIGNMENT);
				panelList.add(p);
			}


		}

		//描画
		for (JPanel panel : panelList) {
			add(panel);
		}

		DebugMessageWindow.clearText();
		for (JPanel panel : panelList) {
			System.out.println(panel);
		}
		DebugMessageWindow.msgToOutPutTextArea();
		
		//ツリーアイテムを押してもうまく表示されないので
		//常に早く表示させるよう対策
		repaint();
	}

}