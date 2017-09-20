package com.classcheck.panel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.tree.FileTree;

public class MatcherTabbedPane extends JTabbedPane {
	SetTabPane stp;
	CompSourceTabPanel cstp;
	
	//２つのタブで共有
	ClassBuilder cb;
	List<MyClass> myClassList;
	List<CodeVisitor> codeVisitorList;
	//右上のパネル
	AstahAndSourcePanel astahAndSourcePane;
	
	//ユーザーのソースコードツリー
	FileTree userFileTree;

	public MatcherTabbedPane(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree fileTree) {
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
		this.userFileTree = fileTree;
		initComponent();
		setVisible(true);
	}

	private void initComponent(){
		astahAndSourcePane = new AstahAndSourcePanel(cb,codeVisitorList);
		 
		//２つのタブを生成
		cstp = new CompSourceTabPanel(cb,userFileTree);
		cstp.setTextAreaEditable(false);
		stp = new SetTabPane(astahAndSourcePane, cb);
		stp.setTableEditable(false);

		//２つのタブを加える
		addTab("Set", stp);
		addTab("View",cstp);
	}
	
	public AstahAndSourcePanel getAstahAndSourcePane() {
		return astahAndSourcePane;
	}
}
