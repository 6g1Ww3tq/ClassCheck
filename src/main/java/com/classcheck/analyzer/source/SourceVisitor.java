package com.classcheck.analyzer.source;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class SourceVisitor extends ASTVisitor {
	StringBuilder sb;

	public SourceVisitor() {
		this.sb = new StringBuilder();
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		sb.append(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		sb.append(node);
		return super.visit(node);
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
}
