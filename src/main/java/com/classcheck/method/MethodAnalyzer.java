package com.classcheck.method;

import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;

public class MethodAnalyzer {
	private StringBuilder message;
	private IOperation iOperation;

	public MethodAnalyzer(IOperation iOperation) {
		this.iOperation = iOperation;
		this.message = new StringBuilder();
	}
	
	private void toStringModifier(){
		if (iOperation.isPublicVisibility()) {
			message.append("public ");
		}else if(iOperation.isPrivateVisibility()){
			message.append("private ");
		}else if(iOperation.isPackageVisibility()){
			message.append("package ");
		}else if(iOperation.isProtectedVisibility()){
			message.append("protected ");
		}
		
	}
	
	private void toStringStatic(){
		if (iOperation.isStatic()) {
			message.append("static ");
		}
	}

	private void toStringFinal(){
		if (iOperation.isReadOnly()) {
			message.append("final ");
		}
	}


	private void toStringReturnName(){
		message.append(iOperation.getReturnTypeExpression() + " ");
	}

	private void toStringName(){
		message.append(iOperation.getName());
	}

	private void toStringParams(){
		IParameter[] params = iOperation.getParameters();
		
		message.append('(');
		for (IParameter param : params) {
			message.append(param.getTypeExpression() + " ");
			message.append(param.getName() + " ");
			message.append(",");
		}
		message.append(')');
	}

	@Override
	public String toString() {
		toStringModifier();
		toStringStatic();
		toStringFinal();
		toStringReturnName();
		toStringName();
		toStringParams();
		message.append('\n');
		return message.toString();
	}
}