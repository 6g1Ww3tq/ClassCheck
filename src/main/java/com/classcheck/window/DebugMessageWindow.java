package com.classcheck.window;

import static org.hamcrest.CoreMatchers.is;

import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
	static ByteArrayOutputStream baos;
	static DefaultStyledDocument doc;
	static private JTextPane textPane;
	static private JScrollPane scrollPane;
	static private StyleContext sc;
	static private boolean isDebugMode = false;

	public DebugMessageWindow(String title,boolean isDebugMode) {
		this.isDebugMode = isDebugMode;
		setSize(new Dimension(800, 600));
		initComponents();
		setTitle(title);
		setVisible(true);
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

	private void changeStream(PrintStream out){
		System.setOut(out);
	}

	public static void msgToOutPutTextArea(){
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
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		}
	}

	private static void setText(String msg, MutableAttributeSet attr) {
		try {
			doc.insertString(doc.getLength(), msg, attr);
		} catch (BadLocationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private static String getOutputMsg(){
		String msg = new String(baos.toByteArray(),StandardCharsets.UTF_8);
		baos.reset();
		return msg;
	}
}
