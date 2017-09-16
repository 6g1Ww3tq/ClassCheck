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
	
	CompTablePane csuc;

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
		csuc = new CompTablePane(cb,codeVisitorList);

		//２つのタブを生成
		ustp = new CompSourceTabPanel(csuc,cb,userFileTree);
		astp = new SetTabPane(astahAndSourcePane, cb);

		//２つのタブを加える
		addTab("Astah", astp);
		addTab("Compare",ustp);
	}
}
