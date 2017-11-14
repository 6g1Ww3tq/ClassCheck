package com.classcheck.type;

import javax.swing.JComboBox;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
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
	
	/** クラス図で定義されたメソッド　*/
	private Method umlMethod;
	/** ソースコードで定義されたメソッド　*/
	private MethodDeclaration codeMethod;

	public BasicType(Method umlMethod, MethodDeclaration codeMethod) {
		this.umlMethod = umlMethod;
		this.codeMethod = codeMethod;
	}

	public boolean evaluate() {
		boolean rtnVal = false;
		//型が配列かどうか
		boolean isArrayCode = false;
		boolean isArrayUML = false;
		String methodRtnType = umlMethod.getReturntype();
		methodRtnType = methodRtnType.replaceAll(" ", ""); //空白を削除（「void 」)
		String splits[] = codeMethod.getDeclarationAsString().split("\\(");
		splits = splits[0].split(" ");
		String methodDecRtnType = splits[splits.length - 2];
		
		if (methodRtnType.contains("[]")) {
			isArrayUML = true;
			methodRtnType = methodRtnType.replaceAll("\\[\\]", "");
		}
		
		if(methodDecRtnType.contains("[]")){ 
			isArrayCode = true;
			methodDecRtnType = methodDecRtnType.replaceAll("\\[\\]", "");
		}
		
		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
			return rtnVal;
		}

		if (isBasic(methodRtnType) && 
				isBasic(methodDecRtnType)) {
			if (methodRtnType.equals(methodDecRtnType)) {
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
