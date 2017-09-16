package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;

public class CompTablePane extends JPanel implements Serializable{

	JScrollPane tableScrollPane;
	DefaultTableModel tableModel;
	JTable classCompTable;
	private ClassBuilder cb;
	private List<CodeVisitor> codeVisitorList;

	boolean isTableEditable = false;

	int rowLength;

	public CompTablePane(ClassBuilder cb, List<CodeVisitor> codeVisitorList) {
		this.cb = cb;
		this.codeVisitorList = codeVisitorList;
		setLayout(new BorderLayout());
		initComponent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		isTableEditable = isEditable;
	}

	private void initComponent() {
		String[] columnNames = {"AstahClass","YourClass"};
		rowLength = 0;

		if (isConfigFileExist()) {
			tableModel = loadTableModel();
		}else{
			//編集不可にする
			tableModel = new DefaultTableModel(null, columnNames){
				@Override
				public boolean isCellEditable(int row, int column) {

					if(column==0){
						return isTableEditable;
					}else{
						return true;
					}
				}
			};
			tableModel.setColumnCount(2);
		}

		classCompTable = new JTable(tableModel);
		tableScrollPane = new JScrollPane(classCompTable);
		tableScrollPane.setPreferredSize(null);
		add(tableScrollPane);

		insertData();
	}

	public void setEditable(boolean isEditable){
		classCompTable.setEnabled(isEditable);
		tableScrollPane.setEnabled(isEditable);
	}

	private void insertData(){
		Object[] rowData = null;
		MyClass myClass = null;
		TableColumn userClassColumn = null;

		CodeVisitor codeVisitor = null;
		JComboBox<String> comboBox = new JComboBox<String>();

		for(int i=0;i<cb.getclasslistsize();i++){
			myClass = cb.getClass(i);
			rowData = new Object[1];
			rowData[0] = myClass.getName();
			tableModel.insertRow(i, rowData);
			rowLength++;
		}

		rowLength--;

		if (rowLength == -1) {
			rowLength++;
		}

		for (int i = 0; i < codeVisitorList.size(); i++) {
			codeVisitor = codeVisitorList.get(i);
			comboBox.addItem(codeVisitor.getClassName());
		}
		userClassColumn = classCompTable.getColumnModel().getColumn(1);
		comboBox.setBorder(BorderFactory.createEmptyBorder());
		userClassColumn.setCellEditor(new DefaultCellEditor(comboBox));
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
