package com.classcheck.analyzer.source;

import java.lang.reflect.Modifier;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends VoidVisitorAdapter<Void> {
	private StringBuilder sbMsg;
	
	public MethodVisitor() {
		sbMsg = new StringBuilder();
	}
	
	@Override
	public void visit(MethodDeclaration methodDec, Void arg) {
		sbMsg.append("-------Modifiers-----\n");
		sbMsg.append(Modifier.toString(methodDec.getModifiers())+"\n");
		sbMsg.append("-------getRange-----\n");
		sbMsg.append(methodDec.getRange()+"\n");
		sbMsg.append("-------Parameters-----\n");
		for (Parameter param : methodDec.getParameters()) {
			sbMsg.append(param+"\n");
		}
		sbMsg.append("-------getThrows-----\n");
		for (ReferenceType param : methodDec.getThrows()) {
			sbMsg.append(param+"\n");
		}
		sbMsg.append("-------getReturnType-----\n");
		sbMsg.append(methodDec.getType()+"\n");
		sbMsg.append("------MethodName------\n");
		sbMsg.append(methodDec.getName()+"\n");
		sbMsg.append("--------Body----------\n");
		sbMsg.append(methodDec.getBody()+"\n");
		sbMsg.append("---------------------\n");
		super.visit(methodDec, arg);
	}
	
	public String getMessage() {
		return sbMsg.toString();
	}
}