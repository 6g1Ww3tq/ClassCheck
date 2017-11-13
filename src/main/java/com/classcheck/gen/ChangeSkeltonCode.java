package com.classcheck.gen;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.MemberTabPanel;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class ChangeSkeltonCode {

	private Map<MyClass, CodeVisitor> tableMap;
	private Map<MyClass, CodeVisitor> methodCodeMap;
	private Map<MyClass, CodeVisitor> fieldCodeMap;
	private Map<MyClass, Map<String, String>> methodChangeMap;
	private Map<MyClass, Map<String, String>> fieldChangeMap;
	private Map<CodeVisitor, String> generatedCodesMap;

	public ChangeSkeltonCode(MemberTabPanel mtp,
			Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<MyClass, Map<String, String>> methodChangeMap) {
		fieldCodeMap = mtp.getFcp().getCodeMap();
		methodCodeMap = mtp.getMcp().getCodeMap();
		mtp.getMcp().getCodeMap();
		this.tableMap = tableMap;
		this.fieldChangeMap = fieldChangeMap;
		this.methodChangeMap = methodChangeMap;
		this.generatedCodesMap = new HashMap<CodeVisitor, String>();
	}

	public Map<CodeVisitor, String> getGeneratedCodesMap() {
		return generatedCodesMap;
	}

	public void change() {
		Set<MyClass> myClassKeySet = methodChangeMap.keySet();
		Map<String, String> fieldMap,methodMap;
		//スケルトンコードのメソッドリスト
		List<Method> methodList;
		//排除するメソッドをチェックするクラス
		CheckMember cm;
		//変更したテキスト	
		StringBuilder sb = null;
		
		//MyClassのフィールドを削除するためコピーを用意
		MyClass clMyClass = null;

		FieldSigReplace fsr = null;
		MethodSigReplace msr = null;
		ClassNameReplace cnr = null;

		CompilationUnit cu = null;
		FieldReplaceVisitor frv = null;
		MethodStmtsReplace mstr = null;

		DebugMessageWindow.clearText();

		for (MyClass myClass : myClassKeySet) {
			fieldMap = fieldChangeMap.get(myClass);
			methodMap = methodChangeMap.get(myClass);
			
			try {
				clMyClass = myClass.clone();
			} catch (CloneNotSupportedException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
			//空のメソッドやnewが使われているメソッドは除外する
			//コンストラクタも同様
			methodList = clMyClass.getMethods();
			cm = new CheckMember(methodList);
			cm.doCheck();
			clMyClass.setMethods(cm.getMemberList());
			
			//検査するメソッドが空の場合はスルー
			if (cm.getMemberList().isEmpty()) {
				continue ;
			}
			
			//フィールド変更
			fsr = new FieldSigReplace(clMyClass.toString());

			for (String befFieldStr : fieldMap.keySet()){
				fsr.setBefore(befFieldStr);
				fsr.setAfter(fieldMap.get(befFieldStr));
				fsr.replace();
			}

			//メソッド変更
			msr = new MethodSigReplace(fsr.getBase());

			for (String befMethodStr : methodMap.keySet()){
				msr.setBefore(befMethodStr);
				msr.setAfter(methodMap.get(befMethodStr));
				msr.replace();
			}

			try {
				//フィールドのクラス名を変更
				frv = new FieldReplaceVisitor(tableMap);
				cu = JavaParser.parse(new ByteArrayInputStream(msr.getBase().getBytes()));
				cu.accept(frv, null);

				//メソッドのステートメントを変更
				mstr = new MethodStmtsReplace(
						methodChangeMap,
						frv.getVariableBefMap(),
						frv.getVariableAftMap(),
						cu.toString());
				mstr.replace();

				//クラス名変更
				cnr = new ClassNameReplace(mstr.getBase());
				cnr.setBefore(myClass.getClassSig());
				cnr.setAfter(methodCodeMap.get(myClass).getClassSig());
				cnr.replace();

				System.out.println(cnr.getBase());
				
				//変換後のクラス名と内容を記録
				generatedCodesMap.put(methodCodeMap.get(myClass), cnr.getBase());
			} catch (ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			DebugMessageWindow.msgToOutPutTextArea();

		}
	}
}