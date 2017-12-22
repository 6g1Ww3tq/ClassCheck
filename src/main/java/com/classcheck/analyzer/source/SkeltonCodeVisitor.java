package com.classcheck.analyzer.source;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SkeltonCodeVisitor extends VoidVisitorAdapter<Void> {

	private boolean writtenNewSt;

	public boolean isWrittenNewSt() {
		return writtenNewSt;
	}

	public SkeltonCodeVisitor() {
		this.writtenNewSt = false;
	}
	
	/*
	 * TODO: メソッドローカルなインスタンスが生成されているスケルトンコードの対処 -> autosource の修正
	 * LineSegmentクラスを継承したThickLineSegmentクラスの一部メソッド内にて..
	 * 
	 * Point start = new Point();
	 * start.linearTransfer();
	 * 
	 * と書かれている場合は
	 * Point start = new Point();
	 * を削除し
	 * フィールドのインスタンスがメソッドをコールしているとする
	 */
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		List<BodyDeclaration> list = n.getMembers();
		Pattern pattern = Pattern.compile("= *new +.*");
		Matcher matcher;
		
		for (BodyDeclaration bodyDeclaration : list) {
			
			if (bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) bodyDeclaration;
				
				BlockStmt methodSt = method.getBody();
				
				for(Statement st : methodSt.getStmts()){
					matcher = pattern.matcher(st.toString());
					
					if (matcher.find()) {
						writtenNewSt = true;
						break;
					}
				}
				
				if (writtenNewSt) {
					break;
				}
			}

		}
		
		super.visit(n, arg);
	}
}
