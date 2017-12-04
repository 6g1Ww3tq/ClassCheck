package com.classcheck.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TestSkeltonCodeVisitor extends VoidVisitorAdapter<Void> {

	//テストコードに対応する学生のコード
	private CodeVisitor codeVisitor;
	//同様にスケルトンコード
	private MyClass myClass;

	private Map<MyClass, CodeVisitor> tableMap;
	private Map<MyClass, Map<String, String>> fieldChangeMap;
	private Map<MyClass, Map<String, String>> methodChangeMap;
	private List<String> mockFinalParamsList;
	private HashMap<String, String> mockMethodMap;
	private Collection<CodeVisitor> codeCollection;
	
	//スケルトンコードのフィールドに定義されている変数名と
	//実際のコードのフィールドの変数名を結びつける
	private HashMap<String, String> variableFieldNameMap;

	public TestSkeltonCodeVisitor(CodeVisitor codeVisitor,
			Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<MyClass, Map<String, String>> methodChangeMap,
			Collection<CodeVisitor> codeCollection) {
		this.codeVisitor = codeVisitor;
		this.tableMap = tableMap;
		this.fieldChangeMap = fieldChangeMap;
		this.methodChangeMap = methodChangeMap;
		this.codeCollection = codeCollection;
		this.mockFinalParamsList = new ArrayList<String>();
		this.mockMethodMap = new HashMap<String, String>();
		this.variableFieldNameMap = new HashMap<String, String>();
		this.myClass = interactCodeClass_from_table(codeVisitor.getClassName());
	}
	
	public HashMap<String, String> getVariableFieldNameMap() {
		return variableFieldNameMap;
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

		//ユーザー定義の参照型のみモックするようにする
		//intのような基本型はMockしない
		for(CodeVisitor codeVisitor : codeCollection){
			if (splitStr[splitStr.length-2].equals(codeVisitor.getClassName())) {
				String typeName = splitStr[splitStr.length-2];
				String varName = splitStr[splitStr.length-1];
				String skeltonFieldVariableName;
				sb.append("@Mocked " + "final "+typeName+" "+varName);
				skeltonFieldVariableName = interactCodeVariableName_from_panel(myClass, varName);
				
				this.variableFieldNameMap.put(skeltonFieldVariableName, varName);
				//								型			変数名
				mockFinalParamsList.add(sb.toString());
			}
		}


		super.visit(n, arg);
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		String methodName = n.getName();
		BlockStmt bs = n.getBody();
		List<Statement> stmts = bs.getStmts();
		Statement st;
		StringBuilder sb = new StringBuilder();
		String statement_str;

		//make Expectation State
		for(int i=0;i<stmts.size();i++){
			st = stmts.get(i);

			if (st.toString().contains("return")) {
				break;
			}

			statement_str = replaceVariableNameUsedBySkeltonVariableName(st.toString());
			sb.append("\r\t\t\t"+statement_str+"\n");
			sb.append("\r\t\t\t"+"times=1" + "\n");
		}

		mockMethodMap.put(methodName, sb.toString());
		super.visit(n, arg);
	}
	
	private String replaceVariableNameUsedBySkeltonVariableName(String statement){
		String replaced_statement;
		String codeFieldName;
		
		for (String skeltonFieldName : variableFieldNameMap.keySet()) {
			//TODO
			//正規表現を使ってスケルトンコードから生成したフィールド変数名をコードの変数名にする
			//if (statement.matches(skeltonFieldName+"\\.[a-zA-Z_0-9]+\\(")) {
			if (statement.contains(skeltonFieldName+".")) {
				codeFieldName = variableFieldNameMap.get(skeltonFieldName);
				replaced_statement = statement.replaceFirst(skeltonFieldName+"\\.",codeFieldName+".");
				return replaced_statement;
			}
		}
		
		return statement;
	}
	
	private String interactCodeVariableName_from_panel(MyClass myClass,String targetCodeFieldVariableName){
		String rtnSkeltonFieldVariableName = null;
		//クラスのフィールドのマップを取得する（インタラクティブにユーザに設定してもらった対応関係のマップ）
		Map<String, String> classFieldMap = fieldChangeMap.get(myClass);
		String codeFieldVariableName;
		String[] split_str;
		
		for (String skeltonField : classFieldMap.keySet()) {
			String codeField = classFieldMap.get(skeltonField);
			split_str = codeField.split(" ");
			codeFieldVariableName = split_str[split_str.length-1];
			
			//コードに書いてあるターゲットのフィールドの変数名を見つけ、
			//対応するスケルトンコードのクラスのフィールドの変数名を取得する
			if (codeFieldVariableName.equals(targetCodeFieldVariableName)) {
				split_str = skeltonField.split(" ");
				String skeltonFieldVariableName = split_str[split_str.length -1];
				rtnSkeltonFieldVariableName = skeltonFieldVariableName;
				break;
			}
		}
		
		return rtnSkeltonFieldVariableName;
	}
	
	private MyClass interactCodeClass_from_table(String codeClass_str){
		MyClass rtnSkeltonClass = null;
		Set<MyClass> myClass_Set = tableMap.keySet();
		
		for (MyClass myClass : myClass_Set) {
			CodeVisitor codeVisitor = tableMap.get(myClass);
			
			if (codeVisitor.getClassName().equals(codeClass_str)) {
				rtnSkeltonClass = myClass;
				break;
			}
		}
		
		return rtnSkeltonClass;
	}
}
