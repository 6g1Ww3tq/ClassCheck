package com.classcheck.method;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;

public class MethodLogic {
	IClass iClass;
	StringBuilder sb;
	IClass iattrType;
	String iattrModifier;
	IOperation[] iOperations;
	MethodAnalyzer methodAnalyzer;

	public void setiClass(INamedElement element) {
		if (element instanceof IClass) {
			this.iClass = (IClass) element;
			this.sb = new StringBuilder();
		}else{
			System.out.println("this element is not IClass instance.");
		}
	}

	public boolean exe() {
		boolean isSuccess = false;

		if (iClass != null) {
			iOperations = iClass.getOperations();
		}

		if (iOperations.length != 0) {
			isSuccess = true;

			for (IOperation iOperation : iOperations) {
				methodAnalyzer = new MethodAnalyzer(iOperation);
				sb.append(methodAnalyzer);
			}
		}

		return isSuccess;
	}

	@Override
	public String toString() {
		String str = sb.toString();
		sb = new StringBuilder();

		return str;
	}
}
