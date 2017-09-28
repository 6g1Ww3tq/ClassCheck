package com.mysample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloWorld {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String state = "LineSegment linesegment = new LineSegment()";
		//		String regex = ".+\\s+.+\\.(.+\\(.*\\));";
//		String regex = ".+\\s+.+\\..+\\(.*\\);";
		Pattern pattern = Pattern.compile(" ");
//		Matcher matcher = pattern.matcher(state);

		String ss[] = pattern.split(state, 0);
		
		if (ss.length > 0) {
			for (int i = 0; i < ss.length; i++) {
				String string = ss[i];
				System.out.println("(+"+i+")"+string);
			}
			
			System.out.println("length : " + ss.length);
		}
//		if (matcher.find()) {
//			state = matcher.group(1);
//			System.out.println(state);
//		}
		/*
		if (state.contains("LineSegment")) {
			state = state.replaceAll("LineSegment", "Dot");
		}
		 */
	}

}
