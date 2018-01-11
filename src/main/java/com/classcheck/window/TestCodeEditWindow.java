package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
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

import com.classcheck.panel.TestCodeTabbedPane;

public class TestCodeEditWindow extends JDialog {

	private Map<String, String> exportFileMap;
	private TestCodeTabbedPane tctp;
	private static boolean opened = false;
	private boolean canceled;
	private JButton okButton;
	private JButton cancelButton;

	public TestCodeEditWindow(Map<String, String> fileMap) {
		super((JFrame) null);
		this.exportFileMap = fileMap;
		this.opened = true;
		this.canceled = false;

		setMinimumSize(new Dimension(800, 500));
		setSize(new Dimension(1000, 1000));
		setLocationRelativeTo(null);
		setModal(true);

		initComponent();
		initEvent();

		pack();
		setVisible(true);
	}
	
	public static boolean isOpened() {
		return opened;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public Map<String, String> getExportFileMap() {
		return exportFileMap;
	}
	
	public HashMap<String, TextArea> getExportEditCodeMap() {
		return this.tctp.getExportEditCodeMap();
	}


	private void initComponent() {
		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 5));
		
		this.tctp = new TestCodeTabbedPane(exportFileMap);
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");

		buttonPane.add(okButton);
		buttonPane.add(cancelButton);

		setTitle("テストコード");
		this.setLayout(new BorderLayout());
		add(tctp,BorderLayout.CENTER);
		add(buttonPane,BorderLayout.SOUTH);
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
				super.windowClosed(e);
				opened = false;
				canceled = true;
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				opened = false;
				canceled = true;
			}
		});
	}

}
