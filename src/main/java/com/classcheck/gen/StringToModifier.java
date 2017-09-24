package com.classcheck.gen;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class StringToModifier {
	private String modStr;

	public StringToModifier(String modStr) {
		this.modStr = modStr;
	}

	public ArrayList<Integer> toModifierList(){
		ArrayList<Integer> modList = new ArrayList<Integer>();

		if (modStr.contains("private")) {
			modList.add(Modifier.PUBLIC);
		}
		if (modStr.contains("public")) {
			modList.add(Modifier.PUBLIC);
		}
		if (modStr.contains("protected")) {
			modList.add(Modifier.PROTECTED);
		}
		if (modStr.contains("static")) {
			modList.add(Modifier.STATIC);
		}
		if (modStr.contains("final")) {
			modList.add(Modifier.FINAL);
		}
		if (modStr.contains("abstract")) {
			modList.add(Modifier.ABSTRACT);
		}

		return modList;
	}
}
