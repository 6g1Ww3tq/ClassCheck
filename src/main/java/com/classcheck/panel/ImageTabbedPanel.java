package com.classcheck.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		for (File file : files) {
			fileName = file.getName();
			matcher = pattern.matcher(fileName);
			
			if (matcher.find()) {
				tabPaneList.add(new ImageTab(file));
			}
		}
		
		for (ImageTab tabPanel : tabPaneList) {
			addTab(tabPanel.getPicFile().getName(), tabPanel);
		}
	}
	
}
