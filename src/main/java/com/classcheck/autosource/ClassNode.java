package com.classcheck.autosource;

import javax.swing.tree.DefaultMutableTreeNode;


public class ClassNode extends DefaultMutableTreeNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MyClass myClass;
	
	public ClassNode() {
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
