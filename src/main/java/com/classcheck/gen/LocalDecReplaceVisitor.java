package com.classcheck.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LocalDecReplaceVisitor extends VoidVisitorAdapter<Void> {

	private Map<String, String> varsMap;
	private Map<MyClass, CodeVisitor> codeMap;
	private List<Integer> replaceBeginLines;

	public LocalDecReplaceVisitor(Map<MyClass, CodeVisitor> codeMap,
			HashMap<String, String> varsMap, List<Integer> replaceBeginLines) {
		this.codeMap = codeMap;
		this.varsMap = varsMap;
		this.replaceBeginLines = replaceBeginLines;
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		BlockStmt blockSt = n.getBody();
		List<String> strList = new ArrayList<String>();
		StringBuilder lines = new StringBuilder();
		List<Statement> smList = blockSt.getStmts();
		Statement statement = null;
		String strStateMent = null;


		for (int row = 0 ; row < smList.size() ; row++){
			statement = smList.get(row);
			strStateMent = statement.toString();

			for (MyClass myClass : codeMap.keySet()){

				if (strStateMent.contains(myClass.getName())) {
					strStateMent = strStateMent.replaceAll(myClass.getName(), codeMap.get(myClass).getClassName());
					break;
				}
			}

			strList.add(strStateMent);
		}

		if (strList.size() > 0) {

			lines.append("{\n");
			for (Iterator it = strList.iterator() ; it.hasNext() ;){
				String str = (String) it.next();
				lines.append("\t"+str + "\n");
			}
			lines.append("}\n");

			try {
				blockSt = JavaParser.parseBlock(lines.toString());
				n.setBody(blockSt);
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		super.visit(n, arg);
	}
}
