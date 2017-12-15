package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MessageDialog extends JDialog {

	private JTextArea textArea;

	public MessageDialog(String message) {
		setTitle(message);
		setLocationRelativeTo(null);
		setSize(new Dimension(400, 400));
		setLayout(new BorderLayout());
		setVisible(true);
		initComponents();
	}

	private void initComponents() {
		textArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane);
	}
	
	public void setTextArea(String text){
		this.textArea.setText(text);
	}
}
