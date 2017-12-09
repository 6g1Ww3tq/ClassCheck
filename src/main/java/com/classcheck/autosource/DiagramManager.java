package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class DiagramManager {
	private AstahAPI api;
	IModel rootModel;
	private String projectPath;

	public DiagramManager() {
		try {
			api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			this.projectPath = projectAccessor.getProjectPath();
			int last = this.projectPath.lastIndexOf("/");
			//実例「/home/User/Sample.astah」->「/home/User/」
			this.projectPath = projectPath.substring(0, last);
			rootModel = projectAccessor.getProject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getProjectPath() {
		return projectPath;
	}

	public List<IClassDiagram> getAllClassDiagramList(){
		List<IClassDiagram> diagramList = new ArrayList<IClassDiagram>();
		
		IDiagram[] diagrams = rootModel.getDiagrams();
		
		for(int i=0;i<diagrams.length;i++){
			if (diagrams[i] instanceof IClassDiagram) {
				IClassDiagram classDiagram = (IClassDiagram) diagrams[i];
				diagramList.add(classDiagram);
			}
		}
		

		try {
			getAllClassDiagrams(rootModel, diagramList,null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}

		return diagramList;
	}
	
	public List<ISequenceDiagram> getAllISequenceDiagramList(){
		List<ISequenceDiagram> diagramList = new ArrayList<ISequenceDiagram>();
		
		IDiagram[] diagrams = rootModel.getDiagrams();
		
		for(int i=0;i<diagrams.length;i++){
			if (diagrams[i] instanceof ISequenceDiagram) {
				ISequenceDiagram classDiagram = (ISequenceDiagram) diagrams[i];
				diagramList.add(classDiagram);
			}
		}
		

		try {
			getAllSequenceDiagrams(rootModel, diagramList,null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
		return diagramList;
	}

	private void getAllSequenceDiagrams(INamedElement element,
			List<ISequenceDiagram> diagramList, IDiagram diagram)
					throws ClassNotFoundException, ProjectNotFoundException {
		
		if (diagram != null) {

			if (diagram instanceof ISequenceDiagram) {
				ISequenceDiagram classDiagram = (ISequenceDiagram) diagram;
				diagramList.add(classDiagram);
			}
		}

		if (element instanceof IPackage) {
			for(INamedElement ownedNamedElement : ((IPackage)element).getOwnedElements()) {
				IDiagram[] diagrams = ownedNamedElement.getDiagrams();

				for(int i=0;i<diagrams.length;i++){
					getAllSequenceDiagrams(ownedNamedElement, diagramList,diagrams[i]);
				}
			}
		}	

	}

	private void getAllClassDiagrams(INamedElement element, List<IClassDiagram> diagramList ,IDiagram diagram)
			throws ClassNotFoundException, ProjectNotFoundException {

		if (diagram != null) {

			if (diagram instanceof IClassDiagram) {
				IClassDiagram classDiagram = (IClassDiagram) diagram;
				diagramList.add(classDiagram);
			}
		}

		if (element instanceof IPackage) {
			for(INamedElement ownedNamedElement : ((IPackage)element).getOwnedElements()) {
				IDiagram[] diagrams = ownedNamedElement.getDiagrams();

				for(int i=0;i<diagrams.length;i++){
					getAllClassDiagrams(ownedNamedElement, diagramList,diagrams[i]);
				}
			}
		}	

	}
}
