package com.classcheck.window;

import javax.swing.tree.DefaultMutableTreeNode;

import com.classcheck.autosouce.MyClass;

public class ClassNode extends DefaultMutableTreeNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MyClass myClass;
	
	public ClassNode() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public ClassNode(MyClass myclass) {
		super(myclass);
		this.myClass = myclass;
	}

	@Override
	public String toString() {
		return myClass.getName();
	}
}
