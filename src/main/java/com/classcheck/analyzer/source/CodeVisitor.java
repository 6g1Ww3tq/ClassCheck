package com.classcheck.analyzer.source;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CodeVisitor extends VoidVisitorAdapter<Void> {
	private String className;
	private List<MethodDeclaration> methodList;
	private List<ConstructorDeclaration> constructorList;
	
	public CodeVisitor() {
		className = null;
		methodList = new ArrayList<MethodDeclaration>();
		constructorList = new ArrayList<ConstructorDeclaration>();
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration classDec, Void arg1) {
		List<BodyDeclaration> list = classDec.getMembers();
		className = classDec.getName();
		
		for (BodyDeclaration bodyDeclaration : list) {

			if (bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) bodyDeclaration;
				methodList.add(method);
			}

			if (bodyDeclaration instanceof ConstructorDeclaration) {
				ConstructorDeclaration constructor = (ConstructorDeclaration) bodyDeclaration;
				constructorList.add(constructor);
			}
		}
		super.visit(classDec, arg1);
	}
	
	public String getClassName() {
		return className;
	}
	
	public List<ConstructorDeclaration> getConstructorList() {
		return constructorList;
	}
	
	public List<MethodDeclaration> getMethodList() {
		return methodList;
	}

}