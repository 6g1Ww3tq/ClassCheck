package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IComment;
import com.change_vision.jude.api.inf.model.IConstraint;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.ClassNode;
import com.classcheck.autosource.MyClass;
import com.classcheck.generic.Pocket;

public class MemberTabPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//フィールドの対応付を行う
	FieldComparePanel fcp;
	//メソッドの対応付を行う
	MethodComparePanel mcp;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JSplitPane compVerticalSplitePane;
	JTree jtree;
	JLabel jtrSelClass;
	DefaultMutableTreeNode astahRoot;
	List<MyClass> myClassList;
	Map<MyClass, Pocket<SelectedType>> selectedSameFieldSigMap;
	Map<MyClass, Pocket<SelectedType>> selectedSameMethodSigMap;

	StatusBar astahTreeStatus;
	StatusBar mcpSourceStatus;
	StatusBar fcpSourceStatus;

	ClassTablePanel tablePane;

	//TODO
	//使い方をよく見る
	Map<MyClass, Boolean> generatableMap = new HashMap<MyClass, Boolean>();

	MyClass selectedMyClass;



	public MemberTabPanel(FieldComparePanel fcp,MethodComparePanel mcp, ClassBuilder cb) {
		selectedMyClass = null;
		this.fcp = fcp;
		this.mcp = mcp;
		this.myClassList = cb.getClasslist();
		this.selectedSameFieldSigMap = new HashMap<MyClass, Pocket<SelectedType>>();
		this.selectedSameMethodSigMap = new HashMap<MyClass, Pocket<SelectedType>>();
		this.tablePane = new ClassTablePanel(this,myClassList);
		//テーブル情報を追加
		fcp.setTableModel(tablePane.getTableModel());
		mcp.setTableModel(tablePane.getTableModel());
		setLayout(new BorderLayout());
		initComponent();
		initActionEvent();
		setVisible(true);
	}

	public void setTableEditable(boolean isEditable){
		tablePane.setTableEditable(isEditable);
	}

	public FieldComparePanel getFcp() {
		return fcp;
	}

	public MethodComparePanel getMcp() {
		return mcp;
	}

	public ClassTablePanel getTablePane() {
		return tablePane;
	}

	public MyClass getSelectedMyClass() {
		return selectedMyClass;
	}

	public JTree getJtree() {
		return jtree;
	}

	private void initComponent(){
		JPanel panel,classNamePane,classFieldMethodPane;
		ClassNode child = null;
		holizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		compVerticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		astahRoot = new DefaultMutableTreeNode("SkeltonCodeClass");
		jtree = new JTree(astahRoot);
		jtree.setMinimumSize(new Dimension(150,200));

		classNamePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jtrSelClass = new JLabel();
		jtrSelClass.setFont(new Font("SansSerif", Font.BOLD, 18));
		jtrSelClass.setCursor(new Cursor(Cursor.HAND_CURSOR));
		classNamePane.add(jtrSelClass);

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
		//メソッドパネルを中央
		panel.add(mcp,BorderLayout.CENTER);
		mcpSourceStatus = new StatusBar(panel, "対応付けしてください");
		mcp.setStatus(mcpSourceStatus);
		mcpSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(400, 150));
		scrollPane.setSize(new Dimension(400, 200));
		panel = new JPanel(new BorderLayout());
		panel.add(scrollPane,BorderLayout.CENTER);
		panel.add(mcpSourceStatus,BorderLayout.SOUTH);
		compVerticalSplitePane.setBottomComponent(panel);

		//フィールドの対応パネル
		panel = new JPanel(new BorderLayout());
		//フィールドパネルを中央
		panel.add(fcp,BorderLayout.CENTER);
		fcpSourceStatus = new StatusBar(panel, "対応付けしてください");
		fcp.setStatus(fcpSourceStatus);
		fcpSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		scrollPane = new JScrollPane(panel);
		scrollPane.setMinimumSize(new Dimension(400, 150));
		scrollPane.setSize(new Dimension(400, 200));
		panel = new JPanel(new BorderLayout());
		panel.add(scrollPane,BorderLayout.CENTER);
		panel.add(fcpSourceStatus,BorderLayout.SOUTH);
		compVerticalSplitePane.setTopComponent(panel);

		compVerticalSplitePane.setContinuousLayout(true);

		//クラス,フィールド,メソッドをまとめる
		classFieldMethodPane = new JPanel();
		classFieldMethodPane.setLayout(new BorderLayout(5,5));
		classFieldMethodPane.add(classNamePane,BorderLayout.NORTH);
		classFieldMethodPane.add(compVerticalSplitePane,BorderLayout.CENTER);

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(classFieldMethodPane);
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
				StringBuilder popSb = new StringBuilder();
				IClass iClass;
				IComment[] comments;
				IConstraint[] constraints;

				if (obj instanceof ClassNode) {
					ClassNode selectedNode = (ClassNode) obj;
					Object userObj = selectedNode.getUserObject();

					if (userObj instanceof MyClass) {
						selectedMyClass = (MyClass) userObj;
						iClass = selectedMyClass.getIClass();
						//jtreeで選択したクラスを表示
						jtrSelClass.setText(selectedMyClass.getName());

						//説明を加える
						popSb.append("<html>");
						popSb.append("<p>");

						popSb.append("定義:<br>");
						if (selectedMyClass.getDefinition().isEmpty()) {
							popSb.append("なし<br>");
						}else{
							String[] strs = iClass.getDefinition().split("\\n", 0);

							for (String comment : strs) {
								popSb.append(comment + "<br>");
							}
						}

						popSb.append("コメント:<br>");
						if (iClass.getComments().length == 0) {
							popSb.append("なし<br>");
						}else{
							comments = iClass.getComments();

							for (IComment comment : comments){
								popSb.append("・"+comment.toString()+"<br>");
							}
						}

						popSb.append("制約:<br>");
						if (iClass.getConstraints().length == 0) {
							popSb.append("なし<br>");
						}else{
							constraints = iClass.getConstraints();

							for (IConstraint constraint : constraints){
								popSb.append("・"+constraint.toString()+"<br>");
							}
						}

						popSb.append("</p>");
						popSb.append("</html>");

						jtrSelClass.setToolTipText(popSb.toString());
						//パネルの更新
						reLoadMemberPane(selectedMyClass,false);
					}
				}

			}
		});

	}

	//TODO
	//フィールドやメソッドのボックスが一部空の場合に
	//どのように対処するか考える
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

					//TODO
					//nullでない
					if (obj != null) {
						methodCodeSigList.add(obj.toString());
					}
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

					//TODO
					//nullでない
					if (obj != null) {
						fieldCodeSigList.add(obj.toString());
					}
				}

			}
		}

		isMethodSame = false;
		isFieldSame = false;

		//同じメソッドが選択されていないかチェック
		//選択できないメソッドがあるかどうか
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
		//あるいは選択できないフィールドがあるかどうか
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

		if (fieldCodeSigList.size() == 0 ||
				fieldPanelList.size() - 1 != fieldCodeSigList.size()) {
			fcpSourceStatus.setColor(Color.red);
			fcpSourceStatus.setText("フィールドが空です");
			setGeneratable(myClass, false);
		}

		if (methodCodeSigList.size() == 0 || 
				methodPanelList.size() - 1 != methodCodeSigList.size()) {
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

	/**
	 * フィールドのボックスに空があるかどうか判定するメソッド
	 * @return
	 */
	public boolean isFieldPanelEmpty(){
		boolean isEmpty = false;
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

						if(box_1.getSelectedItem() == null){
							isEmpty = true;
							return isEmpty;
						}
					}
				}
			}
		}

		return isEmpty;
	}

	/**
	 * メソッドのボックスに空があるかどうか判定するメソッド
	 * @return
	 */
	public boolean isMethodPanelEmpty(){
		boolean isEmpty = false;

		List<JPanel> methodPanelList;
		Component comp;
		JComboBox box_1;
		JPanel panel;

		for(MyClass myClass : generatableMap.keySet()){
			methodPanelList = mcp.getMapPanelList().get(myClass);

			System.out.println("myClass : " + myClass.getName());

			for(int i = 0; i<methodPanelList.size();i++){
				panel = methodPanelList.get(i);

				for(int j = 0; j<panel.getComponentCount();j++){
					comp = panel.getComponent(j);

					if(comp instanceof JComboBox){
						box_1 = (JComboBox) comp;

						if(box_1.getSelectedItem() == null){
							isEmpty = true;
							return isEmpty;
						}
					}
				}
			}
		}
		return isEmpty;

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

						if (!generatable) {
							return generatable;
						}
					}
				}
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

			System.out.println("myClass : " + myClass.getName());

			for(int i = 0; i<methodPanelList.size();i++){
				panel = methodPanelList.get(i);

				for(int j = 0; j<panel.getComponentCount();j++){
					comp = panel.getComponent(j);

					if(comp instanceof JComboBox){
						box_1 = (JComboBox) comp;

						generatable = !checkSameItemSelected(i, box_1, methodPanelList);
						if (!generatable) {
							return generatable;
						}
					}
				}
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
		Object obj;

		obj = box_1.getSelectedItem();

		//ボックスのアイテムが空の場合
		if (obj == null){
			return true;
		}

		strBox_1 = box_1.getSelectedItem().toString();

		System.out.println("strBox_1 :  " + strBox_1);
		for(int i=0;i<panelList.size();i++){

			if (i == index) {
				continue ;
			}

			panel = panelList.get(i);

			for(int j = 0 ; j <panel.getComponentCount();j++){
				comp = panel.getComponent(j);

				if (comp instanceof JComboBox) {
					box_2 = (JComboBox) comp;

					obj = box_2.getSelectedItem();

					//ボックスのアイテムが空の場合
					if (obj == null) {
						continue ;
					}

					strBox_2 = box_2.getSelectedItem().toString();

					System.out.println("strBox_2 :  " + strBox_2);
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

		System.out.println("isSame : "+isSame);
		return isSame;
	}

	public boolean isFieldEpmty() {
		boolean generatable = true;
		List<JPanel> fieldPanelList;

		for(MyClass myClass : generatableMap.keySet()){
			fieldPanelList = fcp.getMapPanelList().get(myClass);

			//（左）...（右)...
			//のラベルを含めるので1
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
			methodPanelList = mcp.getMapPanelList().get(myClass);

			//（左）...（右)...
			//のラベルを含めるので1
			if (methodPanelList.size() <= 1) {
				generatable = false;
				break;
			}

		}
		return generatable;
	}
}