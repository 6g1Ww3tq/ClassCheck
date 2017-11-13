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

public class ConstructorTabbedPanel extends JTabbedPane {

	private ArrayList<ConstructorPanel> constructorPaneList;
	private Set<CodeVisitor> codeSet;


	public ConstructorTabbedPanel(Set<CodeVisitor> codeSet) {
		this.codeSet = codeSet;
		initComponent();
		setVisible(true);
	}
	
	public ArrayList<ConstructorPanel> getConstructorPaneList() {
		return constructorPaneList;
	}

	private void initComponent() {
		JScrollPane jscrollPane;
		ConstructorPanel constructorPane = null;
		this.constructorPaneList = new ArrayList<ConstructorPanel>();
		
		for(CodeVisitor codeVisitor : codeSet){
			constructorPaneList.add(new ConstructorPanel(codeVisitor));
		}
		
		//タブを加える
		for(Iterator it = constructorPaneList.iterator(); it.hasNext() ;){
			constructorPane = (ConstructorPanel) it.next();
			jscrollPane = new JScrollPane();
			jscrollPane.setViewportView(constructorPane);
			addTab(constructorPane.getName(),jscrollPane);
		}
	}
}
