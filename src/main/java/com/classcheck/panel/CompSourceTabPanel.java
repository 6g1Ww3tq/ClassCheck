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

public class CompSourceTabPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JSplitPane userHolizontalSplitePane;
	JSplitPane astahHolizontalSplitePane;
	JSplitPane verticalSplitePane;
	JTree userJtree;
	JTree astahJTree;
	DefaultMutableTreeNode astahRoot;
	MutableFileNode userRoot;
	JTextArea userTextArea;
	JTextArea astahTextArea;

	StatusBar userTreeStatus;
	StatusBar userSourceStatus;
	StatusBar astahTreeStatus;
	StatusBar astahSourceStatus;

	List<MyClass> myClassList;
	FileTree userFileTree;

	public CompSourceTabPanel(ClassBuilder cb, FileTree userFileTree) {
		this.myClassList = cb.getClasslist();
		this.userFileTree = userFileTree;
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTextAreaEditable(boolean isEditable){
		astahTextArea.setEditable(isEditable);
		userTextArea.setEditable(isEditable);
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
					userRoot.add(muFileNode);
				}
			}

		}

	}

	private void initComponent(){
		JPanel panel;
		ClassNode child = null;
		userHolizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		astahHolizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		userTextArea = new JTextArea(20, 20);
		astahTextArea = new JTextArea(20, 20);


		//astah tree logic
		astahRoot = new DefaultMutableTreeNode("Astah");
		astahJTree = new JTree(astahRoot);
		astahJTree.setSize(new Dimension(200,200));

		for (MyClass myClass : myClassList) {
			child = new ClassNode(myClass);
			astahRoot.add(child);
		}

		//user file tree logic
		userRoot = new MutableFileNode(userFileTree.getRoot());
		userJtree = new JTree(userRoot);
		userJtree.setSize(new Dimension(200,200));
		makeFileNodeTree();

		//上の左右パネル
		//user tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(userJtree,BorderLayout.CENTER);
		userTreeStatus = new StatusBar(panel, "Your-Class");
		panel.add(userTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(panel);
		treeScrollPane.setPreferredSize(new Dimension(180, 150));	

		//user textArea(右）
		panel = new JPanel(new BorderLayout());
		panel.add(userTextArea,BorderLayout.CENTER);
		userSourceStatus = new StatusBar(panel, "Your Source Code");
		panel.add(userSourceStatus,BorderLayout.SOUTH);
		JScrollPane textAreaScrollPane = new JScrollPane(panel);
		textAreaScrollPane.setPreferredSize(new Dimension(180, 150));	

		//左右をセット
		userHolizontalSplitePane.setLeftComponent(treeScrollPane);
		userHolizontalSplitePane.setRightComponent(textAreaScrollPane);
		userHolizontalSplitePane.setContinuousLayout(true);

		//下の左右パネル
		//astah tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(astahJTree,BorderLayout.CENTER);
		astahTreeStatus = new StatusBar(panel, "Astah-Class");
		panel.add(astahTreeStatus,BorderLayout.SOUTH);
		JScrollPane astahTreeScrollPane = new JScrollPane(panel);
		astahTreeScrollPane.setPreferredSize(new Dimension(180, 150));	

		//astah textArea(右）
		panel = new JPanel(new BorderLayout());
		panel.add(astahTextArea,BorderLayout.CENTER);
		astahSourceStatus = new StatusBar(panel, "Astah Source Code");
		panel.add(astahSourceStatus,BorderLayout.SOUTH);
		JScrollPane astahTextAreaScrollPane = new JScrollPane(panel);
		astahTextAreaScrollPane.setPreferredSize(new Dimension(180, 150));	

		//左右をセット
		astahHolizontalSplitePane.setLeftComponent(astahTreeScrollPane);
		astahHolizontalSplitePane.setRightComponent(astahTextAreaScrollPane);
		astahHolizontalSplitePane.setContinuousLayout(true);


		//アスタとユーザーソースの上下をセット
		verticalSplitePane.setTopComponent(astahHolizontalSplitePane);
		verticalSplitePane.setBottomComponent(userHolizontalSplitePane);
		verticalSplitePane.setContinuousLayout(true);
		add(verticalSplitePane,BorderLayout.CENTER);

	}

	private void initActionEvent() {
		userJtree.addTreeSelectionListener(new TreeSelectionListener(){

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object obj = userJtree.getLastSelectedPathComponent();

				if (obj instanceof MutableFileNode) {
					MutableFileNode muFileNode = (MutableFileNode) obj;
					FileNode fileNode = muFileNode.getFileNode();

					try {
						userTextArea.setText(FileUtils.readFileToString(fileNode));
						userSourceStatus.setText("Your Source Code : " + fileNode.getName());
						userTreeStatus.setText("Your-Class : " + fileNode.getName()) ;
					} catch (IOException e1) {
						DebugMessageWindow.clearText();
						System.out.println(e1);
						DebugMessageWindow.msgToOutPutTextArea();
					}
				}
			}
		});

		astahJTree.addTreeSelectionListener(new TreeSelectionListener(){

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object obj = astahJTree.getLastSelectedPathComponent();

				if (obj instanceof ClassNode) {
					ClassNode selectedNode = (ClassNode) obj;
					Object userObj = selectedNode.getUserObject();
					if (userObj instanceof MyClass) {
						MyClass myClass = (MyClass) userObj;
						astahTextArea.setText(myClass.toString());
						astahSourceStatus.setText("Astah Source Code : " + myClass.getName());
						astahTreeStatus.setText("Astah-Class : " + myClass.getName()) ;
					}
				}
			}
		});
	}

}

