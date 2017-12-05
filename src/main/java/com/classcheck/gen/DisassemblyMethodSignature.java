package com.classcheck.gen;

import java.util.ArrayList;

import com.classcheck.type.JudgementBasicType;

public class DisassemblyMethodSignature {

	private String methodName;
	private String methodSignature;
	private ArrayList<String> paramTypeList;

	public DisassemblyMethodSignature(String methodSigNature_str) {
		this.methodName = null;
		this.methodSignature = methodSigNature_str;
		this.paramTypeList = new ArrayList<String>();

		initDisassembly();
	}

	public String getMethodName() {
		return methodName;
	}

	public ArrayList<String> getParamTypeList() {
		return paramTypeList;
	}

	private void initDisassembly() {
		methodNameDisassembly();
		paramDisassembly();
	}

	private void methodNameDisassembly() {
		String[] split;
		String[] split_method_dec;

		split = this.methodSignature.split("\\(");
		split_method_dec = split[0].split(" ");
		this.methodName = split_method_dec[split_method_dec.length - 1];
	}

	private void paramDisassembly() {
		String[] split;
		String[] split_var_dec;
		String[] split_var_type;

		try{
			
			split = this.methodSignature.split("\\(");
			split = split[1].split("\\)");
			split_var_dec = split[0].split(",");

			for(int i_split_var_dec = 0;i_split_var_dec<split_var_dec.length;i_split_var_dec++){
				split_var_type = split_var_dec[i_split_var_dec].split(" ");
				paramTypeList.add(split_var_type[split_var_type.length - 2]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			/*
			 * split,split_var_dec,split_var_type
			 * いずれかの配列のインデックスがない場合
			 */
		}

	}

	@Override
	public String toString() {
		StringBuilder paramsDefaultValues_sb = new StringBuilder();
		JudgementBasicType jt = new JudgementBasicType();
		paramsDefaultValues_sb.append(methodName);
		paramsDefaultValues_sb.append("(");

		for(int i_paramTypeList=0;i_paramTypeList<paramTypeList.size();i_paramTypeList++){
			paramsDefaultValues_sb.append(jt.toDefaultValue_Str(paramTypeList.get(i_paramTypeList)));

			if (i_paramTypeList < paramTypeList.size() - 1) {
				paramsDefaultValues_sb.append(",");
			}
		}

		paramsDefaultValues_sb.append(")");
		return paramsDefaultValues_sb.toString();
	}
}
