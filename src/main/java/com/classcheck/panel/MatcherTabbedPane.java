package com.classcheck.panel;

import java.util.List;

import javax.swing.JTabbedPane;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.tree.FileTree;

public class MatcherTabbedPane extends JTabbedPane {
	MethodTabPane stp;
	CompSourceTabPanel cstp;
	
	//２つのタブで共有
	ClassBuilder cb;
	List<MyClass> myClassList;
	List<CodeVisitor> codeVisitorList;
	//右上のパネル
	AstahAndSourcePanel astahAndSourcePane;
	
	//ユーザーのソースコードツリー
	FileTree userFileTree;
	private FieldTabPane ftp;

	public MatcherTabbedPane(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree fileTree) {
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
		this.userFileTree = fileTree;
		initComponent();
		setVisible(true);
	}

	private void initComponent(){
		astahAndSourcePane = new AstahAndSourcePanel(stp,cb,codeVisitorList);
		 
		//２つのタブを生成
		cstp = new CompSourceTabPanel(cb,userFileTree);
		cstp.setTextAreaEditable(false);
		stp = new MethodTabPane(astahAndSourcePane, cb);
		stp.setTableEditable(false);
//		ftp = new FieldTabPane(cb);

		//２つのタブを加える
		addTab("Method", stp);
//		addTab("Field", ftp);
		addTab("View",cstp);
	}
	
	public AstahAndSourcePanel getAstahAndSourcePane() {
		return astahAndSourcePane;
	}
	
	public MethodTabPane getStp() {
		return stp;
	}
}
