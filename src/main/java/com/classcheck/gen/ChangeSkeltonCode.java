package com.classcheck.gen;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.MemberTabPane;
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
	private ArrayList<String> generatedCodes;

	public ChangeSkeltonCode(MemberTabPane mtp,
			Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<MyClass, Map<String, String>> methodChangeMap) {
		fieldCodeMap = mtp.getFcp().getCodeMap();
		methodCodeMap = mtp.getMcp().getCodeMap();
		mtp.getMcp().getCodeMap();
		this.tableMap = tableMap;
		this.fieldChangeMap = fieldChangeMap;
		this.methodChangeMap = methodChangeMap;
		this.generatedCodes = new ArrayList<String>();
	}

	public ArrayList<String> getGeneratedCodes() {
		return generatedCodes;
	}

	public void change() {
		Set<MyClass> myClassKeySet = methodChangeMap.keySet();
		Map<String, String> fieldMap,methodMap;
		//変更したテキスト	
		StringBuilder sb = null;

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

			//フィールド変更
			fsr = new FieldSigReplace(myClass.toString());

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

				//TODO
				//メソッドのボディの変更(start.toString();)のようなメソッド名
				//まずフィールドの変数とクラス名を調べる
				//クラス名(myClass)からメソッドのパネルリストを調べる(メソッドの対応関係を調べる)
				
				mstr = new MethodStmtsReplace(
						methodChangeMap,
						frv.getVariableBefMap(),
						frv.getVariableAftMap(),
						cu.toString());
				mstr.replace();
				
				System.out.println(mstr.getBase());
			} catch (ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}


			//System.out.println(msr.getBase());

			//クラス名変更
			/*
				cnr = new ClassNameReplace(msr.getBase());
				cnr.setBefore(myClass.getClassSig());
				cnr.setAfter(methodCodeMap.get(myClass).getClassSig());
				cnr.replace();
			 */

			//クラス名とメソッド名の変更
			//System.out.println(cnr.getBase());

			DebugMessageWindow.msgToOutPutTextArea();

		}
	}
}