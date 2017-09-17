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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.MyClass;
import com.classcheck.window.DebugMessageWindow;

public class CompTablePane extends JPanel implements Serializable{

	JScrollPane tableScrollPane;
	DefaultTableModel tableModel;
	JTable classCompTable;
	List<MyClass> myClassList;
	AstahAndSourcePanel astahAndSourcePane;
	List<CodeVisitor> codeVisitorList;

	boolean isTableEditable = false;

	public CompTablePane(List<MyClass> myClassList,
			AstahAndSourcePanel astahAndSourcePane) {
		this.myClassList = myClassList;
		this.astahAndSourcePane = astahAndSourcePane;
		this.codeVisitorList = astahAndSourcePane.getCodeVisitorList();
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		isTableEditable = isEditable;
	}

	private void initComponent() {
		String[] columnNames = {"AstahClass","YourClass"};

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
		TableColumn userClassColumn = null;
		MyClass myClass;

		CodeVisitor codeVisitor = null;
		JComboBox<CodeVisitor> comboBox = new JComboBox<CodeVisitor>();

		for(int i=0;i<myClassList.size();i++){
			myClass = myClassList.get(i);
			rowData = new Object[1];
			rowData[0] = myClass.getName();
			tableModel.insertRow(i, rowData);
		}

		for (int i = 0; i < codeVisitorList.size(); i++) {
			codeVisitor = codeVisitorList.get(i);
			comboBox.addItem(codeVisitor);
		}
		
		userClassColumn = classCompTable.getColumnModel().getColumn(1);
		comboBox.setBorder(BorderFactory.createEmptyBorder());
		userClassColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//セルのデフォルト値を設定
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			comboBox = (JComboBox<CodeVisitor>)tableModel.getValueAt(i, 1);
			//comboBox.setSelectedItem(anObject);
		}
	}

	private void initActionEvent() {
		tableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent tme) {
				int eventType = tme.getType();

				if (eventType == TableModelEvent.UPDATE) {
					DebugMessageWindow.clearText();
					System.out.println("Cell " + tme.getFirstRow() + ", "
							+ tme.getColumn() + " changed. The new value: "
							+ tableModel.getValueAt(tme.getFirstRow(),
									tme.getColumn()));	
					DebugMessageWindow.msgToOutPutTextArea();
				}
			}
		});
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
