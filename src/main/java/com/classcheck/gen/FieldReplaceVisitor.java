package com.classcheck.gen;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldReplaceVisitor extends VoidVisitorAdapter<Void> {

	private String befClassDecName;
	private String aftClassDecName;

	public FieldReplaceVisitor(String befClassDecName,String aftClassDecName) {
		super();
		this.befClassDecName = befClassDecName;
		this.aftClassDecName = aftClassDecName;
	}
	
	public void visit(VariableDeclarator n, Void arg) {
		Expression exp = n.getInit();
		if (exp instanceof IntegerLiteralExpr) {
			
		}
	}

	@Override
	public void visit(FieldDeclaration field, Void arg) {
		NameExpr ne = new NameExpr();
		ne.setName("Hoge");
		ClassOrInterfaceType aftType = new ClassOrInterfaceType(aftClassDecName);
		FieldDeclaration fieldDeclaration = null;

		if (field.toString().contains(befClassDecName)) {
			field.setType(aftType);
			fieldDeclaration = new FieldDeclaration(field.getModifiers(), aftType, field.getVariables());
			field = fieldDeclaration;
		}
		super.visit(field, arg);
	}

}