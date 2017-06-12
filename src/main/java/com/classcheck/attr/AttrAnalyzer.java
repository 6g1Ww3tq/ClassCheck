package com.classcheck.attr;

import com.change_vision.jude.api.inf.model.IAttribute;

public class AttrAnalyzer {
	private IAttribute iAttribute;
	private StringBuilder message;

	public AttrAnalyzer(IAttribute iAttribute) {
		this.iAttribute = iAttribute;
		this.message = new StringBuilder();
	}
	
	private void toStringModifier(){
		if (iAttribute.isPublicVisibility()) {
			message.append("public ");
		}else if(iAttribute.isPrivateVisibility()){
			message.append("private ");
		}else if(iAttribute.isPackageVisibility()){
			message.append("package ");
		}else if(iAttribute.isProtectedVisibility()){
			message.append("protected ");
		}
		
	}
	
	private void toStringStatic(){
		if (iAttribute.isStatic()) {
			message.append("static ");
		}
	}

	private void toStringFinal(){
		if (iAttribute.isReadOnly()) {
			message.append("final ");
		}
	}

	private void toStringType(){
		message.append(iAttribute.getTypeExpression() + " ");
	}

	private void toStringName(){
		message.append(iAttribute.getName());
	}

	@Override
	public String toString() {
		toStringModifier();
		toStringStatic();
		toStringFinal();
		toStringType();
		toStringName();
		message.append('\n');
		return message.toString();
	}
}