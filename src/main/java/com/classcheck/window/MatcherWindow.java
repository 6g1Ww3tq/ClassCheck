package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.AstahAndSourcePanel;
import com.classcheck.panel.GenerateToolBar;
import com.classcheck.panel.MatcherTabbedPane;
import com.classcheck.panel.StatusBar;
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
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponent(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree baseDirTree) {
		mt = new MatcherTabbedPane(cb,codeVisitorList,baseDirTree);
		genToolBar = new GenerateToolBar("テストプログラムの生成",JToolBar.HORIZONTAL,baseDirTree.getRoot());
		add(genToolBar,BorderLayout.NORTH);
		add(mt,BorderLayout.CENTER);
	}
}
