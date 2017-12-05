package com.mysample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloRegex {
	public static void main(String[] args) {
		String target = "public void hoge(int a);";
		Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\\(");
		Matcher matcher = pattern.matcher(target);
		
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		}
	}
}
