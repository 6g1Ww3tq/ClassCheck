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
import com.classcheck.panel.MethodCompPanel;
import com.classcheck.panel.CompTablePane;
import com.classcheck.window.DebugMessageWindow;

public class GenerateTestProgram {
	//出力元となるディレクトリ
	File baseDir;
	//出力先テストディレクトリ
	File outDir;

	//アスタクラス->メッセージの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> changeMap;

	private MethodCompPanel mcp;
	private CompTablePane tablePane;

	public GenerateTestProgram(File baseDir,
			MethodCompPanel mcp, CompTablePane tablePane) {
		this.baseDir = baseDir;
		this.mcp = mcp;
//		this.mapPanelList = mcp.getMapPanelList();
//		this.codeMap = mcp.getCodeMap();
		this.tablePane = tablePane;
		makeChangeMap();
		viewChangeMap();
		makeTestDir();
		makeHelloFile();
	}

	private void viewChangeMap() {
		Map<String, String> messagesMap = null;

		DebugMessageWindow.clearText();
		for(MyClass myClass : changeMap.keySet()){
			messagesMap = changeMap.get(myClass);

			System.out.println("*****"+myClass.getName()+"*****");
			for(String bef : messagesMap.keySet()){
				System.out.println("befM : " + bef + "\n"
						+ "aftM : " + messagesMap.get(bef));
			}
		}

		DebugMessageWindow.msgToOutPutTextArea();
	}

	private void makeChangeMap() {
		changeMap = new HashMap<MyClass, Map<String,String>>();
		Map<String, String> messagesMap = null;
		String befMessage,aftMessage;
		DefaultTableModel tm = tablePane.getTableModel();
		Object obj = null;
		MyClassCell myClassCell = null;
		MyClass myClass = null;
		JComboBox comboBox = null;
		CodeVisitor codeVisitor = null;
		Map<MyClass, List<JPanel>> mapPanelList =
				mcp.getMapPanelList();
		List<JPanel> panelList = null;
		Component component = null;
		JLabel label = null;
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
				messagesMap = new HashMap<String, String>();

				panelList = mapPanelList.get(myClass);

				for (JPanel panel : panelList) {

					component = null;
					label = null;
					methodComboBox = null;
					befMessage = aftMessage = null;
					for(int j=0 ; j < panel.getComponentCount() ; j++){
						component = panel.getComponent(j);

						if (component instanceof JLabel) {
							label = (JLabel) component;

							if (!label.getText().
									contains("(左)astahのメソッド")) {
								befMessage = label.getText().substring(0, label.getText().length() - 3);
							}else{
								continue;
							}
						}

						if (component instanceof JComboBox<?>) {
							methodComboBox = (JComboBox) component;

							if (methodComboBox.getSelectedItem() instanceof String) {
								aftMessage = (String)methodComboBox.getSelectedItem();
							}
						}

					}

					if (label != null && methodComboBox != null
							&& befMessage != null && aftMessage != null) {
						messagesMap.put(befMessage, aftMessage);
					}
				}

				//last
				changeMap.put(myClass, messagesMap);
				//System.out.println(messagesMap);
			}

		}

		//System.out.println(changeMap);
	}

	private void makeHelloFile() {
		try {
			FileUtils.writeStringToFile(new File(outDir.getPath()+"/hello.txt"), "hello world");
			ChangeMyClass cmc = new ChangeMyClass(mcp,changeMap);
			
			//アスタと学生のソースコードを元にしたプログラムの生成
			cmc.change();
			cmc.getGeneratedCodes();
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