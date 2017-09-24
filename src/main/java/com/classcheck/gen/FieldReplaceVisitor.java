package com.classcheck.gen;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldReplaceVisitor extends VoidVisitorAdapter<Void> {

	private String befClassDecName;
	private String aftClassDecName;

	public FieldReplaceVisitor(String befClassDecName,String aftClassDecName) {
		super();
		this.befClassDecName = befClassDecName;
		this.aftClassDecName = aftClassDecName;
	}

	@Override
	public void visit(FieldDeclaration n, Void arg) {
		List<Node> listNode = n.getChildrenNodes();

		for (Node node : listNode) {
			System.out.println("node : "+node);
			node.setData(aftClassDecName);
		}
		super.visit(n, arg);
	}

}