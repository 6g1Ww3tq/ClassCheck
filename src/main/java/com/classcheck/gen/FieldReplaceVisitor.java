package com.classcheck.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class FieldReplaceVisitor extends VoidVisitorAdapter<Void> {

	private Map<MyClass, CodeVisitor> tableMap;

	//フィールドの変数名に対する型を記録しておく
	private HashMap<String, MyClass> variableBefMap;
	private HashMap<String, CodeVisitor> variableAftMap;

	Pattern varPattern;

	public FieldReplaceVisitor(Map<MyClass, CodeVisitor> tableMap) {
		super();
		this.tableMap = tableMap;
		this.variableBefMap= new HashMap<String, MyClass>();
		this.variableAftMap = new HashMap<String, CodeVisitor>();

		varPattern = Pattern.compile("\\s+.+;");
	}

	public void visit(VariableDeclarator n, Void arg) {
		Expression exp = n.getInit();
		if (exp instanceof IntegerLiteralExpr) {

		}
	}

	@Override
	public void visit(FieldDeclaration field, Void arg) {
		ClassOrInterfaceType aftType = null;
		//クラス名変換後
		CodeVisitor aftClass;

		Matcher matcher;

		for(MyClass myClass : tableMap.keySet()){
			//フィールドのクラスとスケルトンコードのクラスといずれか一つに一致
			if (field.toString().contains(myClass.getName())) {
				aftClass = tableMap.get(myClass);
				aftType = new ClassOrInterfaceType(aftClass.getClassName());
				field.setType(aftType);

				matcher = varPattern.matcher(field.toString());


				if (matcher.find()) {
					//マッチした文字列を取得(変数名の抽出)
					variableBefMap.put(matcher.group()
							.replaceAll("^\\s+", "")
							.replaceAll(";", ""), myClass);
					variableAftMap.put(matcher.group()
							.replaceAll("^\\s+", "")
							.replaceAll(";", ""), aftClass);
					break;
				}
			}
		}
		super.visit(field, arg);
	}

	public HashMap<String, MyClass> getVariableBefMap() {
		return variableBefMap;
	}
	
	public HashMap<String, CodeVisitor> getVariableAftMap() {
		return variableAftMap;
	}

}