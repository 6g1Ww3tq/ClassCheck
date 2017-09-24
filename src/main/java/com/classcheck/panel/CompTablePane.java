package com.classcheck.panel;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.MyClassCell;
import com.classcheck.window.DebugMessageWindow;

public class CompTablePane extends JPanel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JScrollPane tableScrollPane;
	static DefaultTableModel tableModel;
	JTable classCompTable;
	SetTabPane setTabPane;
	List<MyClass> myClassList;
	AstahAndSourcePanel astahAndSourcePane;
	List<CodeVisitor> codeVisitorList;

	boolean isTableEditable = false;

	public CompTablePane(List<MyClass> myClassList,
			AstahAndSourcePanel astahAndSourcePane) {
	}

	public CompTablePane(SetTabPane setTabPane, List<MyClass> myClassList,
			AstahAndSourcePanel astahAndSourcePane) {
		this.setTabPane = setTabPane;
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
	
	public static DefaultTableModel getTableModel(){
		return tableModel;
	}

	private void initComponent() {
		String[] columnNames = {"AstahClass","YourClass"};
		tableModel = null;
		
		if (isConfigFileExist()) {
			tableModel = loadTableModel();
		}else{
			//編集不可にする
			tableModel = new DefaultTableModel(null, columnNames){
				private static final long serialVersionUID = 1L;

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
		//tableScrollPane.setPreferredSize(null);
		add(tableScrollPane);

		insertData();
	}

	public void setEditable(boolean isEditable){
		classCompTable.setEnabled(isEditable);
		tableScrollPane.setEnabled(isEditable);
	}

	@SuppressWarnings("unchecked")
	private void insertData(){
		TableColumn userClassColumn = null;
		MyClassCell myClassCell;
		MyClass myClass;
		Object[] rowObjects;
		Object obj;
		String defaultVal = "- Choose Class -";
		CodeVisitor codeVisitor = null;
		JComboBox comboBox;
		ClonableJComboBox<Object> clComboBox = new ClonableJComboBox<Object>();

		for (int i = 0; i < codeVisitorList.size(); i++) {
			codeVisitor = codeVisitorList.get(i);
			clComboBox.addItem(codeVisitor);
		}
		clComboBox.addItem(defaultVal);
		clComboBox.setSelectedItem(defaultVal);

		for(int i=0;i<myClassList.size();i++){
			myClass = myClassList.get(i);
			myClassCell = new MyClassCell(myClass);
			rowObjects = new Object[2];
			tableModel.insertRow(i, rowObjects);
			tableModel.setValueAt(myClassCell, i, 0);
			try {
				tableModel.setValueAt(clComboBox.clone(), i, 1);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		userClassColumn = classCompTable.getColumnModel().getColumn(1);
		clComboBox.setBorder(BorderFactory.createEmptyBorder());
		userClassColumn.setCellEditor(new DefaultCellEditor(clComboBox));

		//セル(JComboBox)のデフォルト値を設定
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			obj = tableModel.getValueAt(i, 1);
			System.out.println("obj-Class:"+obj.getClass());
			if (obj instanceof JComboBox<?>) {
				comboBox = (JComboBox) obj;
				codeVisitor = astahAndSourcePane.getCodeMap().get(myClassList.get(i));
				if (codeVisitor == null) {
					comboBox.setSelectedItem(defaultVal);
				}else{
					comboBox.setSelectedItem(astahAndSourcePane.getCodeMap().get(myClassList.get(i)));
				}
			}else{
				StackTraceElement throwableStackTraceElement = new Throwable().getStackTrace()[0];
		        System.out.println(throwableStackTraceElement.getClassName() + "#" + throwableStackTraceElement.getMethodName() + "(" + throwableStackTraceElement.getLineNumber() + ")");
			}
		}
		DebugMessageWindow.msgToOutPutTextArea();

	}

	private void initActionEvent() {
		tableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent tme) {
				int eventType = tme.getType();
				Object obj,obj_2;
				Map<MyClass, CodeVisitor> codeMap = null;
				MyClassCell myClassCell = null;
				CodeVisitor visitor = null;

				if (eventType == TableModelEvent.UPDATE) {
					DebugMessageWindow.clearText();

					obj = tableModel.getValueAt(tme.getFirstRow(),
							tme.getColumn());
					obj_2 = tableModel.getValueAt(tme.getFirstRow(),
							tme.getColumn() - 1);
					codeMap = astahAndSourcePane.getCodeMap();

					if (obj instanceof CodeVisitor) {
						visitor = (CodeVisitor) obj;
					}

					if (obj_2 instanceof MyClassCell) {
						myClassCell = (MyClassCell) obj_2;
					}

					if (visitor != null && myClassCell != null && codeMap != null) {
						codeMap.remove(myClassCell.getMyClass());
						codeMap.put(myClassCell.getMyClass(),visitor);
					}

					System.out.println("Cell " + tme.getFirstRow() + ", "
							+ tme.getColumn() + " changed. The new value: "
							+ tableModel.getValueAt(tme.getFirstRow(),
									tme.getColumn()));	
					DebugMessageWindow.msgToOutPutTextArea();
					
					//AstahAndSourcePaneの更新
					setTabPane.reLoadAstahAndSourcePane(myClassCell.getMyClass(),true);
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

	public class ClonableJComboBox<E> extends JComboBox<E> implements Cloneable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ClonableJComboBox() {
			super();
		}

		@Override
		public String toString(){
			return getSelectedItem().toString();
		}

		@Override
		public Object clone() throws CloneNotSupportedException {
			JComboBox<E> jcomboBox = new JComboBox<E>(){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String toString() {
					return getSelectedItem().toString();
				}
			};

			for (int i = 0; i < getItemCount(); i++) {
				jcomboBox.addItem(this.getItemAt(i));
			}
			return  jcomboBox;
		}
	}
}