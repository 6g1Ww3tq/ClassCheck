package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.ClassNode;
import com.classcheck.autosource.MyClass;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.FileTree;
import com.classcheck.window.DebugMessageWindow;

public class UserSourceTabPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode root;
	JTextArea textArea;

	StatusBar userTreeStatus;
	StatusBar userSourceStatus;
	StatusBar astahAndUserSouceStatus;

	CompAstahUserClass csuc;
	FileTree userFileTree;

	public UserSourceTabPanel(CompAstahUserClass csuc, FileTree userFileTree) {
		this.csuc = csuc;
		this.userFileTree = userFileTree;
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	private void makeFileNodeTree(){
		Iterator<FileNode> it = userFileTree.iterator();
		FileNode fileNode;
		MutableFileNode muFileNode;
		while (it.hasNext()) {
			fileNode = (FileNode) it.next();

			if (fileNode!=null) {
				if (fileNode.isDirectory()) {

				}else if(fileNode.isFile()){
					muFileNode = new MutableFileNode(fileNode);
					root.add(muFileNode);
				}
			}

		}

	}

	private void initComponent(){
		JPanel statusPanel;

		root = new DefaultMutableTreeNode(userFileTree.getRoot().getName());
		jtree = new JTree(root);
		jtree.setSize(new Dimension(200,200));
		textArea = new JTextArea(20, 20);
		holizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		makeFileNodeTree();

		//上の左右パネル
		statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(jtree,BorderLayout.CENTER);
		userTreeStatus = new StatusBar(statusPanel, "Your-Class");
		statusPanel.add(userTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(statusPanel);
		treeScrollPane.setPreferredSize(new Dimension(180, 150));	

		statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(textArea,BorderLayout.CENTER);
		userSourceStatus = new StatusBar(statusPanel, "Your Source Code");
		statusPanel.add(userSourceStatus,BorderLayout.SOUTH);
		JScrollPane textAreaScrollPane = new JScrollPane(statusPanel);
		textAreaScrollPane.setPreferredSize(new Dimension(180, 150));	

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(textAreaScrollPane);
		holizontalSplitePane.setContinuousLayout(true);
		add(holizontalSplitePane,BorderLayout.NORTH);

		//下の設定パネル
		statusPanel = new JPanel(new BorderLayout());
		statusPanel.add(csuc,BorderLayout.CENTER);
		astahAndUserSouceStatus = new StatusBar(statusPanel, "Match Astah Class Your Source Code");
		statusPanel.add(astahAndUserSouceStatus,BorderLayout.SOUTH);
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

				if (obj instanceof MutableFileNode) {
					MutableFileNode muFileNode = (MutableFileNode) obj;
					FileNode fileNode = muFileNode.getFileNode();

					try {
						textArea.setText(FileUtils.readFileToString(fileNode));
						userSourceStatus.setText("Your Source Code : " + fileNode.getName());
					} catch (IOException e1) {
						DebugMessageWindow.clearText();
						System.out.println(e1);
						DebugMessageWindow.msgToOutPutTextArea();
					}
				}
			}
		});
	}

}

