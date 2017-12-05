package com.classcheck.type;

import java.util.HashMap;
import java.util.Map;

public class JudgementMockType {
	private Map typeValuesMap;

	public JudgementMockType() {
		initTypeValuesMap();
	}

	private void initTypeValuesMap() {
		this.typeValuesMap = new HashMap<String, String>();

		typeValuesMap.put("boolean", "anyBoolean");
		typeValuesMap.put("char", "anyChar");
		typeValuesMap.put("byte", "anyByte");
		typeValuesMap.put("short", "anyShort");
		typeValuesMap.put("int", "anyInt");
		typeValuesMap.put("long", "anyLong");
		typeValuesMap.put("float", "anyFloat");
		typeValuesMap.put("double", "anyDouble");
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
