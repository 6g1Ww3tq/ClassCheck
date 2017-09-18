package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.AstahAndSourcePanel;
import com.classcheck.panel.MatcherTabbedPane;
import com.classcheck.panel.StatusBar;
import com.classcheck.tree.FileTree;

public class MatcherWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MatcherWindow(ClassBuilder cb, List<CodeVisitor> codeVisitorList,
			FileTree fileTree) {
		initComponent(cb,codeVisitorList,fileTree);
		setSize(new Dimension(500,500));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initComponent(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree fileTree) {
		add(new MatcherTabbedPane(cb,codeVisitorList,fileTree));
	}
}
