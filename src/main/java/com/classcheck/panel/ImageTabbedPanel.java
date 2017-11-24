package com.classcheck.panel;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class ImageTabbedPanel extends JTabbedPane {

	File root;
	
	public ImageTabbedPanel(String exportPath) {
		root = new File(exportPath);
		
		initComponent();
	}

	private void initComponent() {
		List<ImageTab> tabPaneList = new ArrayList<ImageTab>();
		File[] files = root.listFiles();
		String fileName;
		Pattern	pattern = Pattern.compile(".+\\.(png)$");
		Matcher matcher;
		JScrollPane scrollPane;
		JPanel panel;
		
		for (File file : files) {
			fileName = file.getName();
			matcher = pattern.matcher(fileName);
			
			if (matcher.find()) {
				tabPaneList.add(new ImageTab(file));
			}
		}
		
		for (ImageTab tabPanel : tabPaneList) {
			panel = new JPanel(new BorderLayout());
			scrollPane = new JScrollPane(tabPanel);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			panel.add(scrollPane,BorderLayout.CENTER);
			addTab(tabPanel.getPicFile().getName(), panel);
		}
	}
	
}
