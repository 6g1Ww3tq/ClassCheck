package com.classcheck.panel;

import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.lucene.search.spell.LevensteinDistance;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosouce.ClassBuilder;
import com.classcheck.autosouce.Method;
import com.classcheck.autosouce.MyClass;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class AstahAndSoucePanel extends JPanel {
	List<MyClass> classList;
	List<JPanel> panelList;
	private List<CodeVisitor> codeVisitorList;
	private ClassBuilder cb;

	public AstahAndSoucePanel() {
		classList = new ArrayList<MyClass>();
		panelList = new ArrayList<JPanel>();

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));

		setVisible(true);
	}

	public AstahAndSoucePanel(ClassBuilder cb, List<CodeVisitor> codeVisitorList) {
		this();
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
	}

	public void setMyClass(MyClass myClass) {
		if(!classList.contains(myClass)){
			classList.add(myClass);
		}
	}

	public void initComponent(MyClass myClass){
		panelList.clear();

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
		CodeVisitor visitor = null;
		JLabel l = null;
		JPanel p = null;
		methodList.toArray();
		//		JComboBox<MethodDeclaration> methodComboBox = null;
		JComboBox<String> methodComboBox = null;

		int targetIndex;
		boolean isSourceClassExist = false;

		for(targetIndex = 0 ; targetIndex < codeVisitorList.size() ; targetIndex++){
			if(myClass.getName().equals(codeVisitorList.get(targetIndex).getClassName())){
				isSourceClassExist = true;
				break;
			}
		}

		//説明のパネルを加える
		//（左）astah	:（右)	ソースコード
		p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		l = new JLabel("(左)astah : (右)ソースコード");
		l.setFont(new Font("SansSerif", Font.BOLD, 20));
		l.setAlignmentX(CENTER_ALIGNMENT);
		p.add(l);
		panelList.add(p);

		if (isSourceClassExist){
			visitor = codeVisitorList.get(targetIndex);
			codeMethodList = visitor.getMethodList();
			codeConstructorList = visitor.getConstructorList();
			ArrayList<String> strList = new ArrayList<String>();

			for (MethodDeclaration methodDeclaration : codeMethodList) {
				strList.add(methodDeclaration.getDeclarationAsString());
			}

			for (ConstructorDeclaration constructorDeclaration : codeConstructorList) {
				strList.add(constructorDeclaration.getDeclarationAsString());
			}

			for (Method method : methodList) {
				//TODO
				//Adapterパターン？を使ってMethodDeclaration(finalクラス)をどうにかする
				//methodComboBox = new JComboBox<MethodDeclaration>(codeMethodList.toArray(new MethodDeclaration[codeMethodList.size()]));

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
				l = new JLabel(method.getName()+" : ");
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

		//描画
		for (JPanel panel : panelList) {
			add(panel);
		}
		
		DebugMessageWindow.clearText();
		for (JPanel panel : panelList) {
			System.out.println(panel);
		}
		DebugMessageWindow.msgToOutPutTextArea();
	}
}