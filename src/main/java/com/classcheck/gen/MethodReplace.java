package com.classcheck.gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;

public class MethodReplace {

	private String myClassStr;
	private String beforeMethodSig;
	private String afterMethodSig;

	public MethodReplace(String myClassStr) {
		this.myClassStr = myClassStr;
		this.beforeMethodSig = null;
		this.afterMethodSig = null;
	}
	
	public String getMyClassStr() {
		return myClassStr;
	}
	
	public void setBeforeMethodSig(String beforeMethodSig) {
		this.beforeMethodSig = beforeMethodSig.replaceAll(" : ", "");
	}

	public void setAfterMethodSig(String afterMethodSig) {
		this.afterMethodSig = afterMethodSig;
	}

	public void replace(){
		StringBuilder sb = new StringBuilder();
		StringReader sr = new StringReader(myClassStr);
		BufferedReader br = new BufferedReader(sr);
		String line = null;

		if (beforeMethodSig==null || afterMethodSig==null) {
			return ;
		}

		try {
			while ((line = br.readLine()) != null) {
				if (line.contains(beforeMethodSig)) {
					sb.append(StringUtils.replace(line, beforeMethodSig, afterMethodSig));
				}else{
					sb.append(line);
				}
				sb.append('\n');
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
		
		this.myClassStr = sb.toString();
	}
}