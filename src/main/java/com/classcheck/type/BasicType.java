package com.classcheck.type;

import javax.swing.JComboBox;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.Field;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class BasicType {

	private static final String[] basicTypes = {
		"void", //本当は基本型ではない
		"byte",
		"short",
		"int",
		"long",
		"float",
		"double",
		"char",
		"boolean"
	};

	/** クラス図で定義された型　*/
	private String umlType;
	/** ソースコードで定義された型　*/
	private String codeType;

	public BasicType(Method umlMethod, MethodDeclaration codeMethod) {
		initMethod(umlMethod, codeMethod);
	}

	public BasicType(Field umlField, FieldDeclaration codeField) {
		initField(umlField,codeField);
	}

	private void initField(Field umlField, FieldDeclaration codeField) {
		umlType = umlField.getType();
		codeType = codeField.toString();
		String splits[] = codeType.split("=");
		splits = splits[0].split(" ");
		codeType = splits[splits.length - 2];

		System.out.println("umlType(R):"+umlType);
		System.out.println("codeType(B):"+codeType);
	}

	private void initMethod(Method umlMethod, MethodDeclaration codeMethod) {
		umlType = umlMethod.getReturntype();
		umlType = umlType.replaceAll(" ", ""); //空白を削除（「void 」)
		String splits[] = codeMethod.getDeclarationAsString().split("\\(");
		splits = splits[0].split(" ");
		codeType = splits[splits.length - 2];
	}

	public boolean evaluate() {
		boolean rtnVal = false;
		//型が配列かどうか
		boolean isArrayCode = false;
		boolean isArrayUML = false;

		if (umlType.contains("[]")) {
			isArrayUML = true;
			umlType = umlType.replaceAll("\\[\\]", "");
		}

		if(codeType.contains("[]")){ 
			isArrayCode = true;
			codeType = codeType.replaceAll("\\[\\]", "");
		}

		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
			return rtnVal;
		}

		if (isBasic(umlType) && 
				isBasic(codeType)) {
			if (umlType.equals(codeType)) {
				rtnVal = true;
			}
		}

		return rtnVal;
	}

	private boolean isBasic(String type){
		boolean rtnVal = false;

		for(int i=0;i<basicTypes.length;i++){

			if (basicTypes[i].equals(type)) {
				rtnVal = true;
				break;
			}
		}

		return rtnVal;
	}

}
