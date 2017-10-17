package com.classcheck.panel;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.ArrayList;
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
	//	static DefaultTableModel tableModel;
	DefaultTableModel tableModel;
	JTable classCompTable;
	MemberTabPane mtp;
	FieldCompPanel fcp;
	MethodCompPanel mcp;
	List<MyClass> myClassList;
	List<CodeVisitor> codeVisitorList;

	boolean isTableEditable = false;

	public CompTablePane(MemberTabPane mtp, List<MyClass> myClassList) {
		this.mtp = mtp;
		this.myClassList = myClassList;
		this.fcp = mtp.getFcp();
		this.mcp = mtp.getMcp();
		this.codeVisitorList = mcp.getCodeVisitorList();
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		isTableEditable = isEditable;
	}

	public DefaultTableModel getTableModel(){
		return tableModel;
	}

	public boolean isNullItemSelected(){
		Object obj;
		boolean isNull = false;

		for (int row = 0 ; row < tableModel.getRowCount() ; row++){
			obj = tableModel.getValueAt(row, 1);

			/*
			 * Item が - Choose Class -のとき
			 */
			if (obj.toString().contains("- Choose Class -")) {
				isNull = true;
				break;
			}
		}

		return isNull;
	}

	/*
	 * 初期値のテーブルの２列目のセルはJComboBox
	 * 選択をするとCodeVisitorになる
	 */
	public boolean isSameTableItemSelected(){
		boolean isSameTableItemSelected = false;
		List<ClonableJComboBox> boxList = new ArrayList<ClonableJComboBox>();
		List<CodeVisitor> visitorList = new ArrayList<CodeVisitor>();
		Object obj;
		ClonableJComboBox box_1,box_2;
		CodeVisitor visitor_1,visitor_2;

		for (int row = 0 ; row < tableModel.getRowCount() ; row++){
			obj = tableModel.getValueAt(row, 1);

			/*
			 *なぜかテーブルのアイテムがCodeVisitorクラスと
			 *ClonableJComboBoxの時がある 
			 */
			if (obj instanceof ClonableJComboBox) {
				//boxList.add((ClonableJComboBox) obj);
				box_1 = (ClonableJComboBox) obj;

				if (box_1.getSelectedItem() instanceof CodeVisitor) {
					visitorList.add((CodeVisitor) box_1.getSelectedItem());
				}
			}else if (obj instanceof CodeVisitor) {
				visitorList.add((CodeVisitor) obj);
			}
		}

		if (!isSameTableItemSelected) {
			for (int i=0;i<visitorList.size();i++){
				visitor_1 = visitorList.get(i);

				for (int j=0;j<visitorList.size();j++){
					visitor_2 = visitorList.get(j);

					if (i==j) {
						continue ;
					}

					if (visitor_1.equals(visitor_2)) {
						isSameTableItemSelected = true;
						break;
					}
				}

				if (isSameTableItemSelected) {
					break;
				}
			}
		}

		return isSameTableItemSelected;
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
		ClonableJComboBox<Object> tmp = null;

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
				//デフォルト値の設定
				tmp = (ClonableJComboBox) clComboBox.clone();
				tmp.setSelectedItem(defaultVal);
				tableModel.setValueAt(tmp, i, 1);
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
				codeVisitor = mcp.getCodeMap().get(myClassList.get(i));
				if (codeVisitor == null) {
					comboBox.setSelectedItem(defaultVal);
				}else{
					comboBox.setSelectedItem(mcp.getCodeMap().get(myClassList.get(i)));
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
					codeMap = mcp.getCodeMap();

					if (obj instanceof CodeVisitor) {
						visitor = (CodeVisitor) obj;
					}

					if (obj_2 instanceof MyClassCell) {
						myClassCell = (MyClassCell) obj_2;
					}

					if (visitor != null && myClassCell != null && codeMap != null) {
						codeMap.remove(myClassCell.getMyClass());
						codeMap.put(myClassCell.getMyClass(),visitor);
						//メソッドパネルの更新
						mtp.reLoadMemberPane(myClassCell.getMyClass(),true);
						//同じメソッドが選択されていないかチェック
						mtp.checkSameMethod(myClassCell.getMyClass());
					}

					/*
					System.out.println("Cell " + tme.getFirstRow() + ", "
							+ tme.getColumn() + " changed. The new value: "
							+ tableModel.getValueAt(tme.getFirstRow(),
									tme.getColumn()));	
					 */

					System.out.println("visitor : \n"+visitor);
					System.out.println("myClassCell : \n"+myClassCell);
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

	public class ClonableJComboBox<E> extends JComboBox<E> implements Cloneable{

		private static final long serialVersionUID = 1L;

		public ClonableJComboBox() {
			super();
		}

		@Override
		public String toString(){
			return getSelectedItem().toString();
		}

		/*
		 * アイテムに任意の文字列が表示されるように設定
		 */
		@Override
		public Object clone() throws CloneNotSupportedException {
			ClonableJComboBox<E> jcomboBox = new ClonableJComboBox<E>(){
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
