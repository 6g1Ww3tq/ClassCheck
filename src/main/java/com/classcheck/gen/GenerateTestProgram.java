package com.classcheck.gen;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.io.FileUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.MyClassCell;
import com.classcheck.panel.AddonTabPanel;
import com.classcheck.panel.ClassTablePanel;
import com.classcheck.panel.ConstructorPanel;
import com.classcheck.panel.FieldComparePanel;
import com.classcheck.panel.MemberTabPanel;
import com.classcheck.panel.MethodComparePanel;
import com.classcheck.tree.FileNode;
import com.classcheck.window.DebugMessageWindow;
import com.classcheck.window.SelectConstructorViewer;
import com.classcheck.window.TestCodeEditWindow;

public class GenerateTestProgram {
	//出力元となるディレクトリ
	File baseDir;
	//出力先テストディレクトリ
	File oustTestDir;
	//出力するテストファイル名のリスト
	List<String> testJavaFileNameList;

	//テーブルの対応付け
	private Map<MyClass,CodeVisitor> tableMap;

	//アスタクラス->メッセージの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> methodChangeMap;

	//フィールドの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> fieldChangeMap;

	//javaファイルのパスやデータを格納するリストを用意する(import文に使用する)
	private List<FileNode> javaFileNodeList;

	//検証用のモッククラス
	private static String[] myMockVerificationClassNames = {
		"MyVerificationsInOrder",
		"MyVerifications"
	};

	private MemberTabPanel mtp;
	private FieldComparePanel fcp;
	private MethodComparePanel mcp;
	private ClassTablePanel tablePane;

	public GenerateTestProgram(File baseDir, MemberTabPanel mtp, List<FileNode> javaFileNodeList) {
		this.baseDir = baseDir;
		this.mtp = mtp;
		this.fcp = mtp.getFcp();
		this.mcp = mtp.getMcp();
		this.tablePane = mtp.getTablePane();
		this.javaFileNodeList = javaFileNodeList;
		this.testJavaFileNameList = new ArrayList<String>();
	}

	public boolean doExec(){
		boolean successed = true;

		makeChangeMap();
		viewChangeMap();
		successed = makeFile();

		if (successed) {
			makeInnerClass();
			makeLibDir();
			makeRunScriptFile();
		}

		DebugMessageWindow.msgToTextArea();
		return successed;
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

		DebugMessageWindow.msgToTextArea();
	}

	private void makeChangeMap() {
		tableMap = new HashMap<MyClass, CodeVisitor>();
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
				//テーブルの対応付け
				tableMap.put(myClass, codeVisitor);

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
							String labelText = label.getText();

							if (!labelText.contains("(左)クラス図で定義したメソッド") ||
									!labelText.contains("ヒント")){
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
							String labelText = label.getText();

							if (!labelText.contains("(左)クラス図で定義したメソッド") ||
									!labelText.contains("ヒント")){
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
			}

		}

	}

	private boolean makeFile() {
		boolean successed = true;
		Map<String,String> fileMap;
		StringBuilder sb = null;
		Map<CodeVisitor, String> generatedCodesMap;
		MakeTestFile makeFile = null;
		SelectConstructorViewer scv = null;
		List<ConstructorPanel> cPaneList = null;
		TestCodeEditWindow tced = null;
		HashMap<String, RSyntaxTextArea> exportEditCodeMap;

		try {
			//アスタと学生のソースコードを元にしたプログラムの生成
			ChangeSkeltonCode cmc = new ChangeSkeltonCode(mtp,tableMap,fieldChangeMap,methodChangeMap);
			cmc.change();

			//テストプログラムの
			//初期化コンストラクタの指定
			generatedCodesMap = cmc.getGeneratedCodesMap();
			scv = new SelectConstructorViewer(generatedCodesMap);

			successed = !scv.isCanceled();
			if (scv.isCanceled()) {
				successed = false;
				return successed;
			}

			//加工後の文字列をテスト用にする(javaparserを使用する)
			cPaneList = scv.getCtp().getConstructorPaneList();
			makeFile = new MakeTestFile(baseDir,javaFileNodeList,tableMap,methodChangeMap,fieldChangeMap,generatedCodesMap,cPaneList,tableMap.values());
			//途中でテストプログラムの生成をキャンセルした場合は中断する
			if (makeFile.make() == false) {
				successed = false;
				return successed;
			}

			fileMap = makeFile.getFileMap();

			//ユーザによるテストプログラムの編集
			tced = new TestCodeEditWindow(fileMap);
			successed = !tced.isCanceled();
			if (tced.isCanceled()) {
				successed = false;
				return successed;
			}

			//ユーザーがテストコードを修正したあとのテストコード
			exportEditCodeMap = tced.getExportEditCodeMap();

			//テストディレクトリの作成
			makeTestDir();

			//テストコードを出力していく
			for (String exportFileName : exportEditCodeMap.keySet()) {
				RSyntaxTextArea userEditCode_Str = exportEditCodeMap.get(exportFileName);
				//テストファイル名を記録する
				testJavaFileNameList.add(exportFileName);
				//ファイル出力
				FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+exportFileName), userEditCode_Str.getText());
			}

			DebugMessageWindow.msgToTextArea();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return successed;
	}

	private void makeInnerClass() {
		StringBuilder inOrder_sb = new StringBuilder();
		StringBuilder verifications_sb = new StringBuilder();
		String exportMyVerificationsInOrder = myMockVerificationClassNames[0]+".java";
		String exportMyVerifications = myMockVerificationClassNames[1]+".java";

		inOrder_sb.append("import mockit.VerificationsInOrder;"+"\n");
		inOrder_sb.append("\n");
		inOrder_sb.append("\r\t"+"public class MyVerificationsInOrder extends VerificationsInOrder{"+"\n");
		inOrder_sb.append("\r\t\t"+"private boolean success;"+"\n");
		inOrder_sb.append("\r\t\t"+"public MyVerificationsInOrder() {"+"\n");
		inOrder_sb.append("\r\t\t\t"+"super();"+"\n");
		inOrder_sb.append("\r\t\t\t"+"success = false;"+"\n");
		inOrder_sb.append("\r\t\t"+"}"+"\n");
		inOrder_sb.append("\r\t\t\t"+"public boolean isSuccess() {"+"\n");
		inOrder_sb.append("\r\t\t\t"+"return success;"+"\n");
		inOrder_sb.append("\r\t\t"+"}"+"\n");
		inOrder_sb.append("\r\t\t"+"public void setSuccess(boolean success) {"+"\n");
		inOrder_sb.append("\r\t\t\t"+"this.success = success;"+"\n");
		inOrder_sb.append("\r\t\t"+"}"+"\n");
		inOrder_sb.append("\r\t"+"}"+"\n");
		inOrder_sb.append("\n");


		verifications_sb.append("import mockit.Verifications;"+"\n");
		verifications_sb.append("\n");
		verifications_sb.append("\r\t"+"public class MyVerifications extends Verifications{"+"\n");
		verifications_sb.append("\r\t\t"+"private boolean success;"+"\n");
		verifications_sb.append("\r\t\t"+"public MyVerifications() {"+"\n");
		verifications_sb.append("\r\t\t\t"+"super();"+"\n");
		verifications_sb.append("\r\t\t\t"+"success = false;"+"\n");
		verifications_sb.append("\r\t\t"+"}"+"\n");
		verifications_sb.append("\r\t\t\t"+"public boolean isSuccess() {"+"\n");
		verifications_sb.append("\r\t\t\t"+"return success;"+"\n");
		verifications_sb.append("\r\t\t"+"}"+"\n");
		verifications_sb.append("\r\t\t"+"public void setSuccess(boolean success) {"+"\n");
		verifications_sb.append("\r\t\t\t"+"this.success = success;"+"\n");
		verifications_sb.append("\r\t\t"+"}"+"\n");
		verifications_sb.append("\r\t"+"}"+"\n");
		verifications_sb.append("\n");

		//ファイル出力
		try {
			FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+exportMyVerificationsInOrder), inOrder_sb.toString());
			//FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+exportMyVerifications), verifications_sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * テストフォルダを作成し、
	 * テストコードを生成する
	 */
	private void makeTestDir() {
		oustTestDir = new File(baseDir.getPath()+"/test");
		System.out.println(oustTestDir);
		try {
			FileUtils.forceMkdir(oustTestDir);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void makeLibDir() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File outLibDir = new File(baseDir.getPath()+"/test/lib");

			FileUtils.copyURLToFile(classLoader.getResource("/lib/hamcrest/hamcrest-core/hamcrest-core-1.3.jar"), new File(outLibDir.getAbsolutePath() + "/hamcrest/hamcrest-core/hamcrest-core-1.3.jar"));
			FileUtils.copyURLToFile(classLoader.getResource("/lib/jmockit/jmockit-1.33.jar"),new File(outLibDir.getAbsolutePath() + "/jmockit/jmockit-1.33.jar"));
			FileUtils.copyURLToFile(classLoader.getResource("/lib/junit/junit-4.12.jar"),new File(outLibDir.getAbsolutePath() + "/junit/junit-4.12.jar"));
		}catch (IOException e){
			System.out.println(e.toString());
			e.printStackTrace();
		} 

	}

	/*
	 * 実行＆コンパイルのスクリプトファイルを生成する
	 * 
	 * FIXME
	 * Windows　Linux　の環境に合わせたスクリプトファイルを生成する
	 */
	private void makeRunScriptFile() {
		String os_name = System.getProperty("os.name").toLowerCase();
		String buildFileName = null;
		String runFileName = null;
		StringBuilder build_sb = new StringBuilder();
		StringBuilder run_sb = new StringBuilder();

		try {
			if (os_name.startsWith("linux")) {
				buildFileName = "build.sh";
				runFileName = "run.sh";
				makeShellScript(build_sb,run_sb);
				//ファイル出力
				FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+buildFileName), build_sb.toString());
				FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+runFileName), run_sb.toString());
			}else if (os_name.startsWith("windows")) {
				buildFileName = "build.bat";
				runFileName = "run.bat";
				makeBatScript(build_sb,run_sb);
				//ファイル出力
				FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+buildFileName), build_sb.toString());
				FileUtils.writeStringToFile(new File(oustTestDir.getPath()+"/"+runFileName), run_sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeShellScript(StringBuilder build_sb, StringBuilder run_sb) {
		//ライブラリのパス文字列
		String jarPath_str = AddonTabPanel.getJarPathTextField().getText();
		//エンコーディングの文字列
		String encoding_str = AddonTabPanel.getEncodingFormat();
		build_sb.append("javac ");
		build_sb.append("-cp ");

		//外部ライブラリの追加
		if (jarPath_str.isEmpty() == false) {
			build_sb.append(jarPath_str + ":");
		}
		build_sb.append("classes/:.:lib/jmockit/jmockit-1.33.jar:lib/junit/junit-4.12.jar:lib/hamcrest/hamcrest-core/hamcrest-core-1.3.jar: ");

		//エンコーディングの追加
		if (encoding_str.isEmpty() == false) {
			build_sb.append("-encoding ");
			build_sb.append(encoding_str +" ");
		}
		//テストコード(検証用のモッククラスは同じディレクトリにあるのでコンパイルされる)
		for (String testFileName : testJavaFileNameList) {
			build_sb.append(testFileName+" ");
		}

		run_sb.append("java ");
		run_sb.append("-cp ");
		//外部ライブラリの追加
		if (jarPath_str.isEmpty() == false) {
			run_sb.append(jarPath_str + ":");
		}
		//エンコーディングの追加
		if (encoding_str.isEmpty() == false) {
			build_sb.append("-encoding ");
			build_sb.append(encoding_str +" ");
		}
		run_sb.append("classes/:.:lib/jmockit/jmockit-1.33.jar:lib/junit/junit-4.12.jar:lib/hamcrest/hamcrest-core/hamcrest-core-1.3.jar: ");
		run_sb.append("org.junit.runner.JUnitCore ");
		for (String testFileName : testJavaFileNameList) {
			run_sb.append(testFileName.replaceAll("\\.java$", "")+" ");
		}
	}

	private void makeBatScript(StringBuilder build_sb,StringBuilder run_sb) {
		// FIXME 自動生成されたメソッド・スタブ
	}

	private void makeClassTableCSV() {
		String exportFileName = "Interaction.csv";
		MakeClassTableCSV mct = new MakeClassTableCSV(exportFileName,tableMap,fieldChangeMap,methodChangeMap);
		mct.exportPath(baseDir.getPath());
	}
}