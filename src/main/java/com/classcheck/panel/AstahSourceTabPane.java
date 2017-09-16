package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

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
import com.classcheck.autosource.ClassNode;
import com.classcheck.autosource.MyClass;

public class AstahSourceTabPane extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AstahAndSourcePanel astahAndSourcePane;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode root;
	ClassBuilder cb;
	JTextArea textArea;

	StatusBar astahStatus;
	StatusBar astahSourceStatus;
	StatusBar astahAndSourceStatus;

	public AstahSourceTabPane(AstahAndSourcePanel astahAndSourcePane,ClassBuilder cb) {
		this.astahAndSourcePane = astahAndSourcePane;
		this.cb = cb;
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	private void initComponent(){
		JPanel statusPanel;

		root = new DefaultMutableTreeNode("Astah");
		jtree = new JTree(root);
		jtree.setSize(new Dimension(200,200));
		textArea = new JTextArea(20, 20);
		holizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		MyClass myClass = null;
		ClassNode child = null;

		for(int i=0;i<cb.getclasslistsize();i++){
			myClass = cb.getClass(i);
			child = new ClassNode(myClass);
			root.add(child);
		}

		//上の左右パネル
		statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(jtree,BorderLayout.CENTER);
		astahStatus = new StatusBar(statusPanel, "Astah-Class");
		statusPanel.add(astahStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(statusPanel);
		treeScrollPane.setPreferredSize(new Dimension(180, 150));	

		statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(textArea,BorderLayout.CENTER);
		astahSourceStatus = new StatusBar(statusPanel, "Souce Code From Astah");
		statusPanel.add(astahSourceStatus,BorderLayout.SOUTH);
		JScrollPane textAreaScrollPane = new JScrollPane(statusPanel);
		textAreaScrollPane.setPreferredSize(new Dimension(180, 150));	

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(textAreaScrollPane);
		holizontalSplitePane.setContinuousLayout(true);
		add(holizontalSplitePane,BorderLayout.NORTH);

		//下の設定パネル
		statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(astahAndSourcePane,BorderLayout.CENTER);
		astahAndSourceStatus = new StatusBar(statusPanel, "Compare Astah And Your Code");
		statusPanel.add(astahAndSourceStatus,BorderLayout.SOUTH);
		JScrollPane astahSourceScrollPane = new JScrollPane(statusPanel);

		astahSourceScrollPane.setPreferredSize(new Dimension(180, 150));	
		astahSourceScrollPane.add(new StatusBar(astahSourceScrollPane, "astahとソースコードの設定"));
		verticalSplitePane.setTopComponent(holizontalSplitePane);
		verticalSplitePane.setBottomComponent(astahSourceScrollPane);
		verticalSplitePane.setContinuousLayout(true);
		add(verticalSplitePane,BorderLayout.CENTER);

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
						astahAndSourcePane.removeAll();
						astahAndSourcePane.revalidate();
						astahAndSourcePane.initComponent(myClass);

						astahSourceStatus.setText("Astah-Class-Source : "+myClass.getName());
						astahStatus.setText("Astah-Class:"+myClass.getName());
					}
				}
			}
		});
	}

}
