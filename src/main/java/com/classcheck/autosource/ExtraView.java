package com.classcheck.autosource;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;

/**
 * @author  
 */
public class ExtraView extends JPanel
implements IPluginExtraTabView, ProjectEventListener,ActionListener{


	List<IClass> classList;
	List<ISequenceDiagram> diagramList;
	Boolean classBox[];
	Boolean diagramBox[];
	JButton output;
	JButton configButton;
	JCheckBox debugPrint;
	DefaultTableModel tableModel;
	JScrollPane scrollPane;
	/**
	 * @uml.property  name="config"
	 * @uml.associationEnd  
	 */
	static Config config;
	JFrame window;

	public ExtraView() {

		AstahAPI api;
		try {
			api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			window = api.getViewManager().getMainFrame();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidUsingException e) {
			e.printStackTrace();
		}

		classList = new ArrayList<IClass>();
		diagramList = new ArrayList<ISequenceDiagram>();
		config = new Config();
		initComponents();

	}

	private void initComponents() {
		setLayout(new BorderLayout());
		createList();
		add(scrollPane, BorderLayout.CENTER);

		createButton();
		Container c = new Container();
		c.setLayout(new FlowLayout());
		c.add(output);
		c.add(configButton);
		//debugPrint = new JCheckBox("Add Debug Print");
		//c.add(debugPrint);

		add(c,BorderLayout.SOUTH);
		addProjectEventListener();
	}

	private void createButton(){
		output = new JButton("OUTPUT");
		output.addActionListener(this);
		output.setActionCommand("output");

		configButton = new JButton("CONFIG");
		configButton.addActionListener(this);
		configButton.setActionCommand("config");

	}

	private void createList(){

		classList = SourceGenerator.getClassList();
		diagramList = SourceGenerator.getSequenceDiagramList();

		if(classList.size()>0){
			classBox = new Boolean[classList.size()];
			for(int i=0;i<classList.size();i++){
				if(classList.get(i).getName().equals("")){
					classBox[i]=false;
				}else{
					classBox[i]=true;
				}
			}
		}
		if(diagramList.size()>0){
			diagramBox = new Boolean[diagramList.size()];
			for(int i=0;i<diagramList.size();i++){
				diagramBox[i]=true;
			}
		}


		String tableName[] = {"Class","Name","SequenceDiagram","Name"};


		tableModel = new MyTableModel(tableName,0);

		JTable table = new JTable(tableModel);

		for(int i=0; classList.size()>i  || diagramList.size()>i; i++){

			if(classList.size()>i && diagramList.size()>i){
				Object c[]={classBox[i],classList.get(i),diagramBox[i],diagramList.get(i)};
				tableModel.addRow(c);
			}else if(classList.size()>i){
				Object c[]={classBox[i],classList.get(i),null,null};
				tableModel.addRow(c);
			}else if(diagramList.size()>i){
				Object c[]={null,null,diagramBox[i],diagramList.get(i)};
				tableModel.addRow(c);
			}

		}



		//tableModel.addRow(classBox);
		//tableModel.addRow(diagramBox);


		// tableModel.addColumn(tableName[0], classBox);
		//tableModel.addColumn(tableName[1], diagramBox);
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);


	}

	/**
	 * @param con
	 * @uml.property  name="config"
	 */
	public static void setConfig(Config con){
		config = con;
	}

	class MyTableModel extends DefaultTableModel{
		MyTableModel(String[] columnNames, int rowNum){
			super(columnNames, rowNum);
		}

		public Class getColumnClass(int col){
			if(getValueAt(0,col)==null){
				return new Object().getClass();
			}
			return getValueAt(0, col).getClass();
		}
	}



	private void addProjectEventListener() {
		try {
			AstahAPI api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			projectAccessor.addProjectEventListener(this);
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
	}

	@Override
	public void projectChanged(ProjectEvent e) {
		remove(scrollPane);
		createList();
		add(scrollPane);
	}

	@Override
	public void projectClosed(ProjectEvent e) {
	}

	@Override
	public void projectOpened(ProjectEvent e) {
		remove(scrollPane);
		createList();
		add(scrollPane);
	}

	@Override
	public void addSelectionListener(ISelectionListener listener) {
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return "Show Hello World here";
	}

	@Override
	public String getTitle() {
		return "Source Code Generate";
	}

	public void activated() {
	}

	public void deactivated() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if(cmd.equals("output")){
			config.activate();
			List<IClass> outputClass = new ArrayList<IClass>();
			List<ISequenceDiagram> outputDiagram = new ArrayList<ISequenceDiagram>();
			for(int i=0;i<classList.size();i++){
				if((Boolean) tableModel.getValueAt(i, 0) && i<classList.size()){
					outputClass.add(classList.get(i));
				}
			}
			for(int i=0;i<diagramList.size();i++){
				if((Boolean) tableModel.getValueAt(i, 2) && i<diagramList.size()){
					outputDiagram.add(diagramList.get(i));
				}
			}

			try {
				//if(debugPrint.isSelected()){
				//Method.debugPrintOn();
				//}
				new SourceGenerator().run(outputClass,outputDiagram);
			} catch (UnExpectedException e1) {
				JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		if(cmd.equals("config")){
			try{
				ConfigView cv = new ConfigView(config,window);
				cv.setVisible(true);
			}catch (Exception e1) {
				JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}

	}
}


