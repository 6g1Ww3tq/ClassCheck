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

public class SetTabPane extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	AstahAndSourcePanel astahAndSourcePane;

	JSplitPane holizontalSplitePane;
	JSplitPane verticalSplitePane;
	JTree jtree;
	DefaultMutableTreeNode astahRoot;
	List<MyClass> myClassList;
	Map<MyClass, Pocket<SelectedType>> selectedSameSigMap;

	StatusBar astahTreeStatus;
	StatusBar astahAndSourceStatus;

	CompTablePane tablePane;

	public SetTabPane(AstahAndSourcePanel astahAndSourcePane, ClassBuilder cb) {
		this.astahAndSourcePane = astahAndSourcePane;
		this.myClassList = cb.getClasslist();
		this.selectedSameSigMap = new HashMap<MyClass, Pocket<SelectedType>>();
		this.tablePane = new CompTablePane(this,myClassList, astahAndSourcePane);
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

	private void initComponent(){
		JPanel panel;
		ClassNode child = null;
		holizontalSplitePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		holizontalSplitePane.setSize(new Dimension(400, 400));
		verticalSplitePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		astahRoot = new DefaultMutableTreeNode("AstahClass");
		jtree = new JTree(astahRoot);
		jtree.setSize(new Dimension(200,200));

		//比較パネルの初期化
		for (MyClass myClass : myClassList) {
			child = new ClassNode(myClass);
			astahRoot.add(child);
			//デフォルトでパネルに初期値データを入れる
			astahAndSourcePane.initComponent(myClass,true);
			selectedSameSigMap.put(myClass, new Pocket<SelectedType>(SelectedType.OTHER));
		}

		//デフォルトでパネルに初期値データを入れる
		//->表示させないようにする
		astahAndSourcePane.removeAll();
		astahAndSourcePane.revalidate();

		//上の左右パネル
		//astah tree(左)
		panel = new JPanel(new BorderLayout());
		panel.add(jtree,BorderLayout.CENTER);
		astahTreeStatus = new StatusBar(panel, "Astah-Class");
		panel.add(astahTreeStatus,BorderLayout.SOUTH);
		JScrollPane treeScrollPane = new JScrollPane(panel);
		treeScrollPane.setSize(new Dimension(200, 300));	

		//astah and source panel(右）
		panel = new JPanel(new BorderLayout());
		panel.add(astahAndSourcePane,BorderLayout.CENTER);
		astahAndSourceStatus = new StatusBar(panel, "対応付けしてください");
		astahAndSourceStatus.setStatusLabelFont(new Font("SansSerif", Font.BOLD, 15));
		panel.add(astahAndSourceStatus,BorderLayout.SOUTH);
		JScrollPane astahAndSourceScrollPane = new JScrollPane(panel);
		astahAndSourceScrollPane.setSize(new Dimension(200,300));	

		holizontalSplitePane.setLeftComponent(treeScrollPane);
		holizontalSplitePane.setRightComponent(astahAndSourceScrollPane);
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
						MyClass myClass = (MyClass) userObj;
						//パネルの更新
						reLoadAstahAndSourcePane(myClass,false);
					}
				}
			}
		});
	}

	public void reLoadAstahAndSourcePane(MyClass myClass,boolean isAllChange){
		Map<MyClass, List<JPanel>> mapPanelList = astahAndSourcePane.getMapPanelList();
		List<JPanel> panelList = mapPanelList.get(myClass);
		Component component;
		JLabel astahSigLabel = null;
		JComboBox<String> codeSigBox = null;
		List<String> codeSigList = new ArrayList<String>();
		String codeSig = null;
		Object obj = null;
		int sameCount = 0;
		Pocket<SelectedType> pocket = null;

		//パネルの更新
		astahAndSourcePane.removeAll();
		astahAndSourcePane.revalidate();
		astahAndSourcePane.initComponent(myClass,isAllChange);
		astahTreeStatus.setText("Astah-Class:"+myClass.getName());

		//ステータスバーによるエラーチェック
		for (JPanel panel : panelList) {
			for (int i = 0; i < panel.getComponentCount(); i++) {
				component = panel.getComponent(i);

				if (component instanceof JLabel) {
					astahSigLabel = (JLabel) component;
				}

				if (component instanceof JComboBox<?>) {
					codeSigBox = (JComboBox<String>) component;
				}
			}
			//astah sig : code sig を取得完了

			if (astahSigLabel != null && codeSigBox != null) {
				if (!astahSigLabel.getText().contains("(左)astahのメソッド,コンストラクタのシグネチャ")) {
					obj = codeSigBox.getSelectedItem();

					if (obj instanceof String) {
						codeSig = (String) obj;
						codeSigList.add(codeSig);
					}
				}
			}
		}

		for (String baseSig : codeSigList) {
			sameCount = 0;

			for (String str : codeSigList) {
				if (baseSig.equals(str)) {
					sameCount++;
				}
			}

			pocket = selectedSameSigMap.get(myClass);

			if (sameCount > 1) {
				pocket.set(SelectedType.SAME);
			}else{
				pocket.set(SelectedType.NOTSAME);
			}
		}

		pocket = selectedSameSigMap.get(myClass);

		if (pocket.get() == SelectedType.SAME) {
			astahAndSourceStatus.setColor(Color.red);
			astahAndSourceStatus.setText("同じシグネチャーを選択しないでください");
		}else if (pocket.get() == SelectedType.NOTSAME) {
			astahAndSourceStatus.setColor(Color.green);
			astahAndSourceStatus.setText("OK");
		}
	}
}