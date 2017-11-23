package com.classcheck.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * フィールドとメソッドローカルな変数をvisitするクラス
 * @author masa
 *
 */
public class VariableVisitor extends VoidVisitorAdapter<Void> {
	
	private Set<MyClass> setMyClass;
	private Map<MyClass, String> varNameMap;
	private Map<String, HashSet<String>> varNameMessagesMap;

	public VariableVisitor(Set<MyClass> setMyClass) {
		this.setMyClass = setMyClass;
		varNameMap = new HashMap<MyClass, String>();
	}

	@Override
	public void visit(FieldDeclaration n, Void arg) {
		super.visit(n, arg);
	}
	
	@Override
	public void visit(MethodDeclaration n, Void arg) {
		super.visit(n, arg);
	}
}
