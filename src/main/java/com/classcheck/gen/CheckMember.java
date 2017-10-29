package com.classcheck.gen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.classcheck.autosource.Method;

public class CheckMember {

	private List<Method> methodList;

	public CheckMember(List<Method> methodList) {
		this.methodList = methodList;
	}

	public void doCheck(){
		Method method;
		String methodBody;
		List<Method> removeMethods = new ArrayList<Method>();

		for(int i = 0;i<methodList.size() ;i++){
			method = methodList.get(i);

			methodBody = method.getMethodBody();

			if (writtenNewSt(methodBody) ||
					bodyCounter(methodBody) <= 0) {
				removeMethods.add(method);
			}
		}
		
		for(int i =0;i<removeMethods.size();i++){
			methodList.remove(removeMethods.get(i));
		}

	}

	public List<Method> getMemberList() {
		return methodList;
	}

	private boolean writtenNewSt(String body) {
		StringReader sr = new StringReader(body);
		BufferedReader br = new BufferedReader(sr);
		String line;
		Pattern pattern = Pattern.compile("=\\s+new\\s");
		Matcher matcher;

		try {
			while((line = br.readLine()) != null){
				
				matcher = pattern.matcher(line);

				if (matcher.find()){
					return true;
				}
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return false;
	}

	private int bodyCounter(String body){

		if (body == null || body.equals("")) {
			return 0;
		}

		int lineNum = 0;
		StringReader sr = new StringReader(body);
		BufferedReader br = new BufferedReader(sr);
		String line;
		boolean written_return = false;

		try {

			while((line = br.readLine()) != null){

				if (line.contains("return 0")){
					written_return = true;
				}else if (line.contains("return null")) {
					written_return = true;
				}else if (line.contains("return false")) {
					written_return = true;
				}else if (line.contains("return true")) {
					written_return = true;
				}

				lineNum++;
			}

			br.close();

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		//１行のみで「return null,0,false」しか書かれていない場合も０行とみなす
		if (written_return && lineNum <= 1) {
			lineNum = 0;
		}

		return lineNum;
	}
}
