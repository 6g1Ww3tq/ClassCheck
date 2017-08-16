package com.classcheck.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextMessageWindow extends JFrame {
	
	private JTextArea textArea;
	private JScrollPane textPane;
	
	public TextMessageWindow() {
		setLayout(new BorderLayout());
		setSize(new Dimension(500, 300));

		initComponents();
		setVisible(true);
	}
	
	private void initComponents() {
		textArea = new JTextArea();
		textPane = new JScrollPane(textArea);
		add(textPane, BorderLayout.CENTER);
	}
	
	public void setTextArea(String msg){
		textArea.setText(msg);
	}
}
