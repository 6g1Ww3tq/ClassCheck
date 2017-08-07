package com.classcheck.analyzer.source;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends VoidVisitorAdapter<Void> {
	private StringBuilder sbMsg;
	
	public MethodVisitor() {
		sbMsg = new StringBuilder();
	}
	
	@Override
	public void visit(MethodDeclaration methodDec, Void arg) {
		/* here you can access the attributes of the method.
          this method will be called for all methods in this 
          CompilationUnit, including inner class methods */
		sbMsg.append(methodDec.toString());
		super.visit(methodDec, arg);
	}
	
	public String getMessage() {
		return sbMsg.toString();
	}
}