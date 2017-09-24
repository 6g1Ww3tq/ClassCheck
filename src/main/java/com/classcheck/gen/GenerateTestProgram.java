package com.classcheck.gen;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.AstahAndSourcePanel;
import com.classcheck.panel.CompTablePane;
import com.classcheck.window.DebugMessageWindow;

public class GenerateTestProgram {
	//出力元となるディレクトリ
	File baseDir;
	//出力先テストディレクトリ
	File outDir;

	private Map<MyClass, List<JPanel>> mapPanelList;
	private Map<MyClass, CodeVisitor> codeMap;
	private AstahAndSourcePanel astahAndSourcePane;

	public GenerateTestProgram(File baseDir,
			AstahAndSourcePanel astahAndSourcePane) {
		this.baseDir = baseDir;
		this.astahAndSourcePane = astahAndSourcePane;
		this.mapPanelList = astahAndSourcePane.getMapPanelList();
		this.codeMap = astahAndSourcePane.getCodeMap();
		makeTestDir();
		makeHelloFile();
	}

	private void makeHelloFile() {
		try {
			FileUtils.writeStringToFile(new File(outDir.getPath()+"/hello.txt"), "hello world");
			new ChangeMyClass(astahAndSourcePane,CompTablePane.getTableModel()).change();
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