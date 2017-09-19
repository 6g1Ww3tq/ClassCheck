package com.classcheck.gen;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.classcheck.window.DebugMessageWindow;

public class GenerateTestProgram {
	//出力元となるディレクトリ
	File baseDir;
	//出力先テストディレクトリ
	File outDir;

	public GenerateTestProgram(File baseDir) {
		this.baseDir = baseDir;
		makeTestDir();
		makeHelloFile();
	}

	private void makeHelloFile() {
		try {
			FileUtils.writeStringToFile(new File(outDir.getPath()+"/hello.txt"), "hello world");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private void makeTestDir() {
		outDir = new File(baseDir.getPath()+"/test");
		System.out.println(outDir);
		DebugMessageWindow.msgToOutPutTextArea();
		try {
			FileUtils.forceMkdir(outDir);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
}