package com.classcheck.gen;

import java.util.List;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodReplaceVisitor extends VoidVisitorAdapter<Void> {

	@Override
	public void visit(BlockStmt n, Void arg) {
		List<Statement> smList = n.getStmts();

		System.out.println("visit BlockStmt (Start) : ");

		for (Statement statement : smList) {
			System.out.println(statement);
		}

		System.out.println("visit BlockStmt (End) : ");
		super.visit(n, arg);
	}
}
