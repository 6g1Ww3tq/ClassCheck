package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.panel.GenerateToolBar;
import com.classcheck.panel.MatcherTabbedPane;
import com.classcheck.tree.FileTree;

public class MatcherWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MatcherTabbedPane mt;
	GenerateToolBar genToolBar;
	
	private static boolean closed = true;

	public MatcherWindow(ClassBuilder cb, List<CodeVisitor> codeVisitorList,
			FileTree baseDirTree) {
		initComponent(cb,codeVisitorList,baseDirTree);

		setMinimumSize(new Dimension(700, 700));
		setSize(new Dimension(900, 900));
		//中央表示
		setLocationRelativeTo(null);
		
		addWindowListener(new WindowEventListener());
		setVisible(true);
	}

	private void initComponent(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree baseDirTree) {
		mt = new MatcherTabbedPane(cb,codeVisitorList,baseDirTree);
		
		genToolBar = new GenerateToolBar("テストプログラムの生成",mt.getStp(),JToolBar.HORIZONTAL,baseDirTree.getRoot(),mt.getMethodCompPane());
		add(genToolBar,BorderLayout.NORTH);
		add(mt,BorderLayout.CENTER);
	}
	
	public static boolean isClosed(){
		return closed;
	}
	
	private class WindowEventListener implements WindowListener{

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			closed = true;
		}

		@Override
		public void windowClosing(WindowEvent e) {
			closed = true;
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			closed = false;
		}
		
	}
}
