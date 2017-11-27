package com.classcheck.panel;

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
			editTextArea = new RSyntaxTextArea(20, 60);
			editTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
			editTextArea.setCodeFoldingEnabled(true);
			editTextArea.setText(defaultTestCode);
			scrollPane = new RTextScrollPane();
			scrollPane.setViewportView(editTextArea);
			scrollPane.setLineNumbersEnabled(true);
			addTab(exportFileName, icon, scrollPane);
			exportEditCodeMap.put(exportFileName, editTextArea);
		}
	}
}
