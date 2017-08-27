package com.classcheck.panel;

import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosouce.ClassBuilder;
import com.classcheck.autosouce.Method;
import com.classcheck.autosouce.MyClass;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ClassNodePanel extends JPanel {
	List<MyClass> classList;
	List<JPanel> panelList;
	private List<CodeVisitor> codeVisitorList;
	private ClassBuilder cb;

	public ClassNodePanel() {
		classList = new ArrayList<MyClass>();
		panelList = new ArrayList<JPanel>();

		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));

		setVisible(true);
	}

	public ClassNodePanel(ClassBuilder cb, List<CodeVisitor> codeVisitorList) {
		this();
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
	}

	public void setMyClass(MyClass myClass) {
		if(!classList.contains(myClass)){
			classList.add(myClass);
		}
	}

	public void initComponent(){
		super.removeAll();
	}

	public void initComponent(MyClass myClass){
		super.removeAll();
		panelList.clear();

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
		boolean isClassExist = false;

		for(targetIndex = 0 ; targetIndex < codeVisitorList.size() ; targetIndex++){
			if(myClass.getName().equals(codeVisitorList.get(targetIndex).getClassName())){
				isClassExist = true;
				break;
			}
		}

		if (isClassExist) {
			visitor = codeVisitorList.get(targetIndex);
			codeMethodList = visitor.getMethodList();
			codeConstructorList = visitor.getConstructorList();
			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
			l = new JLabel(visitor.getClassName());
			l.setFont(new Font("SansSerif", Font.BOLD, 20));
			l.setAlignmentX(LEFT_ALIGNMENT);

			p.add(l);

			ArrayList<String> strList = new ArrayList<String>();

			for (MethodDeclaration methodDeclaration : codeMethodList) {
				strList.add(methodDeclaration.getDeclarationAsString());
			}
			
			for (ConstructorDeclaration constructorDeclaration : codeConstructorList) {
				strList.add(constructorDeclaration.getNameExpr().toString());
			}

			for (Method method : methodList) {
				//TODO
				//Adapterパターン？を使ってMethodDeclaration(finalクラス)をどうにかする
				//methodComboBox = new JComboBox<MethodDeclaration>(codeMethodList.toArray(new MethodDeclaration[codeMethodList.size()]));
				methodComboBox = new JComboBox<String>(strList.toArray(new String[strList.size()]));
				p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
				l = new JLabel(method.getName()+" : ");
				l.setAlignmentX(CENTER_ALIGNMENT);

				p.add(l);
				p.add(methodComboBox);
				panelList.add(p);
			}

			for (JPanel panel : panelList) {
				add(panel);
			}


		}
	}
}