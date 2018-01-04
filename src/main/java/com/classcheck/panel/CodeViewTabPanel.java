package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.ClassNode;
import com.classcheck.autosource.MyClass;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.FileTree;
import com.classcheck.window.DebugMessageWindow;

public class CodeViewTabPanel extends JPanel{
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
	RSyntaxTextArea userEditor;
	RSyntaxTextArea skeltonEditor;

	StatusBar userTreeStatus;
	StatusBar userSourceStatus;
	StatusBar astahTreeStatus;
	StatusBar astahSourceStatus;

	List<MyClass> myClassList;
	FileTree userFileTree;

	public CodeViewTabPanel(ClassBuilder cb, FileTree userFileTree) {
		this.myClassList = cb.getClasslist();
		this.userFileTree = userFileTree;
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTextAreaEditable(boolean isEditable){
		skeltonEditor.setEditable(isEditable);
		userEditor.setEditable(isEditable);
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
		RTextScrollPane rScrollPane = null;
		userHolizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		astahHolizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		userEditor = new RSyntaxTextArea(20, 60);
		skeltonEditor = new RSyntaxTextArea(20, 60);
		userEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		userEditor.setBracketMatchingEnabled(false);
		userEditor.setCodeFoldingEnabled(true);
		skeltonEditor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		skeltonEditor.setCodeFoldingEnabled(true);
		skeltonEditor.setBracketMatchingEnabled(false);


		//astah tree logic
		astahRoot = new DefaultMutableTreeNode("SkeltonCode");
		astahJTree = new JTree(astahRoot);
		astahJTree.setMinimumSize(new Dimension(200,200));
		astahJTree.setSize(new Dimension(200,200));

		for (MyClass myClass : myClassList) {
			child = new ClassNode(myClass);
			astahRoot.add(child);
		}

		//user file tree logic
		userRoot = new MutableFileNode(userFileTree.getRoot());
		userJtree = new JTree(userRoot);
		userJtree.setMinimumSize(new Dimension(200, 200));
		userJtree.setSize(new Dimension(200,200));
		makeFileNodeTree();

		//上の左右パネル
		//user tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(userJtree,BorderLayout.CENTER);
		userTreeStatus = new StatusBar(panel, "Your-Class");
		panel.add(userTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(panel);
		treeScrollPane.setMinimumSize(new Dimension(180, 150));	
		treeScrollPane.setSize(new Dimension(180, 150));	

		//user textArea(右）
		rScrollPane = new RTextScrollPane(userEditor);
		rScrollPane.setLineNumbersEnabled(true);
		panel = new JPanel(new BorderLayout());
		panel.add(rScrollPane,BorderLayout.CENTER);
		userSourceStatus = new StatusBar(panel, "Your Source Code");
		panel.add(userSourceStatus,BorderLayout.SOUTH);

		//左右をセット
		userHolizontalSplitePane.setLeftComponent(treeScrollPane);
		userHolizontalSplitePane.setRightComponent(panel);
		userHolizontalSplitePane.setContinuousLayout(true);

		//下の左右パネル
		//astah tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(astahJTree,BorderLayout.CENTER);
		astahTreeStatus = new StatusBar(panel, "SkeltonCode-Class");
		panel.add(astahTreeStatus,BorderLayout.SOUTH);
		JScrollPane astahTreeScrollPane = new JScrollPane(panel);
		astahTreeScrollPane.setMinimumSize(new Dimension(180, 150));	
		astahTreeScrollPane.setSize(new Dimension(180, 150));	

		//astah textArea(右）
		rScrollPane = new RTextScrollPane(skeltonEditor);
		rScrollPane.setLineNumbersEnabled(true);
		panel = new JPanel(new BorderLayout());
		panel.add(rScrollPane,BorderLayout.CENTER);
		astahSourceStatus = new StatusBar(panel, "Skelton Code");
		panel.add(astahSourceStatus,BorderLayout.SOUTH);

		//左右をセット
		astahHolizontalSplitePane.setLeftComponent(astahTreeScrollPane);
		astahHolizontalSplitePane.setRightComponent(panel);
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
						userEditor.setText(FileUtils.readFileToString(fileNode));
						userSourceStatus.setText("Your Source Code : " + fileNode.getName());
						userTreeStatus.setText("Your-Class : " + fileNode.getName()) ;
					} catch (IOException e1) {
						DebugMessageWindow.clearText();
						System.out.println(e1);
						DebugMessageWindow.msgToTextArea();
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
						skeltonEditor.setText(myClass.toString());
						astahSourceStatus.setText("Skelton Code : " + myClass.getName());
						astahTreeStatus.setText("Skelton-Class : " + myClass.getName()) ;
					}
				}
			}
		});
	}

}

