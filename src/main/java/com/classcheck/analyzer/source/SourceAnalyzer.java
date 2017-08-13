package com.classcheck.analyzer.source;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class SourceAnalyzer {
	private StringBuilder sbMsg;
	private CompilationUnit unit;

	//Analyze Signature
	private MethodVisitor mv;
	private SampleMethodVisitor sample_mv;
	private AttributeVisitor attrV;

	public SourceAnalyzer(File file) throws IOException{
		//StringBuilder Messenger Init
		sbMsg = new StringBuilder();
//		mv = new MethodVisitor();
		sample_mv = new SampleMethodVisitor();
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
//		mv.visit(unit,null);
		sample_mv.visit(unit,null);
	}

	public String getMessage() {
//		sbMsg.append(mv.getMessage());
		sbMsg.append(sample_mv.getMessage());
		
		return sbMsg.toString();
	}
}