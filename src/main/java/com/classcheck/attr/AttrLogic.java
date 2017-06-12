package com.classcheck.attr;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;

public class AttrLogic {
	IClass iClass;
	StringBuilder sb;
	IClass iattrType;
	String iattrModifier;
	IAttribute[] iAttributes;
	AttrAnalyzer attrAnalyzer;

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
			iAttributes = iClass.getAttributes();
		}

		if (iAttributes.length != 0) {
			isSuccess = true;

			for (IAttribute iAttribute : iAttributes) {
				attrAnalyzer = new AttrAnalyzer(iAttribute);
				sb.append(attrAnalyzer);
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
