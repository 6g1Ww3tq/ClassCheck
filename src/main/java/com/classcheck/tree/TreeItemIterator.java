package com.classcheck.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreeItemIterator implements Iterator<FileNode> {
	private FileNode root;
	private LinkedList<FileNode> stack;
	private Pattern pattern;

	public TreeItemIterator(Tree tree) {
		this.root = tree.getRoot();
		this.stack = new LinkedList<FileNode>();
		stack.push(root);
		this.pattern = tree.getPattern();
	}

	public boolean hasNext() {
		return !stack.isEmpty();
	}

	public FileNode next() {
		FileNode fileNode = stack.pop();
		List<FileNode> children = fileNode.getChildren();
		Matcher matcher = null;

		//childrenがnullの時は空
		if (children != null) {
			for (FileNode child : children) {
				stack.addLast(child);
			}
		}

		//fileNodeが特定のファイル拡張子と一致した場合のみ返却する
		matcher = pattern.matcher(fileNode.getName());
		if (matcher.find()) {
			return fileNode;
		}else{
			return null;
		}
	}

	public Iterator<FileNode> iterator() {
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}