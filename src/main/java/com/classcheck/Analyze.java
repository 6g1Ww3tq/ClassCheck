package com.classcheck;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.classcheck.attr.AttrLogic;
import com.classcheck.method.MethodLogic;

public class Analyze {
	private AstahAPI api;
	private ProjectAccessor projectAccessor;
	private IModel iCurrentProject;
	private List<INamedElement> classList;
	private StringBuilder sb;

	public Analyze(AstahAPI api) throws ClassNotFoundException, ProjectNotFoundException {
		this.api = AstahAPI.getAstahAPI();
		this.projectAccessor = api.getProjectAccessor();
		this.iCurrentProject = projectAccessor.getProject();
		this.classList = new ArrayList<INamedElement>();
		this.sb = new StringBuilder();
	}

	public void doAnalyze() {
		IPackage iPackage;
		AttrLogic attrLogic = new AttrLogic();
		MethodLogic methodLogic = new MethodLogic();

		try {
			getAllClasses(iCurrentProject, classList);
			sb.append("There are " + classList.size() + " classes.\n");

			for (INamedElement element : classList) {
				if (element instanceof IClass) {
					//クラス名
					sb.append("ClassName : " + element + "\n");

					//属性
					attrLogic.setiClass(element);
					attrLogic.exe();
					sb.append(attrLogic);
					
					//メソッド名
					methodLogic.setiClass(element);
					methodLogic.exe();
					sb.append(methodLogic);

				}else if(element instanceof IPackage){
					//パッケージ名
					iPackage = (IPackage) element;
					sb.append(iPackage.toString() + "\n");
				}

			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {
		return sb.toString();
	}

	private void getAllClasses(INamedElement element, List<INamedElement> classList)
			throws ClassNotFoundException, ProjectNotFoundException {
		if (element instanceof IPackage) {
			classList.add(element);
			for (INamedElement ownedNamedElement : ((IPackage) element).getOwnedElements()) {
				getAllClasses(ownedNamedElement, classList);
			}
		} else if (element instanceof IClass) {
			classList.add(element);
			for (IClass nestedClasses : ((IClass) element).getNestedClasses()) {
				getAllClasses(nestedClasses, classList);
			}
		}
	}
}
