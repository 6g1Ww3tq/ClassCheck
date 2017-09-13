package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import com.classcheck.autosouce.ClassBuilder;
import com.classcheck.autosouce.MyClass;
import com.classcheck.panel.AstahAndSoucePanel;
import com.classcheck.panel.StatuBar;

public class ClassTreeWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//左:Tree,右:astahとソースコード
	JSplitPane holizontalSplitePane;
	//上:Tree,astahとソースコード,下:ソースコード
	JSplitPane verticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode root;
	ClassBuilder cb;
	JTextArea textArea;
	AstahAndSoucePanel astahAndSoucePanel;
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
		astahAndSoucePanel = new AstahAndSoucePanel(cb,codeVisitorList);
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
		
//		JScrollPane treeScrollPane = new JScrollPane(jtree);
//	    treeScrollPane.setPreferredSize(new Dimension(180, 150));	
//		JScrollPane textAreaScrollPane = new JScrollPane(textArea);
//	    textAreaScrollPane.setPreferredSize(new Dimension(180, 150));	
//	    textAreaScrollPane.add(new StatuBar(textAreaScrollPane, "souce code from astah"));
//		JScrollPane astahSourceScrollPane = new JScrollPane(astahAndSoucePanel);
//	    astahSourceScrollPane.setPreferredSize(new Dimension(180, 150));	
//	    astahSourceScrollPane.add(new StatuBar(astahSourceScrollPane, "astahとソースコードの設定"));
//	    holizontalSplitePane.setLeftComponent(treeScrollPane);
//	    holizontalSplitePane.setRightComponent(astahSourceScrollPane);
//	    holizontalSplitePane.setContinuousLayout(true);
//	    add(holizontalSplitePane,BorderLayout.NORTH);
//	    verticalSplitePane.setTopComponent(holizontalSplitePane);
//	    verticalSplitePane.setBottomComponent(textAreaScrollPane);
//	    verticalSplitePane.setContinuousLayout(true);
//		add(verticalSplitePane,BorderLayout.CENTER);

		JPanel p;

		p = new JPanel(new BorderLayout());
		p.add(jtree,BorderLayout.CENTER);
		p.add(new StatuBar(p, "Astah"),BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(p);
	    treeScrollPane.setPreferredSize(new Dimension(180, 150));	

		p = new JPanel(new BorderLayout());
		p.add(textArea,BorderLayout.CENTER);
		p.add(new StatuBar(p, "souce code from astah"),BorderLayout.SOUTH);
		JScrollPane textAreaScrollPane = new JScrollPane(p);
	    textAreaScrollPane.setPreferredSize(new Dimension(180, 150));	

		p = new JPanel(new BorderLayout());
		p.add(astahAndSoucePanel,BorderLayout.CENTER);
		p.add(new StatuBar(p, "souce code from astah"),BorderLayout.SOUTH);
		JScrollPane astahSourceScrollPane = new JScrollPane(p);

	    astahSourceScrollPane.setPreferredSize(new Dimension(180, 150));	
	    astahSourceScrollPane.add(new StatuBar(astahSourceScrollPane, "astahとソースコードの設定"));
	    holizontalSplitePane.setLeftComponent(treeScrollPane);
	    holizontalSplitePane.setRightComponent(astahSourceScrollPane);
	    holizontalSplitePane.setContinuousLayout(true);
	    add(holizontalSplitePane,BorderLayout.NORTH);
	    verticalSplitePane.setTopComponent(holizontalSplitePane);
	    verticalSplitePane.setBottomComponent(textAreaScrollPane);
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
						astahAndSoucePanel.removeAll();
						astahAndSoucePanel.revalidate();
						astahAndSoucePanel.initComponent(myClass);
					}
				}
			}
		});
		
	}
}
