package com.classcheck.panel.event;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.classcheck.autosource.ClassColorChenger;
import com.classcheck.autosource.ClassSearcher;
import com.classcheck.autosource.DiagramManager;
import com.classcheck.autosource.ExportClassDiagram;
import com.classcheck.autosource.MyClass;
import com.classcheck.window.ClassDiagramViewer;
import com.classcheck.window.DebugMessageWindow;

public class ClassLabelMouseAdapter extends MouseAdapter {

	private MyClass targetClass;
	private JLabel label;
	private Container parent;

	public ClassLabelMouseAdapter(MyClass targetClass,JLabel label,Container parent) {
		this.targetClass = targetClass;
		this.label = label;
		this.parent = parent;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (ClassDiagramViewer.isOpened()) {
			JOptionPane.showMessageDialog(parent, "クラス図のイメージウィンドウを閉じてください", "info", JOptionPane.INFORMATION_MESSAGE);
			return ;
		}

		System.out.println("@@@@Diagrams@@@");
		DiagramManager dm = new DiagramManager();
		ClassSearcher cav;
		ClassColorChenger ccc;
		ExportClassDiagram ecd;
		ClassDiagramViewer cdv;
		List<IClassDiagram> allDiagramList = dm.getClassDiagram();
		String projectPath = dm.getProjectPath();
		List<IClassDiagram> findDiagramList;

		cav = new ClassSearcher(allDiagramList);
		findDiagramList = cav.findIClassDiagram(targetClass.getIClass());

		ccc = new ClassColorChenger(targetClass.getIClass());
		ccc.changeColor("#BE850F");

		ecd = new ExportClassDiagram(findDiagramList,projectPath);
		ecd.removeDirectory(ecd.getExportPath());
		ecd.exportImages();

		cdv = new ClassDiagramViewer(ecd.getExportPath(),ccc,ecd);

		System.out.println(findDiagramList.toString());
		DebugMessageWindow.msgToTextArea();

		super.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		label.setForeground(Color.red);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		label.setForeground(Color.black);
	}
}
