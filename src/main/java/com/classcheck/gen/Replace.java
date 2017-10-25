package com.classcheck.gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public abstract class Replace {
	private String base;
	private String before;
	private String after;
	private ArrayList<Integer> lineNumList;

	public Replace(String base) {
		this.base= base;
		this.before = null;
		this.after = null;
		this.lineNumList = new ArrayList<Integer>();
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
		int lineNum = 0;
		boolean replaced = false;

		if (before==null || after==null) {
			return ;
		}

		try {
			while ((line = br.readLine()) != null) {
				lineNum++;

				if (!replaced) {
					//すでに前回、置換した行をまた置換しないようにする
					if (line.contains(before) && !lineNumList.contains(new Integer(lineNum))) {
						sb.append(StringUtils.replace(line, before, after));

						//置換した行数を記録しておく
						lineNumList.add(lineNum);
						//置換した
						replaced = true;
					}else{
						sb.append(line);
					}
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
