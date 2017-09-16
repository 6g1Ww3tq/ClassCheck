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

public class SetTabPane extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AstahAndSourcePanel astahAndSourcePane;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode astahRoot;
	ClassBuilder cb;

	StatusBar astahTreeStatus;
	StatusBar astahAndSourceStatus;
	
	CompTablePane tablePane;

	public SetTabPane(AstahAndSourcePanel astahAndSourcePane,ClassBuilder cb) {
		this.astahAndSourcePane = astahAndSourcePane;
		this.cb = cb;
		this.tablePane = new CompTablePane(cb, astahAndSourcePane.getCodeVisitorList());
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		tablePane.setTableEditable(isEditable);
	}
	private void initComponent(){
		JPanel panel;
		MyClass myClass = null;
		ClassNode child = null;
		holizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		astahRoot = new DefaultMutableTreeNode("Astah");
		jtree = new JTree(astahRoot);
		jtree.setSize(new Dimension(200,200));

		for(int i=0;i<cb.getclasslistsize();i++){
			myClass = cb.getClass(i);
			child = new ClassNode(myClass);
			astahRoot.add(child);
		}

		//上の左右パネル
		//astah tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(jtree,BorderLayout.CENTER);
		astahTreeStatus = new StatusBar(panel, "Astah-Class");
		panel.add(astahTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(panel);
		treeScrollPane.setPreferredSize(new Dimension(180, 150));	

		//astah and source panel(右）
		panel = new JPanel(new BorderLayout());
		panel.add(astahAndSourcePane,BorderLayout.CENTER);
		astahAndSourceStatus = new StatusBar(panel, "Compare Astah And Your Code");
		panel.add(astahAndSourceStatus,BorderLayout.SOUTH);
		JScrollPane astahAndSourceScrollPane = new JScrollPane(panel);
		astahAndSourceScrollPane.setPreferredSize(null);	

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(astahAndSourceScrollPane);
		holizontalSplitePane.setContinuousLayout(true);

		//下のテーブル
		JScrollPane tableScrollPane = new JScrollPane(tablePane);
		tableScrollPane.setPreferredSize(null);	
		tableScrollPane.add(new StatusBar(tableScrollPane, "クラスの対応関係の設定"));
		verticalSplitePane.setTopComponent(holizontalSplitePane);
		verticalSplitePane.setBottomComponent(tableScrollPane);
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
						astahAndSourcePane.removeAll();
						astahAndSourcePane.revalidate();
						astahAndSourcePane.initComponent(myClass);
						astahTreeStatus.setText("Astah-Class:"+myClass.getName());
					}
				}
			}
		});
	}

}
