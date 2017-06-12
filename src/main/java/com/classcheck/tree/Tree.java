package com.classcheck.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Tree implements Aggregate {
	private FileNode root;
	private Pattern pattern;
	
	public Tree(FileNode root,Pattern pattern){
		this.root = root;
		this.pattern = pattern;
	}

	public Tree(FileNode root,String regex) {
		this(root,Pattern.compile(regex));
	}

	public FileNode getRoot() {
		return root;
	}
	
	public Pattern getPattern() {
		return pattern;
	}

	public List<FileNode> getResultList(){
		LinkedList<FileNode> list = new LinkedList<FileNode>();
		Iterator<FileNode> it = iterator();

		while (it.hasNext()) {
			FileNode fileNode = (FileNode) it.next();

			if (fileNode != null) {
				list.add(fileNode);
			}
		}
		
		return list;
	}

	@Override
	public Iterator<FileNode> iterator() {
		return new TreeItemIterator(this);
	}

}