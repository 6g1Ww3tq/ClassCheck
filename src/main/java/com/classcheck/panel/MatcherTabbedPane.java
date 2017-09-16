package com.classcheck.panel;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JTabbedPane;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.tree.FileTree;

public class MatcherTabbedPane extends JTabbedPane {
	SetTabPane astp;
	CompSourceTabPanel ustp;
	
	//２つのタブで共有
	ClassBuilder cb;
	List<CodeVisitor> codeVisitorList;
	//右上のパネル
	AstahAndSourcePanel astahAndSourcePane;
	
	//ユーザーのソースコードツリー
	FileTree userFileTree;

	public MatcherTabbedPane(ClassBuilder cb,
			List<CodeVisitor> codeVisitorList, FileTree fileTree) {
		this.codeVisitorList = codeVisitorList;
		this.cb = cb;
		this.userFileTree = fileTree;
		initComponent();
		setVisible(true);
	}

	private void initComponent(){
		astahAndSourcePane = new AstahAndSourcePanel(codeVisitorList);
		 
		//２つのタブを生成
		ustp = new CompSourceTabPanel(cb,userFileTree);
		ustp.setTextAreaEditable(false);
		astp = new SetTabPane(astahAndSourcePane, cb);
		astp.setTableEditable(true);

		//２つのタブを加える
		addTab("Astah", astp);
		addTab("Compare",ustp);
	}
}
