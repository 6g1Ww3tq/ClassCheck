package com.classcheck.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

public class ClassImageTabbedPanel extends JTabbedPane {

	File root;
	
	public ClassImageTabbedPanel(String exportPath) {
		root = new File(exportPath);
		
		initComponent();
	}

	private void initComponent() {
		List<ClassImageTab> tabPaneList = new ArrayList<ClassImageTab>();
		File[] files = root.listFiles();
		String fileName,classDiagramName;
		Pattern	pattern = Pattern.compile("(.+)\\.(png)$");
		Matcher matcher;
		ImageIcon classIcon = new ImageIcon(getClass().getResource("/icons/class_image.png"));
		
		for (File file : files) {
			fileName = file.getName();
			matcher = pattern.matcher(fileName);
			
			System.out.println("fileName:"+fileName);
			
			if (matcher.find()) {
				classDiagramName = matcher.group(1);
				tabPaneList.add(new ClassImageTab(classDiagramName,file));
			}
		}
		
		for (ClassImageTab tabPanel : tabPaneList) {
			addTab(tabPanel.getClassDiagramName(), classIcon, tabPanel, null);
		}
	}
	
}
