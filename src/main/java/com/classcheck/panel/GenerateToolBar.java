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
	private MemberTabPanel mtp;

	public GenerateToolBar(String name, MemberTabPanel mtp,
			int operation, FileNode baseDir) {
		super(name, operation);

		this.mtp = mtp;
		this.baseDir = baseDir;
		setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
		add(Box.createHorizontalGlue());
		setBorder(new LineBorder(Color.LIGHT_GRAY,1));
		setPreferredSize(null);
		initComponent();
		setVisible(true);
	}

	private void initComponent() {
		//ツールバーにアイコンを入れ,テストプログラムを生成するイベントも作る
		genIcon = new ImageIcon(GenerateToolBar.class.getResource("/icons/start.png"));
		genAction = new AbstractAction("genAction",genIcon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				GenerateState gs = GenerateState.NORMAL;

				if (mtp.getTablePane().isNullItemSelected()) {
					JOptionPane.showMessageDialog(getParent(), "テーブルのセルにクラスを選択してください", "error", JOptionPane.ERROR_MESSAGE);
					gs = GenerateState.TABLENULL;
					return ;
				}

				if(mtp.getTablePane().isSameTableItemSelected()){
					JOptionPane.showMessageDialog(getParent(), "テーブルに同じクラスを選択しないでください", "error", JOptionPane.ERROR_MESSAGE);
					gs = GenerateState.TABLESAMECLASS;
					return ;
				}
				
				if(!mtp.isFieldEpmty()){
					JOptionPane.showMessageDialog(getParent(), "フィールドの選択ができない空のクラスがあります", "error", JOptionPane.ERROR_MESSAGE);
					JOptionPane.showMessageDialog(getParent(), "クラス図の修飾子や型を参考にソースコードを修正してください", "info", JOptionPane.INFORMATION_MESSAGE);
					gs = GenerateState.FIELDNULL;
					return ;
				}

				if(!mtp.isMethodEmpty()){
					JOptionPane.showMessageDialog(getParent(), "メソッドの選択ができない空のクラスがあります", "error", JOptionPane.ERROR_MESSAGE);
					JOptionPane.showMessageDialog(getParent(), "クラス図のシグネチャを参考にソースコードを修正してください", "info", JOptionPane.INFORMATION_MESSAGE);
					gs = GenerateState.METHODNULL;
					return ;
				}

				if (!mtp.isFieldEpmty()) {
					JOptionPane.showMessageDialog(getParent(), "空のフィールドがあります", "error", JOptionPane.ERROR_MESSAGE);
					gs = GenerateState.FIELDNULL;
					return ;
				}

				if (!mtp.isMethodEmpty()) {
					JOptionPane.showMessageDialog(getParent(), "空のメソッドがあります", "error", JOptionPane.ERROR_MESSAGE);
					gs = GenerateState.METHODNULL;
					return ;
				}

				//すべてのクラスのフィールドを調べる
				if (!mtp.isFieldGeneratable()) {
					JOptionPane.showMessageDialog(getParent(), "同じフィールドを選択しないでください", "error", JOptionPane.ERROR_MESSAGE);
					gs = GenerateState.FIELDSAME;
					return ;
				}

				//すべてのクラスのメソッドを調べる
				if (!mtp.isMethodGeneratable()) {
					JOptionPane.showMessageDialog(getParent(), "同じメソッドを選択しないでください", "error", JOptionPane.ERROR_MESSAGE);
					gs = GenerateState.METHODSAME;
					return ;
				}

				if (gs == GenerateState.NORMAL) {
					if(new GenerateTestProgram(baseDir,mtp).doExec()){
						JOptionPane.showMessageDialog(getParent(), "テストプログラムを生成しました","成功",JOptionPane.INFORMATION_MESSAGE);
					}else{
						//JOptionPane.showMessageDialog(getParent(), "テストプログラムに失敗しました","失敗",JOptionPane.ERROR_MESSAGE);
					}
				}
				
				DebugMessageWindow.msgToTextArea();

			}
		};
		
		add(genAction);
	}
}
