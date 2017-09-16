package com.classcheck.panel;

import javax.swing.tree.DefaultMutableTreeNode;

import com.classcheck.tree.FileNode;

public class MutableFileNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	FileNode fileNode;
	
	public MutableFileNode(FileNode fileNode) {
		super(fileNode);
		this.fileNode = fileNode;
	}

	@Override
	public String toString() {
		return fileNode.getName();
	}
	
	public FileNode getFileNode() {
		return fileNode;
	}
}
