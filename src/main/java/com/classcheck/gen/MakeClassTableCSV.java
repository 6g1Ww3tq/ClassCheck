package com.classcheck.gen;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;

public class MakeClassTableCSV {

	private Map<MyClass, CodeVisitor> tableMap;
	private Map<MyClass, Map<String, String>> fieldChangeMap;
	private Map<MyClass, Map<String, String>> methodChangeMap;
	private String exportFileName;

	public MakeClassTableCSV(String exportFileName,
			Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<MyClass, Map<String, String>> methodChangeMap) {
		if (exportFileName.contains(".csv") == false) {
			exportFileName += exportFileName + ".csv";
		}
		this.exportFileName = exportFileName;
		this.tableMap = tableMap;
		this.fieldChangeMap = fieldChangeMap;
		this.methodChangeMap = methodChangeMap;
	}

	public void exportPath(String path) {
		try {
			StringBuilder csvData_sb = new StringBuilder();
			Set<MyClass> myClassSet = tableMap.keySet();
			
			for (MyClass myClass : myClassSet) {
				CodeVisitor codeVisitor = tableMap.get(myClass);
			}
			
			//ファイル出力
			FileUtils.writeStringToFile(new File(path+"/"+exportFileName), csvData_sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
