package com.classcheck.window;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

public class ConfigJDialog extends JDialog implements Serializable{
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"RtnType","MethodName","Param"};
	private JButton rtnTypeBtn;
	private JButton mnBtn;
	private JButton paramBtn;
	private JButton okBtn;
	private JButton cancelBtn;
	private JButton rowBtn;
	private JButton deleteRowBtn;
	private DefaultTableModel tableModel;
	private	JTable methodTable;

	public ConfigJDialog(boolean modal) {
		this.setModal(modal);
		this.setSize(450, 300);
		this.setResizable(false);
		this.setTitle("Config");

		setLayout(new BorderLayout(5,5));

		initComponent();
		btnEventInit();

		setVisible(true);

	}

	private void initComponent() {
		JPanel panelNorth = new JPanel(new BorderLayout(5,5));
		JPanel panelNorth_Column = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
		JPanel panelNorth_Row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
		panelNorth.add(BorderLayout.NORTH,panelNorth_Column);
		panelNorth.add(BorderLayout.SOUTH,panelNorth_Row);
		add(panelNorth,BorderLayout.NORTH);

		JLabel lblReturntype = new JLabel("ReturnType:");
		panelNorth_Column.add(lblReturntype);

		rtnTypeBtn = new JButton("Add");
		panelNorth_Column.add(rtnTypeBtn);

		JLabel lblMethodname = new JLabel("MethodName:");
		panelNorth_Column.add(lblMethodname);

		mnBtn = new JButton("Add");
		panelNorth_Column.add(mnBtn);

		JLabel lblParam = new JLabel("param:");
		panelNorth_Column.add(lblParam);

		paramBtn = new JButton("Add");
		panelNorth_Column.add(paramBtn);


		JLabel lblRowAdd = new JLabel("RowAdd:");
		panelNorth_Row.add(lblRowAdd);
		rowBtn = new JButton("Add");
		panelNorth_Row.add(rowBtn);

		JLabel lblDeleteRow = new JLabel("DeleteRow:");
		panelNorth_Row.add(lblDeleteRow);
		deleteRowBtn = new JButton("delete");
		panelNorth_Row.add(deleteRowBtn);

		JPanel panelCenter = new JPanel();
		if (isConfigFileExist()) {
			tableModel = loadTableModel();
		}else{
			tableModel = new DefaultTableModel(null, columnNames);
			tableModel.setColumnCount(3);
			tableModel.setRowCount(20);
		}
		methodTable = new JTable(tableModel);
		JScrollPane jScrollPane = new JScrollPane(methodTable);
		jScrollPane.setPreferredSize(new Dimension(430, 280));
		panelCenter.add(jScrollPane);
		add(panelCenter,BorderLayout.CENTER);

		JPanel panelSouth = new JPanel();
		add(panelSouth,BorderLayout.SOUTH);
		panelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));

		okBtn = new JButton("OK");

		okBtn.setLayout(new BorderLayout(12, 12));
		panelSouth.add(okBtn);

		cancelBtn = new JButton("Cancel");
		cancelBtn.setLayout(new BorderLayout(12, 12));
		panelSouth.add(cancelBtn);

	}

	private void btnEventInit() {

		rtnTypeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		mnBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		paramBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});


		rowBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int addRowNum = methodTable.getSelectedRow();
				if (addRowNum != -1) {
					tableModel.insertRow(addRowNum,new Vector());
				}
			}
		});

		deleteRowBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] deleteRowNums = methodTable.getSelectedRows();

				for (int rowNum : deleteRowNums) {
					tableModel.removeRow(rowNum);
				}
			}
		});


		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj instanceof Component){
					Component component = (Component) obj;
					Window window = SwingUtilities.getWindowAncestor(component);
					window.dispose();
				}
				saveConfig();
			}
		});

		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = e.getSource();

				if (obj instanceof Component){
					Component component = (Component) obj;
					Window window = SwingUtilities.getWindowAncestor(component);
					window.dispose();
				}
			}
		});
	}

	private DefaultTableModel loadTableModel() {
		DefaultTableModel configJDialog = null;

		try {
			ObjectInputStream objInStream = new ObjectInputStream(
					new FileInputStream("/home/masa/eclipse/eclipse-jee-indigo-SR2-linux-gtk-x86_64/workspace/Java/astah/config/config.bin"));

			Object obj = objInStream.readObject();

			if (obj instanceof DefaultTableModel) {
				configJDialog = (DefaultTableModel) obj;
			}

			objInStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return configJDialog;
	}

	private boolean saveConfig() {
		boolean saveSuccess = false;

		if (!isConfigFileExist()) {
			createConfigFile();
		}

		try {
			ObjectOutputStream objOutStream = new ObjectOutputStream(
					new FileOutputStream("/home/masa/eclipse/eclipse-jee-indigo-SR2-linux-gtk-x86_64/workspace/Java/astah/config/config.bin"));

			objOutStream.writeObject(tableModel);
			objOutStream.close();

			saveSuccess = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return saveSuccess;
	}

	private boolean isConfigFileExist(){
		File file = new File("/home/masa/eclipse/eclipse-jee-indigo-SR2-linux-gtk-x86_64/workspace/Java/astah/config/config.bin");

		return file.exists();
	}

	private boolean createConfigFile(){
		File file = new File("/home/masa/eclipse/eclipse-jee-indigo-SR2-linux-gtk-x86_64/workspace/Java/astah/config/config.bin");
		boolean isCreate = false;

		try {
			isCreate = file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isCreate;
	}
}
