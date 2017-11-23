package com.classcheck.gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public abstract class Replace implements Strategy{
	String base;
	String before;
	String after;
	ArrayList<Integer> lineNumList;

	StringBuilder sb;
	StringReader sr;
	BufferedReader br;
	String line;
	int lineNum;
	private int replaceTimesLimit;
	boolean replaced;

	public Replace(String base) {
		this.base= base;
		this.before = null;
		this.after = null;
		this.sb = null;
		this.sr = null;
		this.br = null;
		this.line = null;
		this.lineNum = 0;
		this.replaced = false;
		this.replaceTimesLimit = 0;
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

	public void setReplaceTimesLimit(int times){
		this.replaceTimesLimit = times;
	}

	public void replace(){
		sb = new StringBuilder();
		sr = new StringReader(base);
		br = new BufferedReader(sr);
		line = null;
		lineNum = 0;
		replaced = false;
		int replaceTimes = 0;

		if (isBeforeAfterNull()) {
			return ;
		}

		try {
			while ((line = br.readLine()) != null) {
				lineNum++;

				//すでに前回、置換した行をまた置換しないようにする
				if (canChange() && canReplace(replaceTimes)) {
					sb.append(StringUtils.replace(line, before, after));

					//置換した行数を記録しておく
					lineNumList.add(lineNum);
					//置換した
					replaceTimes++;
				}else{
					sb.append(line);
				}

				sb.append('\n');
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		this.base = sb.toString();
	}

	protected boolean isBeforeAfterNull() {
		if (before==null || after==null) {
			return true;
		}else{
			return false;
		}
	}

	private boolean canReplace(int replaceTimes) {

		if (replaceTimes <= replaceTimesLimit) {
			return true;
		}else{
			return false;
		}
	}
}
