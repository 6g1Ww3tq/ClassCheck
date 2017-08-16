package com.classcheck.autosouce;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.classcheck.view.ResultTabView;

/**
 * @author  
 */
public class ConfigView extends JFrame implements ActionListener {

	/**
	 * @uml.property  name="config"
	 * @uml.associationEnd  
	 */
	Config config;
	JCheckBox debagPrint;
	JCheckBox generateInstance;
	JCheckBox oclTranslate;
	JComboBox combo;
	DefaultTableModel tableModel;
	String condition;
	JComboBox cond;
	JButton language;
	JLabel collectionLabel;
	JLabel conditionLabel;
	JLabel oclLabel;

	JTabbedPane tabbedpane;

	JFrame window;//astahのフレーム

	public ConfigView(Config c,JFrame w){
		config = c;
		this.setSize(440, 330);
		this.setTitle("Config");
		window = w;

		JPanel nomalTab = createNomalTab();
		JPanel oclTab = createOclTab();
		tabbedpane = new JTabbedPane();
		tabbedpane.addTab("一般",nomalTab);
		tabbedpane.addTab("OCL",oclTab);
	    getContentPane().add(tabbedpane, BorderLayout.CENTER);

	    JButton ok = new JButton("OK");
	    ok.addActionListener(this);
	    ok.setActionCommand("ok");

	    JButton cancel = new JButton("CANCEL");
	    cancel.addActionListener(this);
	    cancel.setActionCommand("cancel");

	    language = new JButton("ENGLISH");
	    language.addActionListener(this);
	    language.setActionCommand("language");

	    Container con = new Container();
	    con.setLayout(new FlowLayout());
	    con.add(ok);
	    con.add(cancel);
	    Container cc = new Container();
	    cc.setLayout(new FlowLayout(FlowLayout.RIGHT));
	    cc.add(language);
	    add(cc,BorderLayout.NORTH);
	    add(con,BorderLayout.SOUTH);


	}

	private JPanel createNomalTab() {
		JPanel nomalTab = new JPanel(new BorderLayout());
		nomalTab.setLayout(new BoxLayout(nomalTab,BoxLayout.Y_AXIS));
		debagPrint = new JCheckBox("デバッグプリントの追加",config.isAddDebagPrint());
		generateInstance = new JCheckBox("インスタンス生成文の自動追加",config.isAutoInstanceGenerate());
		collectionLabel = new JLabel("クラス図上の多重度2以上の要素の変換");

		//コレクション設定用
		String[] combodata = {"none","Array","List","Set","Map"};
	    DefaultComboBoxModel model = new DefaultComboBoxModel(combodata);
	    combo = new JComboBox(model);
	    //combo.setMaximumSize(new Dimension(100,30));
	    combo.setPreferredSize(new Dimension(170, 30));
	    int collectionSelect=0;
		if(config.getClassCollction().equals("none")){
			collectionSelect=0;
		}else if(config.getClassCollction().equals("Array")){
			collectionSelect=1;
		}else if(config.getClassCollction().equals("List")){
			collectionSelect=2;
		}else if(config.getClassCollction().equals("Set")){
			collectionSelect=3;
		}else if(config.getClassCollction().equals("Map")){
			collectionSelect=4;
		}
	    combo.setSelectedIndex(collectionSelect);
	    Container c = new Container();
	    c.setLayout(new FlowLayout(FlowLayout.CENTER));
	    c.add(combo);

	    conditionLabel = new JLabel("操作の事前条件・事後条件");
	  //事後条件等、設定用
	    String[] conddata = {"none","Document","Assertion(Commnet)","Document and Assertion"};
	    DefaultComboBoxModel model2 = new DefaultComboBoxModel(conddata);
	    cond = new JComboBox(model2);
	   // cond.setMaximumSize(new Dimension(100,30));
	    cond.setPreferredSize(new Dimension(170, 30));

	    int condFirst=0;
	    if(!config.isAddAssert() && !config.isDefinitionCondition()){
	    	condFirst = 0;
	    }else if(!config.isAddAssert() && config.isDefinitionCondition()){
	    	condFirst=1;
	    }else if(config.isAddAssert() && !config.isDefinitionCondition()){
	    	condFirst = 2;
	    }else if(config.isAddAssert() && config.isDefinitionCondition()){
	    	condFirst = 3;
	    }
	    cond.setSelectedIndex(condFirst);
	    Container c2 = new Container();
	    c2.setLayout(new FlowLayout(FlowLayout.CENTER));
	    c2.add(cond);


	    nomalTab.add(debagPrint);
	    nomalTab.add(Box.createRigidArea(new Dimension(10,10)));
	    nomalTab.add(generateInstance);
	    nomalTab.add(Box.createRigidArea(new Dimension(10,10)));
	    nomalTab.add(collectionLabel);
	    nomalTab.add(c);
	    nomalTab.add(conditionLabel);
	    nomalTab.add(c2);
		return nomalTab;

	}



	private JPanel createOclTab() {
		JPanel oclTab = new JPanel(new BorderLayout());
		oclTab.setLayout(new BoxLayout(oclTab,BoxLayout.Y_AXIS));
		oclTranslate = new JCheckBox("OCL型をjavaに変換",config.isOcLTranslate());
		oclLabel = new JLabel("OCL型の変換後の型を設定");

		String tableName[] = {"OCL base type","translate type","OCL collection type","translate type"};
		tableModel = new DefaultTableModel(tableName,0);
		JTable table = new JTable(tableModel);
		//table.setFillsViewportHeight(true);

		Object o1[] = {"Boolean",config.oclBoolean,"Collection",config.oclCollection};
		tableModel.addRow(o1);
		Object o2[] = {"Integer",config.oclInteger,"Bag",config.oclBag};
		tableModel.addRow(o2);
		Object o3[] = {"Real",config.oclReal,"Set",config.oclSet};
		tableModel.addRow(o3);
		Object o4[] = {"String",config.oclString,"OrderedSet",config.oclOrderedSet};
		tableModel.addRow(o4);
		Object o5[] = {"","","Sequence",config.oclSequence};
		tableModel.addRow(o5);

		JComboBox cb = new JComboBox(createClassList());
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cb));
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cb));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setAlignmentX(CENTER_ALIGNMENT);

		oclTab.add(oclTranslate);
		oclTab.add(Box.createRigidArea(new Dimension(10,10)));
		oclTab.add(oclLabel);

		oclTab.add(scrollPane);

		return oclTab;

	}



	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();

		if(cmd.equals("ok")){
			boolean addDebugPrint=debagPrint.isSelected();
			boolean autoInstanceGenerate=generateInstance.isSelected();
			boolean ocl=oclTranslate.isSelected();
			String classCollection=(String)combo.getSelectedItem();
			boolean addAssert = isAssert();
			boolean addCondition = isDocument();
			config = new Config(addDebugPrint,autoInstanceGenerate,addAssert,addCondition,ocl,classCollection);
			config.setOclTranslate((String)tableModel.getValueAt(0, 1).toString(), //boolean OCLの変換
					(String)tableModel.getValueAt(1, 1), //integer
					(String)tableModel.getValueAt(2, 1), //real
					(String)tableModel.getValueAt(3, 1), //String
					(String)tableModel.getValueAt(0, 3), //Collection
					(String)tableModel.getValueAt(1, 3), //bag
					(String)tableModel.getValueAt(2, 3), //set
					(String)tableModel.getValueAt(3, 3), //orderedset
					(String)tableModel.getValueAt(4, 3));//sequeance
			ResultTabView.setConfig(config);
			//System.out.println(config.toString());
			this.setVisible(false);

		}else if(cmd.equals("cancel")){
			this.setVisible(false);
		}else if(cmd.equals("language")){
			String name = language.getText();
			if(name.equals("ENGLISH")){
				debagPrint.setText("Add debug print");
				generateInstance.setText("Add instance generation sentence automatically");
				oclTranslate.setText("The change ocl type to Java");
				collectionLabel.setText("Element beyond multiplicity-2 on the class diagram");
				conditionLabel.setText("Operation's precondition and postcondition");
				oclLabel.setText("Establish the change-type in ocl-type");
				language.setText("日本語");


			}else{
				debagPrint.setText("デバッグプリントの追加");
				generateInstance.setText("インスタンス生成文の自動追加");
				oclTranslate.setText("OCL型をjavaに変換");
				collectionLabel.setText("クラス図上の多重度2以上の要素の変換");
				conditionLabel.setText("操作の事前条件・事後条件");
				oclLabel.setText("OCL型の変換後の型を設定");
				language.setText("ENGLISH");

			}
		}
	}

	private boolean isAssert(){
		if(((String)cond.getSelectedItem()).equals("アサーション(コメント)") || ((String)cond.getSelectedItem()).equals("両方")){
			return true;
		}else if(((String)cond.getSelectedItem()).equals("Assertion(Commnet)") || ((String)cond.getSelectedItem()).equals("Document and Assertion")){
			return true;
		}
		return false;
	}

	private boolean isDocument(){
		if(((String)cond.getSelectedItem()).equals("ドキュメント") || ((String)cond.getSelectedItem()).equals("両方")){
			return true;
		}else if(((String)cond.getSelectedItem()).equals("Document") || ((String)cond.getSelectedItem()).equals("Document and Assertion")){
			return true;
		}
		return false;
	}

	private String[] createClassList(){
		AstahAPI api;
		try {
			api = AstahAPI.getAstahAPI();
			 ProjectAccessor projectAccessor = api.getProjectAccessor();
			 IModel imodel = projectAccessor.getProject();
			 List<IClass> classList=new ArrayList<IClass>();
		     getAllClasses(imodel,classList);

		     //classListの要素の名前をString型の配列に入れる
		     String s[] = new String[classList.size()];
		     for(int i=0;i<classList.size();i++){
		    	 s[i]=classList.get(i).getName();
		     }
		     return s;

		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
			String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	private void getAllClasses(INamedElement element, List<IClass> classList)
		      throws ClassNotFoundException, ProjectNotFoundException {
		    if (element instanceof IPackage) {
		    	if(!element.getName().equals("ocl")){
		    		for(INamedElement ownedNamedElement : ((IPackage)element).getOwnedElements()) {
		    			getAllClasses(ownedNamedElement, classList);
		    		}
		    	}

		    } else if (element instanceof IClass) {
		    	if(!(element instanceof IUseCase) && !isActor((IClass) element))
		    	classList.add((IClass)element);
		    	/*for(IClass nestedClasses : ((IClass)element).getNestedClasses()) {
		    		getAllClasses(nestedClasses, classList);
		    	}*///インナークラスはあると邪魔！
		    }
	 }

	private boolean isActor(IClass c){
		 String s[] = c.getStereotypes();
		 for(int i=0;i<s.length;i++){
			 if(s[i].equals("actor")){
				 return true;
			 }
		 }
		 return false;
	 }

}
