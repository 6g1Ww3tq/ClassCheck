package com.classcheck.gen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodReplaceVisitor extends VoidVisitorAdapter<Void> {

	private Map<MyClass, Map<String, String>> methodChangeMap;
	private HashMap<String, MyClass> variableBefMap;
	private HashMap<String, CodeVisitor> variableAftMap;

	public MethodReplaceVisitor(
			Map<MyClass, Map<String, String>> methodChangeMap,
			HashMap<String, MyClass> variableBefMap,
			HashMap<String, CodeVisitor> variableAftMap) {
		this.methodChangeMap = methodChangeMap;
		this.variableBefMap = variableBefMap;
		this.variableAftMap = variableAftMap;
	}

	@Override
	public void visit(BlockStmt n, Void arg) {
		List<Statement> stmts = n.getStmts();
		
		for (Statement statement : stmts) {
		}
		super.visit(n, arg);
	}
}
