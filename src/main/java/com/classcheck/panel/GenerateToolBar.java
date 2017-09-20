package com.classcheck.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

import com.classcheck.gen.GenerateTestProgram;
import com.classcheck.tree.FileNode;
import com.classcheck.window.DebugMessageWindow;

public class GenerateToolBar extends JToolBar {
	
	Action genAction;
	ImageIcon genIcon;
	private File baseDir;
	private AstahAndSourcePanel astahAndSourcePane;

	public GenerateToolBar(String name, int operation, File baseDir,
			AstahAndSourcePanel astahAndSourcePane) {
		super(name, operation);
		
		this.baseDir = baseDir;
		this.astahAndSourcePane = astahAndSourcePane;
		setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
		add(Box.createHorizontalGlue());
		setBorder(new LineBorder(Color.LIGHT_GRAY,1));
		setPreferredSize(null);
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	private void initActionEvent() {
	}

	private void initComponent() {
		//TODO
		//ツールバーにアイコンを入れ,テストプログラムを生成するイベントも作る
		genIcon = new ImageIcon(GenerateToolBar.class.getResource("/icons/start.png"));
		genAction = new AbstractAction("genAction",genIcon) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("generate program");
				DebugMessageWindow.msgToOutPutTextArea();
				new GenerateTestProgram(baseDir,astahAndSourcePane.getMapPanelList());
			}
		};
		System.out.println("Image : " + genIcon.toString());
		DebugMessageWindow.msgToOutPutTextArea();
		add(genAction);
	}
}
