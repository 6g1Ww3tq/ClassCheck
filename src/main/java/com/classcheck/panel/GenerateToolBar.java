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
	private MethodTabPane stp;

	public GenerateToolBar(String name, MethodTabPane stp,
			int operation, FileNode baseDir,
			AstahAndSourcePanel astahAndSourcePane) {
		super(name, operation);

		this.stp = stp;
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
				
				if (!stp.isGeneratable()) {
					JOptionPane.showMessageDialog(getParent(), "同じシグネチャーを選択しないでください", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}

				if (astahAndSourcePane.getExsitSameMethod()) {
					JOptionPane.showMessageDialog(getParent(), "同じシグネチャーを選択しないでください_2", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
				if (stp.getTablePane().isNullItemSelected()) {
					JOptionPane.showMessageDialog(getParent(), "テーブルのセルにクラスを選択してください", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}

				if(stp.getTablePane().isSameTableItemSelected()){
					JOptionPane.showMessageDialog(getParent(), "テーブルに同じクラスを選択しないでください", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
				DebugMessageWindow.msgToOutPutTextArea();
				JOptionPane.showMessageDialog(getParent(), "テストプログラムを生成しました","成功",JOptionPane.INFORMATION_MESSAGE);
				new GenerateTestProgram(baseDir,astahAndSourcePane,stp.getTablePane());
			}
		};
		System.out.println("Image : " + genIcon.toString());
		DebugMessageWindow.msgToOutPutTextArea();
		add(genAction);
	}
}
