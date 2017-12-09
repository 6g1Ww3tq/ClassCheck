package com.classcheck.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

public class Class_Sequence_ImageTabbedPanel extends JTabbedPane {

	File root;
	private ArrayList<String> classDiagramNameList;

	public Class_Sequence_ImageTabbedPanel(String exportPath, ArrayList<String> classDiagramNameList) {
		root = new File(exportPath);
		this.classDiagramNameList = classDiagramNameList;

		initComponent();
	}

	private void initComponent() {
		List<Class_Sequence_ImageTab> tabPaneList = new ArrayList<Class_Sequence_ImageTab>();
		File[] files = root.listFiles();
		String fileName,classDiagramName;
		Pattern	pattern = Pattern.compile("(.+)\\.(png)$");
		Matcher matcher;
		ImageIcon classIcon = new ImageIcon(getClass().getResource("/icons/class_image.png"));
		ImageIcon sequenceIcon = new ImageIcon(getClass().getResource("/icons/sequence_image.png"));

		for (File file : files) {
			fileName = file.getName();
			matcher = pattern.matcher(fileName);

			System.out.println("fileName:"+fileName);

			if (matcher.find()) {
				classDiagramName = matcher.group(1);
				tabPaneList.add(new Class_Sequence_ImageTab(classDiagramName,file));
			}
		}

		for (Class_Sequence_ImageTab tabPanel : tabPaneList) {

			//もじクラス図の場合はクラス図のアイコンを使う
			if (this.classDiagramNameList.contains(tabPanel.getClassDiagramName())) {
				addTab(tabPanel.getClassDiagramName(), classIcon, tabPanel, null);
			
				//それ以外はシーケンス図のアイコンを使う
			}else{
				addTab(tabPanel.getClassDiagramName(), sequenceIcon, tabPanel, null);
			}
		}
	}

}
