package com.classcheck.type;

import java.util.HashMap;
import java.util.Map;

public class JudgementBasicType {

	private Map typeValuesMap;

	public JudgementBasicType() {
		initTypeValuesMap();
	}

	private void initTypeValuesMap() {
		this.typeValuesMap = new HashMap<String, String>();

		typeValuesMap.put("boolean", "false");
		typeValuesMap.put("char", "'a'");
		typeValuesMap.put("byte", "0");
		typeValuesMap.put("short", "0");
		typeValuesMap.put("int", "0");
		typeValuesMap.put("long", "0");
		typeValuesMap.put("float", "0");
		typeValuesMap.put("double", "0");
	}

	public String toDefaultValue_Str(String paramType_str) {
		String defaultValue_str = null;

		if (typeValuesMap.containsKey(paramType_str)) {
			//基本型の初期値
			Object obj = typeValuesMap.get(paramType_str);

			if (obj instanceof String) {
				defaultValue_str = (String) obj;
			}

		}else{
			//参照型の初期値
			defaultValue_str="null";
		}

		return defaultValue_str;
	}

}
