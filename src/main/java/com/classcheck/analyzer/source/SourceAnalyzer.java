package com.classcheck.analyzer.source;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class SourceAnalyzer {
	private static final int READ_ERROR = -1;

	ASTParser parser;
	CompilationUnit unit;

	public SourceAnalyzer(File file) throws IOException{
		StringBuilder sb = new StringBuilder();
		int data;
		FileReader fr = new FileReader(file);

		while ((data = fr.read()) != READ_ERROR) {
			sb.append((char)data);
		}

		parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(sb.toString().toCharArray());
		unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());

	}

	public void accept(ASTVisitor visitor){
		unit.accept(visitor);
	}

}
