package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosouce.ClassBuilder;
import com.classcheck.autosouce.MyClass;
import com.classcheck.panel.ClassNodePanel;

public class ClassTreeWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JTree jtree;
	DefaultMutableTreeNode root;
	ClassBuilder cb;
	JTextArea textArea;
	ClassNodePanel classPanel;
	List<CodeVisitor> codeVisitorList;
	
	public ClassTreeWindow(ClassBuilder cb,List<CodeVisitor> codeVisitorList) {
		this.codeVisitorList = codeVisitorList;
		this.cb = cb;
		initComponent();
		initActionEvent();
		setSize(new Dimension(400,400));
		setVisible(true);
	}

	private void initComponent() {
		root = new DefaultMutableTreeNode("root");
		jtree = new JTree(root);
		jtree.setSize(new Dimension(200,200));
		classPanel = new ClassNodePanel(cb,codeVisitorList);
		textArea = new JTextArea(20, 20);

		MyClass myClass = null;
		ClassNode child = null;

		for(int i=0;i<cb.getclasslistsize();i++){
			myClass = cb.getClass(i);
			child = new ClassNode(myClass);
			root.add(child);
		}

		JScrollPane treeScrollPane = new JScrollPane(jtree);
	    treeScrollPane.setPreferredSize(new Dimension(180, 150));	
		JScrollPane classScrollPane = new JScrollPane(textArea);
	    classScrollPane.setPreferredSize(new Dimension(180, 150));	
		JScrollPane textScrollPane = new JScrollPane(classPanel);
	    textScrollPane.setPreferredSize(new Dimension(180, 150));	
		add(treeScrollPane,BorderLayout.WEST);
		add(textScrollPane,BorderLayout.SOUTH);
		add(classScrollPane,BorderLayout.CENTER);
	}
	
	private void initActionEvent() {
		jtree.addTreeSelectionListener(new TreeSelectionListener(){
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object obj = jtree.getLastSelectedPathComponent();
				
				if (obj instanceof ClassNode) {
					ClassNode selectedNode = (ClassNode) obj;
					Object userObj = selectedNode.getUserObject();
					if (userObj instanceof MyClass) {
						MyClass myClass = (MyClass) userObj;
						textArea.setText(myClass.toString());
						classPanel.initComponent(myClass);
					}
				}
			}
		});
	}
}
