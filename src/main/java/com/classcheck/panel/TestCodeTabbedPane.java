package com.classcheck.panel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class TestCodeTabbedPane extends JTabbedPane {

	private Map<String, String> exportFileMap;
	private HashMap<String, RSyntaxTextArea> exportEditCodeMap;

	public TestCodeTabbedPane(Map<String, String> exportFileMap) {
		this.exportFileMap = exportFileMap;
		this.exportEditCodeMap = new HashMap<String, RSyntaxTextArea>();
		initComponent();
	}

	public HashMap<String, RSyntaxTextArea> getExportEditCodeMap() {
		return exportEditCodeMap;
	}

	private void initComponent() {
		RSyntaxTextArea editTextArea = null;
		RTextScrollPane scrollPane = null;
		ImageIcon icon = new ImageIcon(getClass().getResource("/icons/sequence_image.png"));

		for (String exportFileName : exportFileMap.keySet()) {
			String defaultTestCode = exportFileMap.get(exportFileName);
			int currentLineNumber = getEditLineNumber(defaultTestCode);
			editTextArea = new RSyntaxTextArea(20, 60);
			editTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
			editTextArea.setCodeFoldingEnabled(true);
			editTextArea.setText(defaultTestCode);
			//ユーザーが修正する行数に移動する
			editTextArea.setCaretPosition(currentLineNumber);
			scrollPane = new RTextScrollPane();
			scrollPane.setViewportView(editTextArea);
			scrollPane.setLineNumbersEnabled(true);
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
