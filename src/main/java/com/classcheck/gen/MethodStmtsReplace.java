package com.classcheck.gen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;

public class MethodStmtsReplace extends Replace {

	private Map<MyClass, Map<String, String>> methodChangeMap;
	private HashMap<String, MyClass> variableBefMap;
	private HashMap<String, CodeVisitor> variableAftMap;
	private Set<String> varKeys;

	Pattern pattern;
	Matcher matcher;

	public MethodStmtsReplace(
			Map<MyClass, Map<String, String>> methodChangeMap,
			HashMap<String, MyClass> variableBefMap,
			HashMap<String, CodeVisitor> variableAftMap,
			String base) {
		super(base);
		this.methodChangeMap = methodChangeMap;
		this.variableBefMap = variableBefMap;
		this.variableAftMap = variableAftMap;
		this.varKeys = variableAftMap.keySet();
		this.pattern = Pattern.compile("\\..+\\(");

	}

	private boolean isMapNull() {
		if (methodChangeMap == null ||
				variableBefMap == null ||
				variableAftMap == null) {
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void replace() {
		setReplaceTimesLimit(1000);

		if (isMapNull()) {
			return ;
		}
		super.replace();
	}

	@Override
	public boolean canChange() {
		Map<String, String> methodMap;
		Pattern aftPtn = Pattern.compile(" [a-zA-Z_]+\\(");
		Matcher aftMatcher;

		for (String key : varKeys){
			
			if (line.contains(key)) {
				matcher = pattern.matcher(line);
				methodMap = methodChangeMap.get(variableBefMap.get(key));

				for(String befMethod : methodMap.keySet()){

					if (matcher.find()) {
						setBefore(matcher.group());
						aftMatcher = aftPtn.matcher(methodMap.get(befMethod));
						
						if (aftMatcher.find()) {
							setAfter("."+aftMatcher.group().replaceAll(" ", ""));
						}
						return line.contains(before) && !lineNumList.contains(new Integer(lineNum));
					}
				}
			}
		}

		return false;
	}

	@Override
	protected boolean isBeforeAfterNull() {
		return false;
	}

}
