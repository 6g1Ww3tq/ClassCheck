package com.classcheck.gen;

public class FieldSigReplace extends Replace {

	public FieldSigReplace(String base) {
		super(base);
	}

	@Override
	public boolean canChange() {
		return line.contains(before) && !lineNumList.contains(new Integer(lineNum));
	}
}
