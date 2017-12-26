package com.classcheck.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.type.JudgementMockType;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class TestSkeltonCodeVisitor extends VoidVisitorAdapter<Void> {

	//このクラスのテストコードに対応する学生のコード
	private CodeVisitor codeVisitor;
	//同様にこのクラスのスケルトンコード
	private MyClass myClass;

	private BidiMap<MyClass, CodeVisitor> tableMap;
	private BidiMap<CodeVisitor, MyClass> inverseTableMap;
	private Map<MyClass, Map<String, String>> fieldChangeMap;
	private Map<MyClass, Map<String, String>> methodChangeMap;
	private List<String> mockFinalParamsList;
	private HashMap<String, String> mockMethodMap;
	private Collection<CodeVisitor> codeCollection;

	//スケルトンコードのフィールドに定義されている変数名と
	//実際のコードのフィールドの変数名を結びつける
	private HashMap<String, String> variableFieldNameMap;
	//<ソースコードのフィールドの変数名,型>
	private HashMap<String, MyClass> varTypeMap;

	public TestSkeltonCodeVisitor(CodeVisitor codeVisitor,
			Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<MyClass, Map<String, String>> methodChangeMap,
			Collection<CodeVisitor> codeCollection) {
		this.codeVisitor = codeVisitor;
		this.tableMap = new DualHashBidiMap<MyClass, CodeVisitor>(tableMap);
		this.inverseTableMap = this.tableMap.inverseBidiMap();
		this.fieldChangeMap = fieldChangeMap;
		this.varTypeMap = new HashMap<String, MyClass>();
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
				this.varTypeMap.put(varName, inverseTableMap.get(codeVisitor));
				mockFinalParamsList.add(sb.toString());
			}
		}


		super.visit(n, arg);
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		String methodSigNature_str = n.getDeclarationAsString();
		BlockStmt bs = n.getBody();
		List<Statement> stmts = bs.getStmts();
		Statement st;
		StringBuilder sb = new StringBuilder();
		String statement_str;
		MyClass fieldTypeClass = null;

		//make Expectation State
		for(int i=0;i<stmts.size();i++){
			st = stmts.get(i);

			if (st.toString().contains("return")) {
				break;
			}

			statement_str = replaceVariableNameUsedBySkeltonVariableName(st.toString());

			for (String varName : varTypeMap.keySet()) {
				String targetVarName = statement_str.split("\\.")[0];
				if (targetVarName.equals(varName)) {
					fieldTypeClass = varTypeMap.get(varName);
					break;
				}
			}

			statement_str = replaceMethodSignatureFollowSouceCode(statement_str,fieldTypeClass);
			sb.append("\r\t\t\t\t"+statement_str+"\n");
			//VerificationsInOrderのみにするのでtimesは不要
			//sb.append("\r\t\t\t\t"+"times=1;" + "\n");
		}

		mockMethodMap.put(methodSigNature_str, sb.toString());
		super.visit(n, arg);
	}

	//FIXME
	private String replaceMethodSignatureFollowSouceCode(String statement_str,MyClass fieldTypeClass) {
		Map<String, String> methodMap = methodChangeMap.get(fieldTypeClass);
		String[] split;
		String varName = null;
		String methodName = null;
		String replacedMethodSigNature_str = null;
		String replacedMethodName = null;
		StringBuilder methodSigNature_sb = new StringBuilder();
		JudgementMockType judgementMockType = new JudgementMockType();
		Pattern methodNamePattern = Pattern.compile("([0-9a-zA-Z_]+)\\(");
		Matcher methodNameMatcher = null;

		split = statement_str.split("\\.");
		varName = split[0];
		split = split[1].split("\\(");
		methodName = split[0];

		for (String skeltonMethod : methodMap.keySet()) {
			if (skeltonMethod.contains(methodName)) {
				replacedMethodSigNature_str = methodMap.get(skeltonMethod);
			}
		}

		methodNameMatcher = methodNamePattern.matcher(replacedMethodSigNature_str);
		//split = replacedMethodSigNature_str.split(" [");
		//replacedMethodName = split[split.length - 1].split("\\(")[0];

		if (methodNameMatcher.find()) {
			replacedMethodName = methodNameMatcher.group(1);
		}

		//FIXME
		//スケルトンコードのメソッドをソースコードのメソッドに変更する
		//また,パラメータがある場合はそれに合わせた引数にする
		//ex) mockObj.add(int num); => mockObj.add(1);
		methodSigNature_sb.append(varName);
		methodSigNature_sb.append(".");
		methodSigNature_sb.append(replacedMethodName);
		methodSigNature_sb.append("(");
		
		DisassemblyMethodSignature disassemblyMethod = new DisassemblyMethodSignature(replacedMethodSigNature_str);
		List<String> paramTypeList = disassemblyMethod.getParamTypeList();
		for(int i_paramTypeList=0;i_paramTypeList<paramTypeList.size();i_paramTypeList++){
			String type_str = paramTypeList.get(i_paramTypeList);

			methodSigNature_sb.append(judgementMockType.toDefaultValue_Str(type_str));

			if (i_paramTypeList < paramTypeList.size() - 1) {
				methodSigNature_sb.append(",");
			}
		}

		methodSigNature_sb.append(")");
		methodSigNature_sb.append(";");
		return methodSigNature_sb.toString();
	}

	private String replaceVariableNameUsedBySkeltonVariableName(String statement_str){
		String replaced_statement;
		String codeFieldName;

		for (String skeltonFieldName : variableFieldNameMap.keySet()) {
			//正規表現を使ってスケルトンコードから生成したフィールド変数名をコードの変数名にする
			//if (statement_str.matches(skeltonFieldName+"\\.[a-zA-Z_0-9]+\\(")) {
			if (statement_str.contains(skeltonFieldName+".")) {
				codeFieldName = variableFieldNameMap.get(skeltonFieldName);
				replaced_statement = statement_str.replaceFirst(skeltonFieldName+"\\.",codeFieldName+".");
				return replaced_statement;
			}
		}

		return statement_str;
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
