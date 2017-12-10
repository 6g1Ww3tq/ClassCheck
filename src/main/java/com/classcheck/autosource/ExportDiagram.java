package com.classcheck.autosource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.change_vision.jude.api.inf.exception.InvalidExportImageException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.classcheck.window.DebugMessageWindow;

public class ExportDiagram {

	private List<IClassDiagram> classDiagrams;
	private String projectPath;
	private String exportPath;
	private List<ISequenceDiagram> sequenceDiagrams;

	public ExportDiagram(List<IClassDiagram> findDiagramList,List<ISequenceDiagram> findSequenceDiagramList, String projectPath) {
		this.classDiagrams = findDiagramList;
		this.sequenceDiagrams = findSequenceDiagramList;
		this.projectPath = projectPath; 
		this.exportPath = projectPath + "/.tmp";
	}

	public String getExportPath() {
		return exportPath;
	}

	public void exportImages() {
		IClassDiagram classDiagram;
		ISequenceDiagram sequenceDiagram;

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
		for(int i_sequenceDiagrams=0;i_sequenceDiagrams<sequenceDiagrams.size();i_sequenceDiagrams++){
			sequenceDiagram = sequenceDiagrams.get(i_sequenceDiagrams);
			try {
				sequenceDiagram.exportImage(exportPath, "png", 96);
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

	public boolean removeDirectory(String dir) throws NullPointerException{
		File removeDir = new File(dir);
		boolean removed = false;

		if (existExportDirectory(dir)) {
			try {
				FileUtils.deleteDirectory(removeDir);
				removed = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return removed;
	}

	public boolean existExportDirectory(String dir) throws NullPointerException{
		boolean isExist = false;
		File exportDir = new File(dir);

		if (exportDir.isDirectory()) {
			isExist = true;
		}else{
			isExist = false;
		}

		return isExist;
	}
}
