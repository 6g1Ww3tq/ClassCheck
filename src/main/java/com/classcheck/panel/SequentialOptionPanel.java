package com.classcheck.panel;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SequentialOptionPanel extends JPanel {

	private String className;
	private String methodSigNature_str;
	private CheckboxGroup group;

	public SequentialOptionPanel(String className ,String methodSigNature_str) {
		this.className = className;
		this.methodSigNature_str = methodSigNature_str;
		setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));

		initComponents();
	}

	private void initComponents() {
		JLabel classLabel = new JLabel(className);
		classLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		JLabel label = new JLabel("  ::  " +  this.methodSigNature_str + "   の順番を守りますか？");
		group = new CheckboxGroup();
		Checkbox sequentialCB = new Checkbox("Yes", true, group);
		Checkbox non_sequentialCB = new Checkbox("No", false, group);

		add(classLabel);
		add(label);
		add(sequentialCB);
		add(non_sequentialCB);
	}

	public boolean yesChecked() {
		boolean checked = true;
		Checkbox box = group.getSelectedCheckbox();

		if (box.getLabel().equals("Yes")) {
			checked = true;
		} else {
			checked = false;
		}

		return checked;
	}

	public String getMethodSigNature_str() {
		return methodSigNature_str;
	}
}
