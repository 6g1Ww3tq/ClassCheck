package com.classcheck.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TestSkeltonCodeVisitor extends VoidVisitorAdapter<Void> {
	
	private List<String> mockFinalParamsList;
	private HashMap<String, String> mockMethodMap;

	public TestSkeltonCodeVisitor() {
		this.mockFinalParamsList = new ArrayList<String>();
		this.mockMethodMap = new HashMap<String, String>();
	}
	
	public List<String> getMockFieldList() {
		return mockFinalParamsList;
	}
	
	public HashMap<String, String> getMockMethodMap() {
		return mockMethodMap;
	}
	
	@Override
	public void visit(FieldDeclaration n, Void arg) {
		String fieldDec = n.toString();
		String[] splitStr; 
		StringBuilder sb = new StringBuilder();
		
		fieldDec = fieldDec.replaceAll(";", "");
		splitStr = fieldDec.split(" ");
		
		sb.append("@Mocked " + "final "+splitStr[splitStr.length-2]+" "+splitStr[splitStr.length-1]);
		
		mockFinalParamsList.add(sb.toString());
		
		super.visit(n, arg);
	}
	
	@Override
	public void visit(MethodDeclaration n, Void arg) {
		String methodName = n.getName();
		BlockStmt bs = n.getBody();
		List<Statement> stmts = bs.getStmts();
		Statement st;
		StringBuilder sb = new StringBuilder();
		
		//make Expectatioon State
		for(int i=0;i<stmts.size();i++){
			st = stmts.get(i);
			
			if (st.toString().contains("return")) {
				break;
			}
			
			sb.append("\r\t\t"+st.toString()+"\n");
			sb.append("\r\t\t"+"times=1" + "\n");
		}
		
		mockMethodMap.put(methodName, sb.toString());
		
		// TODO 自動生成されたメソッド・スタブ
		super.visit(n, arg);
	}
}
