package com.classcheck.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.lucene.search.spell.LevensteinDistance;

import com.classcheck.analyzer.source.CodeVisitor;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;

public class ConstructorPanel extends JPanel {

	private CodeVisitor codeVisitor;
	private ButtonGroup group;
	private Map<JRadioButton, ConstructorDeclaration> radioConstMap;
	private Map<AbstractButton, String> abstractBtnMap;

	public ConstructorPanel(CodeVisitor codeVisitor) {
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

	public Map<AbstractButton, String> getAbstractBtnMap() {
		return abstractBtnMap;
	}

	//シーケンス図を基にモックしている部分を考えて
	//フィールドをモックする
	//そしてそれに合わせたコンストラクタのみを選択可能とする
	private void initComponent() {
		this.group = new ButtonGroup();
		this.radioConstMap = new HashMap<JRadioButton, ConstructorDeclaration>();
		this.abstractBtnMap = new HashMap<AbstractButton, String>();
		List<FieldDeclaration> fieldList = codeVisitor.getFieldList();
		List<ConstructorDeclaration> constructorList = codeVisitor.getConstructorList();
		List<Parameter> paramList;
		String[] splits;
		JRadioButton radioButton = null;
		float distance;
		float distanceMax = -1;
		String fieldParams;
		StringBuilder radioTextSB = new StringBuilder();
		LevensteinDistance ld = new LevensteinDistance();

		for(int i=0;i<fieldList.size();i++){
			FieldDeclaration field = fieldList.get(i);
			radioTextSB.append(field.toString());

			if(i < fieldList.size() - 1){
				radioTextSB.append(",");
			}
		}

		fieldParams = radioTextSB.toString();

		if (constructorList.size() > 0) {
			for(ConstructorDeclaration constructor : constructorList){
				radioTextSB = new StringBuilder();

				radioTextSB.append(constructor.getDeclarationAsString());
				radioButton = new JRadioButton(radioTextSB.toString());
				radioConstMap.put(radioButton, constructor);

				/*
				 * パラメータの型と変数名を取得するアルゴリズム
				 */
				abstractBtnMap.put((AbstractButton)radioButton, radioButton.getText());

				group.add(radioButton);
				add(radioButton);

				//デフォルトで選択するアイテムを設定(類似度)
				distance = ld.getDistance(fieldParams, constructor.getDeclarationAsString());
				if(distance > distanceMax){
					distanceMax = distance;
					group.setSelected(radioButton.getModel(), true);
				}
			}

		} else {

			radioButton = new JRadioButton("なし　（デフォルトコンストラクタ）");
			group.add(radioButton);
			add(radioButton);

		}

		group.setSelected(radioButton.getModel(), true);

	}

}
