package com.classcheck.analyzer.source;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class SourceAnalyzer {
	//private StringBuilder sbMsg;
	private CompilationUnit unit;

	//Analyze Signature
	private CodeVisitor codeVisitor;
	private SampleMethodVisitor sample_mv;

	public SourceAnalyzer(File file) throws IOException{
		//StringBuilder Messenger Init
		//sbMsg = new StringBuilder();
		codeVisitor = new CodeVisitor();
//		sample_mv = new SampleMethodVisitor();
		// creates an input stream for the file to be parsed
		FileInputStream in = new FileInputStream(file);

		try {
			unit = JavaParser.parse(in);
			//All print in Java Code
			//sbMsg.append(unit.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public void doAnalyze() {
		codeVisitor.visit(unit,null);
//		sample_mv.visit(unit,null);
	}
	
	public CodeVisitor getCodeVisitor() {
		return codeVisitor;
	}
}