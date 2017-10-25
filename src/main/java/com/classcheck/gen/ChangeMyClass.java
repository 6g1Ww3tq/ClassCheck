package com.classcheck.gen;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.MemberTabPane;
import com.classcheck.window.DebugMessageWindow;

public class ChangeMyClass {

	private Map<MyClass, CodeVisitor> methodCodeMap;
	private Map<MyClass, CodeVisitor> fieldCodeMap;
	private Map<MyClass, Map<String, String>> methodChangeMap;
	private Map<MyClass, Map<String, String>> fieldChangeMap;
	private ArrayList<String> generatedCodes;

	public ChangeMyClass(MemberTabPane mtp,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<MyClass, Map<String, String>> methodChangeMap) {
		fieldCodeMap = mtp.getFcp().getCodeMap();
		methodCodeMap = mtp.getMcp().getCodeMap();
		mtp.getMcp().getCodeMap();
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

			//TODO
			//メソッドのボディの変更(start.toString();)のようなメソッド名
			//まずフィールドの変数とクラス名を調べる
			//クラス名(myClass)からメソッドのパネルリストを調べる(メソッドの対応関係を調べる)

			
			System.out.println(msr.getBase());

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