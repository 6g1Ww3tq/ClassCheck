package com.classcheck.panel;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class StatusBar extends JPanel {
	Component parentFrame;
	JLabel statusLabel;
	
	public StatusBar(Component parentComp) {
		this.parentFrame = parentComp;
		initLayout();
		initComponent();
		setVisible(true);
	}
	
	public StatusBar(Component parentComp,String text) {
		this(parentComp);
		setText(text);
	}
	
	private void initComponent() {
		statusLabel = new JLabel("default");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		add(statusLabel);
	}

	private void initLayout() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));		
		setPreferredSize(new Dimension(parentFrame.getWidth(), 16));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	public void setText(String text){
		statusLabel.setText(text);
	}
}
