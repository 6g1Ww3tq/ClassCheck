package com.classcheck.window;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.text.ChangedCharSetException;

import com.classcheck.autosource.ClassColorChenger;
import com.classcheck.autosource.ExportClassDiagram;
import com.classcheck.panel.ImageTabbedPanel;

public class ClassDiagramViewer extends JFrame {
	private static boolean isOpened = false;
	private ClassColorChenger ccc;
	private String exportPath;
	private ExportClassDiagram ecd;

	public ClassDiagramViewer(String exportPath, ClassColorChenger ccc, ExportClassDiagram ecd) {
		this.exportPath = exportPath;
		this.ccc = ccc;
		this.ecd = ecd;

		initComponent();
		initEvent();
	}

	private void initComponent() {
		Container container = getContentPane();
		
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
				//クローズしたらクラスの色を元の黒に戻し
				//画像フォルダ(.tmp/)を削除
				ccc.changeColor(ClassColorChenger.getDefaultColor());
				ecd.removeImages();
				super.windowClosed(e);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				isOpened = false;
				ccc.changeColor(ClassColorChenger.getDefaultColor());
				ecd.removeImages();
				super.windowClosing(e);
			}
		});
	}

	public static boolean isOpened() {
		return isOpened;
	}
	
}
