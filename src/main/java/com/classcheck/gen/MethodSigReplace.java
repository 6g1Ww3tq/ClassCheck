package com.classcheck.gen;


public class MethodSigReplace extends Replace{

	public MethodSigReplace(String myClassStr) {
		super(myClassStr);
	}
	
	@Override
	public void setBefore(String before) {
		super.setBefore(before.replaceAll(" : ", ""));
	}
	
	@Override
	public boolean canChange() {
		return line.contains(before) && !lineNumList.contains(new Integer(lineNum));
	}
}