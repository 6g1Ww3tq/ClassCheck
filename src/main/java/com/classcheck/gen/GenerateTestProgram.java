package com.classcheck.gen;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.MyClassCell;
import com.classcheck.panel.CompTablePane;
import com.classcheck.panel.FieldCompPanel;
import com.classcheck.panel.MemberTabPane;
import com.classcheck.panel.MethodCompPanel;
import com.classcheck.window.DebugMessageWindow;

public class GenerateTestProgram {
	//出力元となるディレクトリ
	File baseDir;
	//出力先テストディレクトリ
	File outDir;

	//アスタクラス->メッセージの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> methodChangeMap;
	
	//フィールドの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> fieldChangeMap;

	private MemberTabPane mtp;
	private FieldCompPanel fcp;
	private MethodCompPanel mcp;
	private CompTablePane tablePane;

	public GenerateTestProgram(File baseDir, MemberTabPane mtp) {
		this.baseDir = baseDir;
		this.mtp = mtp;
		this.fcp = mtp.getFcp();
		this.mcp = mtp.getMcp();
		this.tablePane = mtp.getTablePane();
		makeChangeMap();
		viewChangeMap();
		makeTestDir();
		makeFile();
	}

	private void viewChangeMap() {
		Map<String, String> fieldsMap = null;
		Map<String, String> methodsMap = null;

		DebugMessageWindow.clearText();
		for(MyClass myClass : methodChangeMap.keySet()){
			fieldsMap = fieldChangeMap.get(myClass);
			methodsMap = methodChangeMap.get(myClass);

			System.out.println("*****"+myClass.getName()+"*****");
			
			for(String bef :fieldsMap.keySet()){
				System.out.println("befM : " + bef + "\n"
						+ "aftM : " + fieldsMap.get(bef));
			}
			
			for(String bef : methodsMap.keySet()){
				System.out.println("befM : " + bef + "\n"
						+ "aftM : " + methodsMap.get(bef));
			}
		}

		DebugMessageWindow.msgToOutPutTextArea();
	}

	private void makeChangeMap() {
		methodChangeMap = new HashMap<MyClass, Map<String,String>>();
		fieldChangeMap = new HashMap<MyClass, Map<String,String>>();
		Map<String, String> methodsMap = null;
		Map<String, String> fieldsMap = null;
		String befFieldStr,aftFieldStr,befMethodStr,aftMethodStr;
		DefaultTableModel tm = tablePane.getTableModel();
		Object obj = null;
		MyClassCell myClassCell = null;
		MyClass myClass = null;
		JComboBox comboBox = null;
		CodeVisitor codeVisitor = null;
		Map<MyClass, List<JPanel>> methodMapPanelList =
				mcp.getMapPanelList();
		Map<MyClass, List<JPanel>> fieldMapPanelList =
				fcp.getMapPanelList();
		List<JPanel> methodPanelList = null;
		List<JPanel> fieldPanelList = null;
		Component component = null;
		JLabel label = null;
		JComboBox<String> fieldComboBox = null;
		JComboBox<String> methodComboBox = null;

		for(int i = 0 ; i < tm.getRowCount() ; i++){
			myClassCell = null;
			myClass = null;
			comboBox = null;
			codeVisitor = null;
			obj = tm.getValueAt(i, 0);

			if (obj instanceof MyClassCell) {
				myClassCell = (MyClassCell) obj;
				myClass = myClassCell.getMyClass();
			}

			obj = tm.getValueAt(i, 1);

			if (obj instanceof JComboBox<?>) {
				//テーブルが変更されていないデフォルト状態の処理
				comboBox = (JComboBox) obj;

				if (comboBox.getSelectedItem() instanceof CodeVisitor) {
					codeVisitor = (CodeVisitor) comboBox.getSelectedItem();
				}
			}else if (obj instanceof CodeVisitor) {
				//テーブルが変更後の処理
				codeVisitor = (CodeVisitor) obj;
			}

			if (myClass != null && codeVisitor != null) {
				fieldsMap = new HashMap<String, String>();
				methodsMap = new HashMap<String, String>();

				fieldPanelList = fieldMapPanelList.get(myClass);
				methodPanelList = methodMapPanelList.get(myClass);

				//フィールド処理
				for (JPanel panel : fieldPanelList) {
					component = null;
					label = null;
					fieldComboBox = null;
					befFieldStr = aftFieldStr = null;
					for(int j=0; j < panel.getComponentCount();j++){
						component = panel.getComponent(j);

						if (component instanceof JLabel) {
							label = (JLabel) component;

							if (!label.getText().
									contains("(左)astahのフィールド")) {
								befFieldStr = label.getText().replaceAll("^\\s+","");
							}else{
								continue;
							}
						}
						
						if (component instanceof JComboBox<?>) {
							fieldComboBox = (JComboBox) component;

							if (fieldComboBox.getSelectedItem() instanceof String) {
								aftFieldStr = (String)fieldComboBox.getSelectedItem();
							}
						}
						
					}
					
					if (label != null && fieldComboBox != null
							&& befFieldStr != null && aftFieldStr != null) {
						fieldsMap.put(befFieldStr, aftFieldStr);
					}
				}
				
				//メソッド処理
				for (JPanel panel : methodPanelList) {

					component = null;
					label = null;
					methodComboBox = null;
					befMethodStr = aftMethodStr = null;
					for(int j=0 ; j < panel.getComponentCount() ; j++){
						component = panel.getComponent(j);

						if (component instanceof JLabel) {
							label = (JLabel) component;

							if (!label.getText().
									contains("(左)astahのメソッド")) {
								befMethodStr = label.getText().replaceAll("^\\s+","");
							}else{
								continue;
							}
						}

						if (component instanceof JComboBox<?>) {
							methodComboBox = (JComboBox) component;

							if (methodComboBox.getSelectedItem() instanceof String) {
								aftMethodStr = (String)methodComboBox.getSelectedItem();
							}
						}

					}

					if (label != null && methodComboBox != null
							&& befMethodStr != null && aftMethodStr != null) {
						methodsMap.put(befMethodStr, aftMethodStr);
					}
				}

				//last
				fieldChangeMap.put(myClass, fieldsMap);
				methodChangeMap.put(myClass, methodsMap);
				//System.out.println(methodsMap);
			}

		}

		//System.out.println(methodChangeMap);
	}

	private void makeFile() {
		StringBuilder sb = null;
		try {
			//アスタと学生のソースコードを元にしたプログラムの生成
			ChangeMyClass cmc = new ChangeMyClass(mtp,fieldChangeMap,methodChangeMap);
			cmc.change();
			cmc.getGeneratedCodes();
			
			//ファイル出力
			FileUtils.writeStringToFile(new File(outDir.getPath()+"/hello.txt"), "hello world");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * テストフォルダを作成し、
	 * テストコードを生成する
	 */
	private void makeTestDir() {
		outDir = new File(baseDir.getPath()+"/test");
		System.out.println(outDir);
		DebugMessageWindow.msgToOutPutTextArea();
		try {
			FileUtils.forceMkdir(outDir);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}
}