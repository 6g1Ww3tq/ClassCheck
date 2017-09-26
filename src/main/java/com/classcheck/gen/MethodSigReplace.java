package com.classcheck.gen;


public class MethodSigReplace extends Replace{

	public MethodSigReplace(String myClassStr) {
		super(myClassStr);
	}
	
	@Override
	public void setBefore(String before) {
		// TODO 自動生成されたメソッド・スタブ
		super.setBefore(before.replaceAll(" : ", ""));
	}
}