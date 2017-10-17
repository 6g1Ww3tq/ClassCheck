package com.classcheck.panel;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JTabbedPane;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.tree.FileTree;

public class MatcherTabbedPane extends JTabbedPane {
	MemberTabPane mtp;
	CompSourceTabPanel cstp;
	
	//２つのタブで共有
	ClassBuilder cb;
	List<MyClass> myClassList;
	List<CodeVisitor> codeVisitorList;
	//右上のパネル
	MethodCompPanel mcp;
	
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
		mcp = new MethodCompPanel(mtp,cb,codeVisitorList);
		 
		//２つのタブを生成
		cstp = new CompSourceTabPanel(cb,userFileTree);
		cstp.setTextAreaEditable(false);
		mtp = new MemberTabPane(mcp, cb);
		mtp.setTableEditable(false);

		//２つのタブを加える
		addTab("Method", mtp);
		addTab("View",cstp);
	}
	
	public MethodCompPanel getAstahAndSourcePane() {
		return mcp;
	}
	
	public MemberTabPane getStp() {
		return mtp;
	}
}
