package com.classcheck.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class FileTree implements Aggregate {
	private FileNode root;
	private Pattern pattern;

	public FileTree(FileNode root,Pattern pattern){
		this.root = root;
		this.pattern = pattern;
	}

	public FileTree(FileNode root,String regex) {
		this(root,Pattern.compile(regex));
	}

	public FileNode getRoot() {
		return root;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public List<FileNode> getDirNodeList(){
		LinkedList<FileNode> dirNodeList = new LinkedList<FileNode>();
		List<FileNode> childrenNode = null;
		Iterator<FileNode> it = iterator();
		boolean dotJavaFileExsist = false;

		while (it.hasNext()) {
			FileNode fileNode = (FileNode) it.next();
			dotJavaFileExsist = false;

			if (fileNode != null) {
				if (fileNode.isDirectory()) {
					
					//ディレクトリ配下にJavaファイルが存在するかどうか調べる
					childrenNode = fileNode.getChildren();
					for (FileNode childNode : childrenNode) {
						if (childNode.getName().contains(".java")) {
							dotJavaFileExsist = true;
						}
					}
					
					//ディレクトリ配下にJavaファイルが存在しない
					if (dotJavaFileExsist == false) {
						continue;
					}
					
					
					if (dirNodeList.contains(fileNode) == false) {
						dirNodeList.add(fileNode);
					}
				}
			}
		}

		return dirNodeList;
	}

	public List<FileNode> getFileNodeList(){
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