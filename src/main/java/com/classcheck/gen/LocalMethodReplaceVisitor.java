package com.classcheck.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class LocalMethodReplaceVisitor extends VoidVisitorAdapter<Void> {

	private Map<MyClass, Map<String, String>> changeMap;
	private Map<String, String> varsMap;
	private Map<MyClass, CodeVisitor> codeMap;
	private List<Integer> replaceBeginLines;

	public LocalMethodReplaceVisitor(Map<MyClass, CodeVisitor> codeMap,
			HashMap<String, String> varsMap, List<Integer> replaceBeginLines,
			Map<MyClass, Map<String, String>> changeMap) {
		this.codeMap = codeMap;
		this.varsMap = varsMap;
		this.changeMap = changeMap;
		this.replaceBeginLines = replaceBeginLines;
	}

	public void visit(com.github.javaparser.ast.body.MethodDeclaration n, Void arg) {
		BlockStmt blockSt = n.getBody();
		StringBuilder lines = new StringBuilder();
		List<String> strList = new ArrayList<String>();
		List<Statement> smList = blockSt.getStmts();
		Map<String, String> messagesMap = null;
		Statement statement = null;
		String strStateMent,className,message = null;
		Pattern varPattern = null;
		Matcher varMatcher = null;


		for (int row = 0 ; row < smList.size() ; row++){
			strStateMent = null;
			statement = smList.get(row);

			for (String var : varsMap.keySet()){

				if (statement.toString().contains(var)) {
					className = varsMap.get(var);
				}

				varPattern = Pattern.compile(var+"\\."+"(.+)");
				if (statement.toString().contains(var)) {
					varMatcher = varPattern.matcher(statement.toString());

					if (varMatcher.find()) {

						message = varMatcher.group(1);
						for (MyClass myClass : changeMap.keySet()){

							className = varsMap.get(var);
							if (myClass.getName().contains(className)) {
								messagesMap = changeMap.get(myClass);

								for(String befMessage : messagesMap.keySet()){

									if (message.contains(befMessage)) {
										message = messagesMap.get(befMessage);
										break;
									}
								}

								break;
							}
						}


						strStateMent = varMatcher.replaceAll(var+"."+message);
					}
				}
			}

			if (strStateMent != null) {
				strStateMent = statement.toString();
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
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		}

		super.visit(n,arg);
	}
}
