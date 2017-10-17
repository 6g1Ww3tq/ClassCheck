package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

	public MatcherWindow(ClassBuilder cb, List<CodeVisitor> codeVisitorList,
			FileTree baseDirTree) {
		initComponent(cb,codeVisitorList,baseDirTree);
		setSize(new Dimension(500,500));
		//中央表示
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponent(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree baseDirTree) {
		mt = new MatcherTabbedPane(cb,codeVisitorList,baseDirTree);
		
		genToolBar = new GenerateToolBar("テストプログラムの生成",mt.getStp(),JToolBar.HORIZONTAL,baseDirTree.getRoot(),mt.getMethodCompPane());
		add(genToolBar,BorderLayout.NORTH);
		add(mt,BorderLayout.CENTER);
	}
}
