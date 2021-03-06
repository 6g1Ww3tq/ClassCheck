package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.panel.ConstructorTabbedPanel;

public class SelectConstructorViewer extends JDialog {

	private Map<CodeVisitor, String> generatedCodesMap;
	private ConstructorTabbedPanel ctp;
	private HashMap<CodeVisitor, String> constructorMap;
	private JButton okButton;
	private JButton cancelButton;
	private static boolean opened = false;
	private boolean canceled;

	public SelectConstructorViewer(Map<CodeVisitor, String> generatedCodesMap) {
		super((JFrame) null);
		this.generatedCodesMap = generatedCodesMap;
		setTitle("コンストラクタの指定");
		setMinimumSize(new Dimension(300, 300));
		setSize(new Dimension(400, 400));
		setLocationRelativeTo(null);
		initCompoenent();
		initEvent();
		setModal(true);

		pack();
		setVisible(true);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public HashMap<CodeVisitor, String> getConstructorMap() {
		return constructorMap;
	}

	public ConstructorTabbedPanel getCtp() {
		return ctp;
	}

	private void initCompoenent() {
		JPanel contentPane = (JPanel) getContentPane();
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 5));
		this.constructorMap = new HashMap<CodeVisitor, String>();
		this.ctp = new ConstructorTabbedPanel(generatedCodesMap.keySet());
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");
		this.opened = true;
		this.canceled = false;

		buttonPane.add(okButton);
		buttonPane.add(cancelButton);

		contentPane.add(ctp,BorderLayout.CENTER);
		contentPane.add(buttonPane,BorderLayout.SOUTH);

	}

	private void initEvent() {
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component c = (Component)e.getSource();
				Window w = SwingUtilities.getWindowAncestor(c);
				w.dispose();
				canceled = false;
				opened = false;
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Component c = (Component)e.getSource();
				Window w = SwingUtilities.getWindowAncestor(c);
				w.dispose();
				canceled = true;
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				opened = true;
			}

			@Override
			public void windowClosed(WindowEvent e) {
				opened = false;
				canceled = true;
				super.windowClosed(e);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				opened = false;
				canceled = true;
				super.windowClosing(e);
			}
		});
	}
}
