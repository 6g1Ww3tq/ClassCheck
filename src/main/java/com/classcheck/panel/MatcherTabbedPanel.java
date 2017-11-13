package com.classcheck.panel;

import java.util.List;

import javax.swing.JTabbedPane;

import com.change_vision.jude.api.inf.model.IClass;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.tree.FileTree;

public class MatcherTabbedPanel extends JTabbedPane {
	private List<IClass> javaPackage;

	MemberTabPanel mtp;
	ViewTabPanel cstp;
	
	//２つのタブで共有
	ClassBuilder cb;
	List<MyClass> myClassList;
	List<CodeVisitor> codeVisitorList;
	//右上のパネル(フィールド)
	FieldComparePanel fcp;
	//右下のパネル(メソッド)
	MethodComparePanel mcp;
	
	//ユーザーのソースコードツリー
	FileTree userFileTree;

	public MatcherTabbedPanel(List<IClass> javaPackage, ClassBuilder cb,
			List<CodeVisitor> codeVisitorList,
			FileTree fileTree) {
		this.javaPackage = javaPackage;
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
		this.userFileTree = fileTree;
		initComponent();
		setVisible(true);
	}

	private void initComponent(){
		//フィールドとメソッドのパネル
		fcp = new FieldComparePanel(javaPackage,cb, codeVisitorList);
		mcp = new MethodComparePanel(javaPackage,cb,codeVisitorList);
		 
		//２つのタブを生成
		cstp = new ViewTabPanel(cb,userFileTree);
		cstp.setTextAreaEditable(false);
		mtp = new MemberTabPanel(fcp,mcp, cb);
		mtp.setTableEditable(false);
		
		//２つのタブを加える
		addTab("Compare", mtp);
		addTab("View",cstp);
	}
	
	public MethodComparePanel getMethodCompPane() {
		return mcp;
	}
	
	public MemberTabPanel getStp() {
		return mtp;
	}
}
