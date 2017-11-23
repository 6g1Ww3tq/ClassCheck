package com.classcheck.window;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class TextMessageWindow extends JFrame {
	
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private StyleContext sc;
	private DefaultStyledDocument doc;
	
	public TextMessageWindow() {
		setSize(new Dimension(800, 600));

		initComponents();
		setVisible(true);
	}
	
	protected void initComponents() {
		textPane = new JTextPane();
		sc = new StyleContext();
		doc = new DefaultStyledDocument(sc);
		textPane.setStyledDocument(doc);
		scrollPane = new JScrollPane(textPane);
		
		setLocationRelativeTo(null);
		add(scrollPane);
	}
	
	public void changeStyle(int start,int end){
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setBold(attr, true);
		
		doc.setCharacterAttributes(start, end, attr, true);
	}
	
	public void appendText(String msg,MutableAttributeSet attr){
		try {
			doc.insertString(doc.getLength(), msg, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
