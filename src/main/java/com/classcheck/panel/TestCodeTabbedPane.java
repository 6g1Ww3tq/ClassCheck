package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class TestCodeTabbedPane extends JTabbedPane {

	private Map<String, String> exportFileMap;
	private HashMap<String, TextArea> exportEditCodeMap;

	public TestCodeTabbedPane(Map<String, String> exportFileMap) {
		this.exportFileMap = exportFileMap;
		this.exportEditCodeMap = new HashMap<String, TextArea>();
		initComponent();
	}

	public HashMap<String, TextArea> getExportEditCodeMap() {
		return exportEditCodeMap;
	}

	private void initComponent() {
		JPanel panel = null;
		TextArea editTextArea = null;
		JScrollPane scrollPane = null;
		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/sequence_image.png"));

		for (String exportFileName : exportFileMap.keySet()) {
			String defaultTestCode = exportFileMap.get(exportFileName);
			int currentLineNumber = getEditLineNumber(defaultTestCode);
			//ユーザーが修正する行数に移動する
			editTextArea = new TextArea(20,60);
			editTextArea.setText(defaultTestCode);
			editTextArea.setCaretPosition(currentLineNumber);
			panel = new JPanel(new BorderLayout());
			panel.add(editTextArea);
			scrollPane = new JScrollPane(panel);
			addTab(exportFileName, icon, scrollPane);
			exportEditCodeMap.put(exportFileName, editTextArea);
		}
	}

	private int getEditLineNumber(String testCode_Str){
		int targetLineNumber = 1;
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(testCode_Str.getBytes())));
		String line;
		
		try {
			int readLineNumber = 1;
			while ((line = br.readLine()) != null) {
				if (line.contains("コンストラクタを編集してください")) {
					targetLineNumber = readLineNumber;
					break;
				}
				readLineNumber++;
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

		return targetLineNumber;
	}
}
