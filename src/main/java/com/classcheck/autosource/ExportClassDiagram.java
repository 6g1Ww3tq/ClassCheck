package com.classcheck.autosource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.change_vision.jude.api.inf.exception.InvalidExportImageException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClassDiagram;

public class ExportClassDiagram {

	private List<IClassDiagram> classDiagrams;
	private String projectPath;
	private String exportPath;

	public ExportClassDiagram(List<IClassDiagram> findDiagramList,String projectPath) {
		this.classDiagrams = findDiagramList;
		this.projectPath = projectPath; 
		this.exportPath = projectPath + "/.tmp";
	}
	
	public String getExportPath() {
		return exportPath;
	}

	public void exportImages() {
		IClassDiagram classDiagram;
		
		for(int i_classDiagrams=0;i_classDiagrams<classDiagrams.size();i_classDiagrams++){
			classDiagram = classDiagrams.get(i_classDiagrams);
			try {
				classDiagram.exportImage(exportPath, "png", 96);
			} catch (InvalidUsingException e) {
				e.printStackTrace();
			} catch (InvalidExportImageException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void removeImages() {
		try {
			FileUtils.deleteDirectory(new File(exportPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}