package com.classcheck.window;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.classcheck.autosource.ClassColorChenger;
import com.classcheck.autosource.ExportDiagram;
import com.classcheck.autosource.SequenceSearcher;
import com.classcheck.panel.Class_Sequence_ImageTabbedPanel;

public class Class_Sequence_DiagramViewer extends JFrame {
	private static boolean isOpened = false;
	private ClassColorChenger ccc;
	private SequenceSearcher ss;
	private String exportPath;
	private ExportDiagram ecd;
	private ArrayList<String> classDiagramNameList;

	public Class_Sequence_DiagramViewer(String exportPath, ClassColorChenger ccc, SequenceSearcher ss, ExportDiagram ecd, List<IClassDiagram> findClassDiagramList) {
		this.exportPath = exportPath;
		this.ccc = ccc;
		this.ss = ss;
		this.ecd = ecd;
		this.classDiagramNameList = new ArrayList<String>();
		
		for (IClassDiagram iClassDiagram : findClassDiagramList) {
			classDiagramNameList.add(iClassDiagram.getName());
		}

		initComponent();
		initEvent();
	}

	private void initComponent() {
		Container container = getContentPane();
		Class_Sequence_ImageTabbedPanel tabPanel = new Class_Sequence_ImageTabbedPanel(this,exportPath,this.classDiagramNameList);
		
		container.add(tabPanel);
		
		setLocationRelativeTo(null);
		setSize(new Dimension(1000,650));
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
				ss.refleshChangedMessages();
				ecd.removeImages();
				super.windowClosed(e);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				isOpened = false;
				ccc.changeColor(ClassColorChenger.getDefaultColor());
				ss.refleshChangedMessages();
				ecd.removeImages();
				super.windowClosing(e);
			}
		});
	}

	public static boolean isOpened() {
		return isOpened;
	}
	
}
