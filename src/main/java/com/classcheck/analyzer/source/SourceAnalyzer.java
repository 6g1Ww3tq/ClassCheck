package com.classcheck.analyzer.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class SourceAnalyzer {
	private CompilationUnit unit;

	private CodeVisitor codeVisitor;

	public SourceAnalyzer(File file) throws IOException{
		codeVisitor = new CodeVisitor();
		FileInputStream in = new FileInputStream(file);

		try {
			unit = JavaParser.parse(in);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public void doAnalyze() {
		codeVisitor.visit(unit,null);
	}
	
	public CodeVisitor getCodeVisitor() {
		return codeVisitor;
	}
}