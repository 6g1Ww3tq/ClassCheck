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
	private MethodCompPanel mcp;
	private MemberTabPane mtp;

	public GenerateToolBar(String name, MemberTabPane mtp,
			int operation, FileNode baseDir,
			MethodCompPanel mcp) {
		super(name, operation);

		this.mtp = mtp;
		this.baseDir = baseDir;
		this.mcp = mcp;
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

				if (mtp.getTablePane().isNullItemSelected()) {
					JOptionPane.showMessageDialog(getParent(), "テーブルのセルにクラスを選択してください", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}

				if(mtp.getTablePane().isSameTableItemSelected()){
					JOptionPane.showMessageDialog(getParent(), "テーブルに同じクラスを選択しないでください", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				
				//すべてのクラスのメソッドを調べる
				if (!mtp.isGeneratable()) {
					JOptionPane.showMessageDialog(getParent(), "同じシグネチャーを選択しないでください", "エラー", JOptionPane.ERROR_MESSAGE);
					return ;
				}

				DebugMessageWindow.msgToOutPutTextArea();
				JOptionPane.showMessageDialog(getParent(), "テストプログラムを生成しました","成功",JOptionPane.INFORMATION_MESSAGE);
				new GenerateTestProgram(baseDir,mcp,mtp.getTablePane());
			}
		};
		System.out.println("Image : " + genIcon.toString());
		DebugMessageWindow.msgToOutPutTextArea();
		add(genAction);
	}
}
