package com.classcheck.window;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.classcheck.panel.ImageTabbedPanel;

public class ClassDiagramViewer extends JFrame {
	private static boolean isOpened = false;
	private String exportPath;

	public ClassDiagramViewer(String exportPath) {
		this.exportPath = exportPath;

		initComponent();
		initEvent();
	}

	private void initComponent() {
		Container container = getContentPane();
		
		System.out.println("exportPath:"+exportPath);
		container.add(new ImageTabbedPanel(exportPath));
		
		setTitle("クラス図");
		setLocationRelativeTo(null);
		setSize(new Dimension(400,400));
		setVisible(true);
	}
	
	private void initEvent() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				isOpened = true;
				super.windowOpened(e);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				isOpened = false;
				super.windowClosed(e);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				isOpened = false;
				super.windowClosing(e);
			}
		});
	}

	public static boolean isOpened() {
		return isOpened;
	}
	
}
