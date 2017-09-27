package com.classcheck.panel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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

	public GenerateToolBar(String name,
			int operation, FileNode baseDir,
			AstahAndSourcePanel astahAndSourcePane) {
		super(name, operation);

		this.baseDir = baseDir;
		this.astahAndSourcePane = astahAndSourcePane;
		setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
		add(Box.createHorizontalGlue());
		setBorder(new LineBorder(Color.LIGHT_GRAY,1));
		setPreferredSize(null);
		initComponent();
		setVisible(true);
	}

	private void initComponent() {
		//TODO
		//ツールバーにアイコンを入れ,テストプログラムを生成するイベントも作る
		genIcon = new ImageIcon(GenerateToolBar.class.getResource("/icons/start.png"));
		genAction = new AbstractAction("genAction",genIcon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("generate program");
				if (!SetTabPane.isGeneratable()) {
					JOptionPane.showMessageDialog(getParent(), "同じシグネチャーを選択しないでください");
					return ;
				}

				if (AstahAndSourcePanel.getExsitSameMethod()) {
					JOptionPane.showMessageDialog(getParent(), "同じシグネチャーを選択しないでください_2");
					return ;
				}

				if(CompTablePane.isSameTableItemSelected()){
					JOptionPane.showMessageDialog(getParent(), "テーブルに同じクラスを選択しないでください");
					return ;
				}
				
				DebugMessageWindow.msgToOutPutTextArea();
				new GenerateTestProgram(baseDir,astahAndSourcePane);
			}
		};
		System.out.println("Image : " + genIcon.toString());
		DebugMessageWindow.msgToOutPutTextArea();
		add(genAction);
	}
}
