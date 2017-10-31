package com.classcheck.panel;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.lucene.search.spell.LevensteinDistance;

import com.classcheck.analyzer.source.CodeVisitor;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

public class ConstructorPane extends JPanel {

	private CodeVisitor codeVisitor;
	private ButtonGroup group;

	public ConstructorPane(CodeVisitor codeVisitor) {
		this.codeVisitor = codeVisitor;
		setName(codeVisitor.getClassName());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		initComponent();
	}
	
	public ButtonGroup getGroup() {
		return group;
	}
	
	public CodeVisitor getCodeVisitor() {
		return codeVisitor;
	}

	private void initComponent() {
		this.group = new ButtonGroup();
		List<FieldDeclaration> fieldList = codeVisitor.getFieldList();
		List<ConstructorDeclaration> methodList = codeVisitor.getConstructorList();
		JRadioButton radioButton = null;
		float distance;
		float distanceMax = -1;
		String fieldParams;
		StringBuilder sb = new StringBuilder();
		LevensteinDistance ld = new LevensteinDistance();
		
		for(int i=0;i<fieldList.size();i++){
			FieldDeclaration field = fieldList.get(i);
			sb.append(field.toString());
			
			if(i < fieldList.size() - 1){
				sb.append(",");
			}
		}
			
		fieldParams = sb.toString();

		for(ConstructorDeclaration method : methodList){
			radioButton = new JRadioButton(method.getDeclarationAsString());
			group.add(radioButton);
			add(radioButton);

			//デフォルトで選択するアイテムを設定(類似度)
			distance = ld.getDistance(fieldParams, method.getDeclarationAsString());
			if(distance > distanceMax){
				distanceMax = distance;
				group.setSelected(radioButton.getModel(), true);
			}
		}
		
		group.setSelected(radioButton.getModel(), true);
		
	}

}
