package com.classcheck.analyzer.source;

import java.io.ByteArrayInputStream;

import com.classcheck.autosource.MyClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class SkeltonCodeAnalyzer {
	private CompilationUnit unit;
	
	private SkeltonCodeVisitor skeltonCodeVisitor;
	
	public SkeltonCodeAnalyzer(MyClass myClass) {
		skeltonCodeVisitor = new SkeltonCodeVisitor();
		String strData = myClass.toString();
		ByteArrayInputStream bais = new ByteArrayInputStream(strData.getBytes());
		
		try {
			unit = JavaParser.parse(bais);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	public void doAnalyze(){
		skeltonCodeVisitor.visit(unit, null);
	}
	
	public SkeltonCodeVisitor getSkeltonCodeVisitor() {
		return skeltonCodeVisitor;
	}
}
