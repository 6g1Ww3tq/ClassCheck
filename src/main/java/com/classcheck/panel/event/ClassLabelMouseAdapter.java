package com.classcheck.panel.event;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.classcheck.autosource.ClassColorChenger;
import com.classcheck.autosource.ClassSearcher;
import com.classcheck.autosource.DiagramManager;
import com.classcheck.autosource.ExportDiagram;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.SequenceSearcher;
import com.classcheck.window.Class_Sequence_DiagramViewer;
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

		if (Class_Sequence_DiagramViewer.isOpened()) {
			JOptionPane.showMessageDialog(parent, "UML図のイメージウィンドウを閉じてください", "info", JOptionPane.INFORMATION_MESSAGE);
			return ;
		}

		System.out.println("@@@@Diagrams@@@");
		Object obj = e.getSource();
		JLabel clickedLabel = null;
		DiagramManager dm = new DiagramManager();
		ClassSearcher cs;
		SequenceSearcher ss = null;
		ClassColorChenger ccc;
		ExportDiagram ecd;
		Class_Sequence_DiagramViewer cdv;
		List<IClassDiagram> allIClassDiagram = dm.getAllClassDiagramList();
		List<IClassDiagram> findClassDiagramList;
		List<ISequenceDiagram> allISequenceDiagram = dm.getAllISequenceDiagramList();
		List<ISequenceDiagram> findSequenceDiagramList = null;
		String projectPath = dm.getProjectPath();
		
		if (obj instanceof JLabel) {
			clickedLabel = (JLabel) obj;
			ss = new SequenceSearcher(allISequenceDiagram,"#672A92");
			findSequenceDiagramList = ss.findISequenceDiagram(targetClass.getIClass(),clickedLabel);
		}

		cs = new ClassSearcher(allIClassDiagram);
		findClassDiagramList = cs.findIClassDiagram(targetClass.getIClass());

		ccc = new ClassColorChenger(targetClass.getIClass());
		ccc.changeColor("#672A92");

		ecd = new ExportDiagram(findClassDiagramList,findSequenceDiagramList,projectPath);
		ecd.removeDirectory(ecd.getExportPath());
		ecd.exportImages();

		cdv = new Class_Sequence_DiagramViewer(ecd.getExportPath(),ccc,ss,ecd,findClassDiagramList);

		System.out.println(findClassDiagramList.toString());
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
