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
		boolean isMessageChanged = false;


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

						//コンマ[;]を外す
						message = message.substring(0,message.length()-1);


						for (MyClass myClass : changeMap.keySet()){

							className = varsMap.get(var);
							if (myClass.getName().contains(className)) {
								messagesMap = changeMap.get(myClass);

								for(String befMessage : messagesMap.keySet()){

									if (befMessage.contains(message)) {
										//fqcnとそうでない場合がある
										//テーブルを更新した状態
										//とそうでない場合だとメッセージが違う??(可能性)
										/*
										String regex = ".+\\s+.+\\.(.+\\(.*\\));";
										Pattern pattern = Pattern.compile(regex);
										Matcher matcher;

										message = messagesMap.get(befMessage);
										matcher = pattern.matcher(message);

										if (matcher.find()) {
											message = matcher.group(1)+";";
										}

										isMessageChanged = true;
										break;
										 */
										Pattern pattern = Pattern.compile(" ");
										message = messagesMap.get(befMessage);
										String ss[] = pattern.split(message, 0);

										if (ss.length > 0) {
											message = ss[ss.length - 1] + ";";
										}

										isMessageChanged = true;
										break;
									}
								}

								if (isMessageChanged) {
									isMessageChanged = false;
									break;
								}
							}
						}


						strStateMent = var+"."+message;
					}
				}
			}

			if (strStateMent == null) {
				strStateMent = statement.toString();
			}

			strList.add(strStateMent);
		}

		if (strList.size() > 0) {

			lines.append("{\n");

			//怪しい	
			for (Iterator it = strList.iterator() ; it.hasNext() ;){
				String str = (String) it.next();

				if (str == null) {
					continue;
				}

				if (!str.contains(";")) {
					str += ";";
				}

				lines.append("\t"+str + "\n");
			}
			lines.append("}\n");

			try {
				blockSt = JavaParser.parseBlock(lines.toString());
				n.setBody(blockSt);
			} catch (ParseException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("***error**");
				System.out.println(lines.toString());
				System.out.println(e);
				System.out.println("*****");
			}

		}

		super.visit(n,arg);
	}
}
