package com.classcheck.gen;

import java.util.ArrayList;

import com.classcheck.type.JudgementType;

public class InitialParamValues {

	private String constructorName_str;
	private String constructor_str;
	private StringBuilder defaultValues_sb;

	public InitialParamValues(String constructor_str) {
		this.constructor_str = constructor_str;
		this.defaultValues_sb = new StringBuilder();
	}

	public String setUpDefaultValues_str(){
		String[] split;
		String[] split_constructor_dec;
		String[] split_var_dec;
		String[] split_var_type;
		ArrayList<String> paramTypeList = new ArrayList<String>();

		split = this.constructor_str.split("\\(");
		split_constructor_dec = split[0].split(" ");
		//												コンストラクタの名前部分
		this.constructorName_str = split_constructor_dec[split_constructor_dec.length - 1];
		split = split[1].split("\\)");
		split_var_dec = split[0].split(",");

		for(int i_split_var_dec = 0;i_split_var_dec<split_var_dec.length;i_split_var_dec++){
			split_var_type = split_var_dec[i_split_var_dec].split(" ");
			paramTypeList.add(split_var_type[split_var_type.length - 2]);
		}
		
		this.defaultValues_sb.append(this.constructorName_str);
		this.defaultValues_sb.append("(");

		for(int i_paramTypeList=0;i_paramTypeList<paramTypeList.size();i_paramTypeList++){
			JudgementType jt = new JudgementType(paramTypeList.get(i_paramTypeList));
			this.defaultValues_sb.append(jt.toDefaultValue_Str());
			
			if (i_paramTypeList < paramTypeList.size() - 1) {
				this.defaultValues_sb.append(",");
			}
		}

		this.defaultValues_sb.append(")");
		return this.defaultValues_sb.toString();
	}
	
	@Override
	public String toString() {
		return defaultValues_sb.toString();
	}
}
