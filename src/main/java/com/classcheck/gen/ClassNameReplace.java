package com.classcheck.gen;

public class ClassNameReplace extends Replace {

	public ClassNameReplace(String base) {
		super(base);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean canChange() {
		return line.contains(before) && !lineNumList.contains(new Integer(lineNum));
	}
}
