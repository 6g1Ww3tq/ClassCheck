package com.classcheck.window;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.classcheck.panel.TestCodeTabbedPane;

public class TestCodeEditWindow extends JDialog {

	private Map<String, String> exportFileMap;
	private TestCodeTabbedPane tctp;
	private static boolean opened = false;
	private boolean canceled;

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

	private void initComponent() {
		this.tctp = new TestCodeTabbedPane(exportFileMap);
		
		setTitle("コンストラクタの修正");
		add(tctp);
	}

	private void initEvent() {
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
