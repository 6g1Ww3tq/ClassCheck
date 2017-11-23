package com.classcheck.gen;

public class ClassNameReplace extends Replace {

	public ClassNameReplace(String base) {
		super(base);
	}

	@Override
	public boolean canChange() {
		return line.contains(before) && !lineNumList.contains(new Integer(lineNum));
	}
}
