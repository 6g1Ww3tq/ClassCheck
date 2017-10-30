package com.classcheck.panel;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.classcheck.analyzer.source.CodeVisitor;

public class ConstructorTabbedPane extends JTabbedPane {

	private ArrayList<ConstructorPane> constructorPaneList;
	private Set<CodeVisitor> codeSet;


	public ConstructorTabbedPane(Set<CodeVisitor> codeSet) {
		this.codeSet = codeSet;
		initComponent();
		setVisible(true);
	}

	private void initComponent() {
		JScrollPane jscrollPane;
		ConstructorPane constructorPane = null;
		this.constructorPaneList = new ArrayList<ConstructorPane>();
		
		for(CodeVisitor codeVisitor : codeSet){
			constructorPaneList.add(new ConstructorPane(codeVisitor));
		}
		
		//タブを加える
		for(Iterator it = constructorPaneList.iterator(); it.hasNext() ;){
			constructorPane = (ConstructorPane) it.next();
			jscrollPane = new JScrollPane();
			jscrollPane.setViewportView(constructorPane);
			addTab(constructorPane.getName(),jscrollPane);
		}
	}
}
