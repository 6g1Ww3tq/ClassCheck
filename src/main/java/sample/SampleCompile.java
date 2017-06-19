package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

public class SampleCompile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> command = new ArrayList<String>();
		ProcessBuilder pb = new ProcessBuilder();

		command.add("javac");
		command.add("/home/masa/workspace/report/java/cui/input/sample/Sample.java");
		command.add("-d");
		command.add("/home/masa/workspace/report/java/cui/input/sample/");
		pb.command(command);
		try {
			Process p = pb.start();

			p.waitFor();

			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder sb = new StringBuilder();

			for(String line = br.readLine(); line != null ; line = br.readLine()){
				sb.append(line);
			}

			System.out.println(sb.toString());
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}
}