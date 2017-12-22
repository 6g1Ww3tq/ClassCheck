package com.classcheck.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class DebugMessageWindow extends JFrame {
	static private JCheckBox debubCheckBox;
	static ByteArrayOutputStream baos;
	static DefaultStyledDocument doc;
	static private JTextPane textPane;
	static private JScrollPane scrollPane;
	static private StyleContext sc;
	static private boolean isDebugMode = false;

	public DebugMessageWindow(JCheckBox debugCheckBox, String title,boolean isDebugMode) {
		this.debubCheckBox = debugCheckBox;
		this.isDebugMode = isDebugMode;
		setSize(new Dimension(800, 600));
		initComponents();
		initEvents();
		setTitle(title);
	}

	private void initComponents() {
		baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		textPane = new JTextPane();
		sc = new StyleContext();
		doc = new DefaultStyledDocument(sc);
		textPane.setStyledDocument(doc);
		scrollPane = new JScrollPane(textPane);

		setLocationRelativeTo(null);
		add(scrollPane);
		changeStream(out);
	}

	private void initEvents() {
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				debubCheckBox.setSelected(false);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				debubCheckBox.setSelected(false);
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

	private void changeStream(PrintStream out){
		System.setOut(out);
	}

	public static void msgToTextArea(){
		if(isDebugMode){
			MutableAttributeSet attr = new SimpleAttributeSet();
			StyleConstants.setForeground(attr, Color.black);
			setText(getOutputMsg(),attr);
		}
	}

	public static void clearText(){
		if(isDebugMode){
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

		}
	}

	private static void setText(String msg, MutableAttributeSet attr) {
		try {
			doc.insertString(doc.getLength(), msg, attr);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private static String getOutputMsg(){
		String msg = new String(baos.toByteArray(),StandardCharsets.UTF_8);
		baos.reset();
		return msg;
	}
}
