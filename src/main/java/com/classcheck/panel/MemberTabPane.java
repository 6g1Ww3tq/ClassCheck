package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.ClassNode;
import com.classcheck.autosource.MyClass;
import com.classcheck.basic.Pocket;

public class MemberTabPane extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//フィールドの対応付を行う
	FieldCompPanel fcp;
	//メソッドの対応付を行う
	MethodCompPanel mcp;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JSplitPane compVerticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode astahRoot;
	List<MyClass> myClassList;
	Map<MyClass, Pocket<SelectedType>> selectedSameFieldSigMap;
	Map<MyClass, Pocket<SelectedType>> selectedSameMethodSigMap;

	StatusBar astahTreeStatus;
	StatusBar mcpSourceStatus;
	StatusBar fcpSourceStatus;

	CompTablePane tablePane;

	Map<MyClass, Boolean> generatableMap = new HashMap<MyClass, Boolean>();

	MyClass selectedMyClass;



	public MemberTabPane(FieldCompPanel fcp,MethodCompPanel mcp, ClassBuilder cb) {
		selectedMyClass = null;
		this.fcp = fcp;
		this.mcp = mcp;
		this.myClassList = cb.getClasslist();
		this.selectedSameFieldSigMap = new HashMap<MyClass, Pocket<SelectedType>>();
		this.selectedSameMethodSigMap = new HashMap<MyClass, Pocket<SelectedType>>();
		this.tablePane = new CompTablePane(this,myClassList);
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		tablePane.setTableEditable(isEditable);
	}

	public FieldCompPanel getFcp() {
		return fcp;
	}

	public MethodCompPanel getMcp() {
		return mcp;
	}

	public CompTablePane getTablePane() {
		return tablePane;
	}

	public MyClass getSelectedMyClass() {
		return selectedMyClass;
	}

	private void initComponent(){
		JPanel panel;
		ClassNode child = null;
		holizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		compVerticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		astahRoot = new DefaultMutableTreeNode("SkeltonCodeClass");
		jtree = new JTree(astahRoot);
		jtree.setMinimumSize(new Dimension(150,200));

		//フィールド
		boolean isSameFieldSelected = false;
		//メソッド
		boolean isSameMethodSelected = false;

		//比較パネルの初期化
		for (MyClass myClass : myClassList) {
			child = new ClassNode(myClass);
			astahRoot.add(child);
			//デフォルトでパネルに初期値データを入れる
			//フィールド
			isSameFieldSelected = fcp.initComponent(myClass, true);
			//TODO
			//フィールド用を作る＃1
			selectedSameFieldSigMap.put(myClass, new Pocket<SelectedType>(SelectedType.OTHER));

			//メソッド
			isSameMethodSelected = mcp.initComponent(myClass,true);
			//TODO
			//メソッド用を作る＃1
			selectedSameMethodSigMap.put(myClass, new Pocket<SelectedType>(SelectedType.OTHER));

			generatableMap.put(myClass, !isSameMethodSelected);
		}

		//デフォルトでパネルに初期値データを入れる
		//->表示させないようにする
		//フィールド
		fcp.removeAll();
		fcp.revalidate();
		//メソッド
		mcp.removeAll();
		mcp.revalidate();

		//上の左右パネル
		//astah tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(jtree,BorderLayout.CENTER);
		astahTreeStatus = new StatusBar(panel, "SkeltonCode-Class");
		panel.add(astahTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(panel);
		treeScrollPane.setMinimumSize(new Dimension(150, 200));	

		//astah and source panel(右）

		//メソッドの対応パネル
		panel = new JPanel(new BorderLayout());
		panel.add(mcp,BorderLayout.CENTER);
		mcpSourceStatus = new StatusBar(panel, "対応付けしてください");
		mcp.setStatus(mcpSourceStatus);
		mcpSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(mcpSourceStatus,BorderLayout.SOUTH);
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(400, 150));
		scrollPane.setSize(new Dimension(400, 200));
		compVerticalSplitePane.setBottomComponent(scrollPane);

		//フィールドの対応パネル
		panel = new JPanel(new BorderLayout());
		panel.add(fcp,BorderLayout.CENTER);
		fcpSourceStatus = new StatusBar(panel, "対応付けしてください");
		fcp.setStatus(fcpSourceStatus);
		fcpSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(fcpSourceStatus,BorderLayout.SOUTH);
		scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(400, 150));
		scrollPane.setSize(new Dimension(400, 200));
		compVerticalSplitePane.setTopComponent(scrollPane);
		compVerticalSplitePane.setContinuousLayout(true);

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(compVerticalSplitePane);
		holizontalSplitePane.setContinuousLayout(true);

		//下のテーブル
		JScrollPane tableScrollPane = new JScrollPane(tablePane);
		tableScrollPane.setMinimumSize(new Dimension(400, 100));	
		tableScrollPane.setSize(new Dimension(400, 150));	
		tableScrollPane.add(new StatusBar(tableScrollPane, "クラスの対応関係の設定"));
		verticalSplitePane.setTopComponent(holizontalSplitePane);
		verticalSplitePane.setBottomComponent(tableScrollPane);
		verticalSplitePane.setContinuousLayout(true);
		add(verticalSplitePane,BorderLayout.CENTER);

	}

	private void initActionEvent() {
		jtree.addTreeSelectionListener(new TreeSelectionListener(){

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Object obj = jtree.getLastSelectedPathComponent();

				if (obj instanceof ClassNode) {
					ClassNode selectedNode = (ClassNode) obj;
					Object userObj = selectedNode.getUserObject();

					if (userObj instanceof MyClass) {
						selectedMyClass = (MyClass) userObj;
						//パネルの更新
						reLoadMemberPane(selectedMyClass,false);
					}
				}

			}
		});
	}

	public void reLoadMemberPane(MyClass myClass,boolean isAllChange){
		Map<MyClass, List<JPanel>> fieldMapPanelList = fcp.getMapPanelList();
		Map<MyClass, List<JPanel>> methodMapPanelList = mcp.getMapPanelList();
		List<JPanel> methodPanelList = methodMapPanelList.get(myClass);
		List<JPanel> fieldPanelList = fieldMapPanelList.get(myClass);
		Component component;
		JComboBox methodCodeSigBox = null;
		JComboBox fieldCodeSigBox = null;
		List<String> fieldCodeSigList = new ArrayList<String>();
		List<String> methodCodeSigList = new ArrayList<String>();
		Object obj = null;
		Pocket<SelectedType> fieldPocket = null;
		Pocket<SelectedType> methodPocket = null;

		String baseSig,comparedSig;
		boolean isMethodSame;
		boolean isFieldSame;

		//パネルの更新
		//フィールド
		fcp.removeAll();
		fcp.revalidate();
		fcp.initComponent(myClass, isAllChange);
		//メソッド
		mcp.removeAll();
		mcp.revalidate();
		mcp.initComponent(myClass,isAllChange);

		astahTreeStatus.setText("SkeltonCode-Class:"+myClass.getName());

		//ステータスバーによるエラーチェック(メソッド)
		for (JPanel panel : methodPanelList) {
			for (int i = 0; i < panel.getComponentCount(); i++) {
				component = panel.getComponent(i);
				methodCodeSigBox = null;

				if (component instanceof JComboBox) {
					methodCodeSigBox = (JComboBox) component;
				}

				if (methodCodeSigBox != null) {
					obj = methodCodeSigBox.getSelectedItem();
					methodCodeSigList.add(obj.toString());
				}

			}
		}

		//TODO
		//ステータスバーによるエラーチェック(フィールド)
		for (JPanel panel : fieldPanelList) {
			for (int i = 0; i < panel.getComponentCount(); i++) {
				component = panel.getComponent(i);
				fieldCodeSigBox = null;

				if (component instanceof JComboBox) {
					fieldCodeSigBox = (JComboBox) component;
				}

				if (fieldCodeSigBox != null) {
					obj = fieldCodeSigBox.getSelectedItem();
					fieldCodeSigList.add(obj.toString());
				}

			}
		}

		isMethodSame = false;
		isFieldSame = false;

		//同じメソッドが選択されていないかチェック
		for(int i=0;i<methodCodeSigList.size();i++){

			baseSig = methodCodeSigList.get(i);

			for(int j=0;j<methodCodeSigList.size();j++){

				comparedSig = methodCodeSigList.get(j);
				if (i==j) {
					continue ;
				}

				if (baseSig.equals(comparedSig)) {
					isMethodSame = true;
					break;
				}

			}

			if (isMethodSame) {
				break;
			}

		}

		//同じフィールドが選択されていないかチェック
		for(int i=0;i<fieldCodeSigList.size();i++){

			baseSig = fieldCodeSigList.get(i);

			for(int j=0;j<fieldCodeSigList.size();j++){

				comparedSig = fieldCodeSigList.get(j);
				if (i==j) {
					continue ;
				}

				if (baseSig.equals(comparedSig)) {
					isFieldSame = true;
					break;
				}

			}

			if (isFieldSame) {
				break;
			}

		}

		fieldPocket = selectedSameFieldSigMap.get(myClass);
		methodPocket = selectedSameMethodSigMap.get(myClass);

		//フィールド
		if (isFieldSame) {
			fieldPocket.set(SelectedType.SAME);
		}else{
			fieldPocket.set(SelectedType.NOTSAME);
		}

		//メソッド
		if (isMethodSame) {
			methodPocket.set(SelectedType.SAME);
		}else{
			methodPocket.set(SelectedType.NOTSAME);
		}

		checkSameField(myClass);
		checkSameMethod(myClass);

		if (fieldCodeSigList.size() == 0) {
			fcpSourceStatus.setColor(Color.red);
			fcpSourceStatus.setText("フィールドが空です");
			setGeneratable(myClass, false);
		}

		if (methodCodeSigList.size() == 0) {
			mcpSourceStatus.setColor(Color.red);
			mcpSourceStatus.setText("メソッドが空です	");
			setGeneratable(myClass, false);
		}
	}

	private void checkSameField(MyClass myClass) {
		Pocket<SelectedType> pocket = selectedSameFieldSigMap.get(myClass);

		if (pocket.get() == SelectedType.SAME) {
			fcpSourceStatus.setColor(Color.red);
			fcpSourceStatus.setText("同じフィールドを選択しないでください");
			setGeneratable(myClass ,false);
		}else if(pocket.get() == SelectedType.NOTSAME){
			fcpSourceStatus.setColor(Color.green);
			fcpSourceStatus.setText("OK");
			setGeneratable(myClass ,true);
		}
	}

	public void checkSameMethod(MyClass myClass){
		Pocket<SelectedType> pocket = selectedSameMethodSigMap.get(myClass);

		if (pocket.get() == SelectedType.SAME) {
			mcpSourceStatus.setColor(Color.red);
			mcpSourceStatus.setText("同じメソッドを選択しないでください");
			setGeneratable(myClass ,false);
		}else if(pocket.get() == SelectedType.NOTSAME){
			mcpSourceStatus.setColor(Color.green);
			mcpSourceStatus.setText("OK");
			setGeneratable(myClass ,true);
		}
	}

	public void setGeneratable(MyClass myClass , boolean b) {
		generatableMap.put(myClass, b);
	}

	//TODO
	//同じフィールドがないかどうか調べる
	public boolean isFieldGeneratable(){
		boolean generatable = true;
		List<JPanel> fieldPanelList;
		Component comp;
		JComboBox box_1;
		JPanel panel;

		for(MyClass myClass : generatableMap.keySet()){
			fieldPanelList = fcp.getMapPanelList().get(myClass);

			for(int i = 0; i<fieldPanelList.size();i++){
				panel = fieldPanelList.get(i);

				for(int j = 0; j<panel.getComponentCount();j++){
					comp = panel.getComponent(j);

					if(comp instanceof JComboBox){
						box_1 = (JComboBox) comp;

						generatable = !checkSameItemSelected(i, box_1, fieldPanelList);
					}
				}
			}

			if (!generatable) {
				break;
			}

		}


		return generatable;
	}

	public boolean isMethodGeneratable(){
		boolean generatable = true;
		List<JPanel> methodPanelList;
		Component comp;
		JComboBox box_1;
		JPanel panel;

		for(MyClass myClass : generatableMap.keySet()){
			methodPanelList = mcp.getMapPanelList().get(myClass);

			for(int i = 0; i<methodPanelList.size();i++){
				panel = methodPanelList.get(i);

				for(int j = 0; j<panel.getComponentCount();j++){
					comp = panel.getComponent(j);

					if(comp instanceof JComboBox){
						box_1 = (JComboBox) comp;

						generatable = !checkSameItemSelected(i, box_1, methodPanelList);
					}
				}
			}

			if (!generatable) {
				break;
			}

		}

		return generatable;
	}

	private boolean checkSameItemSelected(int index,
			JComboBox box_1,
			List<JPanel> panelList) {
		boolean isSame = false;
		Component comp;
		JComboBox box_2;
		String strBox_1,strBox_2;
		JPanel panel;

		strBox_1 = box_1.getSelectedItem().toString();

		for(int i=0;i<panelList.size() ||i!=index;i++){
			panel = panelList.get(i);

			for(int j = 0 ; j <panel.getComponentCount();j++){
				comp = panel.getComponent(j);

				if (comp instanceof JComboBox) {
					box_2 = (JComboBox) comp;

					strBox_2 = box_2.getSelectedItem().toString();

					if (strBox_1.equals(strBox_2)) {
						isSame = true;
						break;
					}
				}
			}

			if (isSame) {
				break;
			}
		}

		return isSame;
	}

	public boolean isFieldEpmty() {
		boolean generatable = true;
		List<JPanel> fieldPanelList;

		for(MyClass myClass : generatableMap.keySet()){
			fieldPanelList = fcp.getMapPanelList().get(myClass);

			if (fieldPanelList.size() <= 1) {
				generatable = false;
				break;
			}
			
		}

		return generatable;
	}

	public boolean isMethodEmpty() {
		boolean generatable = true;
		List<JPanel> methodPanelList;

		for(MyClass myClass : generatableMap.keySet()){
			methodPanelList = fcp.getMapPanelList().get(myClass);

			if (methodPanelList.size() <= 1) {
				generatable = false;
				break;
			}
			
		}
		return generatable;
	}
}