package com.classcheck.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.classcheck.type.FILETYPE;

public class FileNode extends File{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FILETYPE type;

	public FileNode(String pathname) {
		super(pathname);
		setFileType();
	}

	public FileNode(File file) {
		super(file.toString());
		setFileType();
	}

	private void setFileType() {
		if(this.isDirectory()){
			type = FILETYPE.FOLDER;
		}else{
			type = FILETYPE.FILE;
		}
	}

	public FILETYPE getType() {
		return type;
	}

	public List<FileNode> getChildren() {
		File[] files = null;
		List<FileNode> fileNodes = null;

		if (!this.isDirectory()) {
			return null;
		}

		files = this.listFiles();

		if (files == null) {
			return null;
		}

		fileNodes = new ArrayList<FileNode>(files.length);
		for (File file: files) {
			fileNodes.add(new FileNode(file));
		}

		return fileNodes;
	}	
	
	@Override
	public String toString() {
		return super.toString();
	}
	
}