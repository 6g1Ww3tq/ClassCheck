package com.classcheck.panel;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.classcheck.analyzer.source.CodeVisitor;
import com.github.javaparser.ast.body.ConstructorDeclaration;

public class ConstructorPane extends JPanel {

	private CodeVisitor codeVisitor;
	private ButtonGroup group;

	public ConstructorPane(CodeVisitor codeVisitor) {
		this.codeVisitor = codeVisitor;
		setName(codeVisitor.getClassName());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		initComponent();
	}

	private void initComponent() {
		this.group = new ButtonGroup();
		List<ConstructorDeclaration> methodList = codeVisitor.getConstructorList();
		JRadioButton radioButton = null;

		for(ConstructorDeclaration method : methodList){

			radioButton = new JRadioButton(method.getDeclarationAsString());
			group.add(radioButton);
			add(radioButton);
		}

	}

}
