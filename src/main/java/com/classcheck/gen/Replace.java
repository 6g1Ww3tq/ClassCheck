package com.classcheck.gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;

public abstract class Replace {
	private String base;
	private String before;
	private String after;

	public Replace(String base) {
		this.base= base;
		this.before = null;
		this.after = null;
	}
	
	public String getBase() {
		return base;
	}
	
	public void setBefore(String before) {
		this.before = before;
	}
	
	public void setAfter(String after) {
		this.after = after;
	}

	public void replace(){
		StringBuilder sb = new StringBuilder();
		StringReader sr = new StringReader(base);
		BufferedReader br = new BufferedReader(sr);
		String line = null;

		if (before==null || after==null) {
			return ;
		}

		try {
			while ((line = br.readLine()) != null) {
				if (line.contains(before)) {
					sb.append(StringUtils.replace(line, before, after));
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
		
		this.base = sb.toString();
	}
}
