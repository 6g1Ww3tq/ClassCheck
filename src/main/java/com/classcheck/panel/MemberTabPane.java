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
import javax.swing.JLabel;
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
	private FieldCompPanel fcp;
	//メソッドの対応付を行う
	MethodCompPanel mcp;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JSplitPane compVerticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode astahRoot;
	List<MyClass> myClassList;
	Map<MyClass, Pocket<SelectedType>> selectedSameSigMap;

	StatusBar astahTreeStatus;
	StatusBar mtpSourceStatus;
	StatusBar fcpSourceStatus;

	CompTablePane tablePane;

	Map<MyClass, Boolean> generatableMap = new HashMap<MyClass, Boolean>();

	MyClass selectedMyClass;


	public MemberTabPane(MethodCompPanel mcp, ClassBuilder cb) {
		selectedMyClass = null;
		this.fcp = new FieldCompPanel(cb);
		this.mcp = mcp;
		this.myClassList = cb.getClasslist();
		this.selectedSameSigMap = new HashMap<MyClass, Pocket<SelectedType>>();
		this.tablePane = new CompTablePane(this,myClassList, mcp);
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		tablePane.setTableEditable(isEditable);
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
		holizontalSplitePane.setSize(new Dimension(400, 400));
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		compVerticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		astahRoot = new DefaultMutableTreeNode("AstahClass");
		jtree = new JTree(astahRoot);
		jtree.setSize(new Dimension(200,200));

		boolean isSameMethodSelected = false;

		//比較パネルの初期化
		for (MyClass myClass : myClassList) {
			child = new ClassNode(myClass);
			astahRoot.add(child);
			//デフォルトでパネルに初期値データを入れる
			isSameMethodSelected = mcp.initComponent(myClass,true);
			selectedSameSigMap.put(myClass, new Pocket<SelectedType>(SelectedType.OTHER));

			generatableMap.put(myClass, !isSameMethodSelected);
		}

		//デフォルトでパネルに初期値データを入れる
		//->表示させないようにする
		mcp.removeAll();
		mcp.revalidate();

		//上の左右パネル
		//astah tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(jtree,BorderLayout.CENTER);
		astahTreeStatus = new StatusBar(panel, "Astah-Class");
		panel.add(astahTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(panel);
		treeScrollPane.setSize(new Dimension(200, 300));	

		//astah and source panel(右）

		//メソッドの対応パネル
		panel = new JPanel(new BorderLayout());
		panel.add(mcp,BorderLayout.CENTER);
		mtpSourceStatus = new StatusBar(panel, "対応付けしてください");
		mcp.setStatus(mtpSourceStatus);
		mtpSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(mtpSourceStatus,BorderLayout.SOUTH);
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setSize(new Dimension(200,300));	
		//compVerticalSplitePane.setBottomComponent(scrollPane);
		compVerticalSplitePane.setTopComponent(scrollPane);
		//フィールドの対応パネル
		panel = new JPanel(new BorderLayout());
		panel.add(mcp,BorderLayout.CENTER);
		fcpSourceStatus = new StatusBar(panel, "対応付けしてください");
		fcp.setStatus(fcpSourceStatus);
		fcpSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(mtpSourceStatus,BorderLayout.SOUTH);
		scrollPane = new JScrollPane(panel);
		scrollPane.setSize(new Dimension(200,300));	
		//compVerticalSplitePane.setTopComponent(scrollPane);
		compVerticalSplitePane.setBottomComponent(scrollPane);
		compVerticalSplitePane.setSize(new Dimension(100, 100));
		compVerticalSplitePane.setContinuousLayout(true);

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(compVerticalSplitePane);
		holizontalSplitePane.setContinuousLayout(true);

		//下のテーブル
		JScrollPane tableScrollPane = new JScrollPane(tablePane);
		tableScrollPane.setSize(new Dimension(200, 200));	
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
						reLoadMethodCompPane(selectedMyClass,false);
					}
				}

			}
		});
	}

	public void reLoadMethodCompPane(MyClass myClass,boolean isAllChange){
		Map<MyClass, List<JPanel>> mapPanelList = mcp.getMapPanelList();
		List<JPanel> panelList = mapPanelList.get(myClass);
		Component component;
		JComboBox codeSigBox = null;
		List<String> codeSigList = new ArrayList<String>();
		Object obj = null;
		Pocket<SelectedType> pocket = null;

		String baseSig,comparedSig;
		boolean isSame;

		//パネルの更新
		mcp.removeAll();
		mcp.revalidate();
		mcp.initComponent(myClass,isAllChange);
		astahTreeStatus.setText("Astah-Class:"+myClass.getName());

		//ステータスバーによるエラーチェック
		for (JPanel panel : panelList) {
			for (int i = 0; i < panel.getComponentCount(); i++) {
				component = panel.getComponent(i);
				codeSigBox = null;

				if (component instanceof JComboBox) {
					codeSigBox = (JComboBox) component;
				}

				if (codeSigBox != null) {
					obj = codeSigBox.getSelectedItem();
					codeSigList.add(obj.toString());
				}

			}
		}

		isSame = false;

		for(int i=0;i<codeSigList.size();i++){

			baseSig = codeSigList.get(i);

			for(int j=0;j<codeSigList.size();j++){

				comparedSig = codeSigList.get(j);
				if (i==j) {
					continue ;
				}

				if (baseSig.equals(comparedSig)) {
					isSame = true;
					break;
				}

			}
			
			if (isSame) {
				break;
			}

		}

		pocket = selectedSameSigMap.get(myClass);

		if (isSame) {
			pocket.set(SelectedType.SAME);
		}else{
			pocket.set(SelectedType.NOTSAME);
		}

		checkSameMethod(myClass);
		
		if (codeSigList.size() == 0) {
			mtpSourceStatus.setColor(Color.red);
			mtpSourceStatus.setText("クラスを選択してください");
			setGeneratable(myClass, false);
		}
	}

	public void checkSameMethod(MyClass myClass){
		Pocket<SelectedType> pocket = selectedSameSigMap.get(myClass);

		if (pocket.get() == SelectedType.SAME) {
			mtpSourceStatus.setColor(Color.red);
			mtpSourceStatus.setText("同じシグネチャーを選択しないでください");
			setGeneratable(myClass ,false);
		}else if(pocket.get() == SelectedType.NOTSAME){
			mtpSourceStatus.setColor(Color.green);
			mtpSourceStatus.setText("OK");
			setGeneratable(myClass ,true);
		}
	}

	public void setGeneratable(MyClass myClass , boolean b) {
		generatableMap.put(myClass, b);
	}

	public boolean isGeneratable(){
		boolean isSameMethod = true;
		List<JPanel> panelList;
		Component comp;
		JComboBox box_1;

		for(MyClass myClass : generatableMap.keySet()){
			panelList = mcp.getMapPanelList().get(myClass);

			for(JPanel panel : panelList){

				for(int i = 0 ; i <panel.getComponentCount();i++){
					comp = panel.getComponent(i);

					if (comp instanceof JComboBox) {
						box_1 = (JComboBox) comp;

						isSameMethod = checkSameItemSelected(i,box_1,panelList);
					}
				}
			}

			if (isSameMethod) {
				break;
			}
		}
		return !isSameMethod;
	}

	private boolean checkSameItemSelected(int index,
			JComboBox box_1,
			List<JPanel> panelList) {
		boolean isSame = false;
		Component comp;
		JComboBox box_2;
		String strBox_1,strBox_2;

		strBox_1 = box_1.getSelectedItem().toString();

		for(JPanel panel : panelList){

			for(int i = 0 ; i <panel.getComponentCount();i++){
				comp = panel.getComponent(i);

				if (i==index) {
					continue ;
				}

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
}