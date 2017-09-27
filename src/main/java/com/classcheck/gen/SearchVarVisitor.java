package com.classcheck.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class SearchVarVisitor extends VoidVisitorAdapter<Void> {

	//キー：変数名　値：型   値の重複は許されるがキーの重複は許されないから
	private HashMap<String, String> varsMap;

	private List<Integer> replaceBeginLines; 
	public SearchVarVisitor() {
		varsMap = new HashMap<String, String>();
		replaceBeginLines = new ArrayList<Integer>();
	}

	public HashMap<String, String> getVarsMap() {
		return varsMap;
	}
	
	public List<Integer> getReplaceBeginLines() {
		return replaceBeginLines;
	}

	public void visit(FieldDeclaration n, Void arg) {
		List<Node> nodeList = n.getChildrenNodes();
		Node classNode = null;
		Node varNode = null;

		if (nodeList.size() == 2) {
			classNode = nodeList.get(0);
			varNode = nodeList.get(1);

			varsMap.put(classNode.toString(), varNode.toString());
		}
		super.visit(n, arg);
	}

	@Override
	public void visit(BlockStmt n, Void arg) {
		List<Statement> smList = n.getStmts();
		String state,subRegex,decRegex,retRegex;
		Pattern decPattern,subPattern;
		decPattern = subPattern = null;
		Matcher matcher = null;
		Pattern spacePattern = Pattern.compile(" ");
		String[] ss = null;

		retRegex = "return\\s*\\w*;";
		decRegex = ".+\\s+.+;";
		subRegex = ".+\\s+.+\\s*=.+;";

		decPattern = Pattern.compile(decRegex);
		subPattern = Pattern.compile(subRegex);

		for (Statement statement : smList) {

			state = statement.toString();

			if (!Pattern.matches(retRegex, state)) {
				if((matcher = decPattern.matcher(state)) != null){
					if (matcher.find()) {
						replaceBeginLines.add(statement.getBeginLine());
						ss = spacePattern.split(state, 0);
						
						//ss[1]：変数名　ss[0]:型	値の重複は許されるがキーの重複は許されないから
						varsMap.put(ss[1], ss[0]);
					}
				}else if((matcher = subPattern.matcher(state)) != null){
					if (matcher.find()) {
						replaceBeginLines.add(statement.getBeginLine());
						ss = spacePattern.split(state, 0);
						
						//ss[1]：変数名　ss[0]:型	値の重複は許されるがキーの重複は許されないから
						varsMap.put(ss[1], ss[0]);
					}
				}
			}
		}
		super.visit(n, arg);
	}
}
