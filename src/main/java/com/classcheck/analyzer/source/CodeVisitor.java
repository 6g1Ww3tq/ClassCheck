package com.classcheck.analyzer.source;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class CodeVisitor extends VoidVisitorAdapter<Void> {
	private String className;
	private String classSig;
	private List<MethodDeclaration> methodList;
	private List<ConstructorDeclaration> constructorList;
	
	public CodeVisitor() {
		className = null;
		classSig = "";
		methodList = new ArrayList<MethodDeclaration>();
		constructorList = new ArrayList<ConstructorDeclaration>();
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration classDec, Void arg1) {
		List<BodyDeclaration> list = classDec.getMembers();
		className = classDec.getName();
		classSig += Modifier.toString(classDec.getModifiers());
		if (!Modifier.toString(classDec.getModifiers()).isEmpty()) {
			classSig += " ";
		}
		classSig += "class ";
		classSig += classDec.getName();
		
		for (BodyDeclaration bodyDeclaration : list) {

			if (bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) bodyDeclaration;
				methodList.add(method);
			}

			/*
			 * コンストラクタをソースコードから読み取るかどうか
			 */
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
	
	public String getClassSig() {
		return classSig;
	}
	
	public List<ConstructorDeclaration> getConstructorList() {
		return constructorList;
	}
	
	public List<MethodDeclaration> getMethodList() {
		return methodList;
	}
	
	@Override
	public String toString() {
		return getClassName();
	}

}