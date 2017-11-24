package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.presentation.IPresentation;

public class ClassSearcher {

	private List<IClassDiagram> diagramList;

	public ClassSearcher(List<IClassDiagram> diagramList) {
		this.diagramList = diagramList;
	}


	public List<IClassDiagram> findIClassDiagram(IClass iClass){
		List<IClassDiagram> foundDiagramList = new ArrayList<IClassDiagram>();
		IClassDiagram classDiagram;

		for(int i_diagramList=0;i_diagramList<diagramList.size();i_diagramList++){
			classDiagram = diagramList.get(i_diagramList);
			try {
				IPresentation[] presentations = classDiagram.getPresentations();
				
				for(int j_presentations=0;j_presentations<presentations.length;j_presentations++){
					IElement element = presentations[j_presentations].getModel();

					if (element instanceof IClass) {
						IClass targetClass = (IClass) element;
						
						if(targetClass.equals(iClass)){
							foundDiagramList.add(classDiagram);
						}
					}
				}
				
			} catch (InvalidUsingException e) {
				e.printStackTrace();
			}
		}

		return foundDiagramList;
	}
}
