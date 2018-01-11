package com.classcheck.uml.search;

import java.util.List;

import javax.swing.JLabel;

import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.classcheck.autosource.DiagramManager;
import com.classcheck.autosource.SequenceSearcher;

public class SequenceDiagramSearch {

	private String className;
	private JLabel methodLabel;

	public SequenceDiagramSearch(String className,JLabel methodLabel){
		this.className = className;
		this.methodLabel = methodLabel;
	}
	
	public List<ISequenceDiagram> getSequenceList(){
		DiagramManager dm = new DiagramManager();
		List<ISequenceDiagram> allISequenceDiagram = dm.getAllISequenceDiagramList();
		List<ISequenceDiagram> findSequenceDiagramList = null;
		SequenceSearcher ss = new SequenceSearcher(allISequenceDiagram,"#FF0000");
		
		findSequenceDiagramList = ss.findMethodFromISequenceDiagram(className,methodLabel);
		
		return findSequenceDiagramList;
	}
}
