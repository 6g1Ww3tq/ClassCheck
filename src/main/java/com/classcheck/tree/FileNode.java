package com.classcheck.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public String getFileNameRemovedFormat(){
		String fileName = getName();
		String fileNameRemovedFormat_str = null;
		Pattern pattern = Pattern.compile("(.+)\\..+$"); 
		Matcher matcher = null;

		if (fileName != null) {
			matcher = pattern.matcher(fileName);
			
			if (matcher.find()) {
				fileNameRemovedFormat_str = matcher.group(1);
			}
		}
		
		return fileNameRemovedFormat_str;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
}