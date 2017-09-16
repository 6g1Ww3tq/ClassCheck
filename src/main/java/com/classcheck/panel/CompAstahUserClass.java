package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;

public class CompAstahUserClass extends JPanel implements Serializable{

	DefaultTableModel tableModel;
	JTable methodTable;
	private ClassBuilder cb;
	private List<CodeVisitor> codeVisitorList;

	public CompAstahUserClass(ClassBuilder cb, List<CodeVisitor> codeVisitorList) {
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
		setSize(450,300);
		setLayout(new BorderLayout());
		initComponent();
		setVisible(true);
	}

	private void initComponent() {
		String[] columnNames = {"AstahClass","YourClass"};
		JScrollPane tableScrollPane = null;

		if (isConfigFileExist()) {
			tableModel = loadTableModel();
		}else{
			tableModel = new DefaultTableModel(null, columnNames);
			tableModel.setColumnCount(2);
		}

		methodTable = new JTable(tableModel);
		tableScrollPane = new JScrollPane(methodTable);
		tableScrollPane.setPreferredSize(new Dimension(430, 280));
		add(tableScrollPane);

		insertData();
	}
	
	private void insertData(){
		String[] rowData = null;
		MyClass myClass = null;
		
		for(int i=0;i<cb.getclasslistsize();i++){
			myClass = cb.getClass(i);
			rowData = new String[2];
			rowData[0] = myClass.getName();
			rowData[1] = "Dammy";
			tableModel.insertRow(i, rowData);
		}
	}

	private DefaultTableModel loadTableModel() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	private boolean isConfigFileExist() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
