package com.classcheck.gen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.ConstructorPanel;
import com.classcheck.tree.FileNode;
import com.classcheck.window.SequentialOrderOptionWindow;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class MakeTestFile {
	//ソースコードの基準となるディレクトリ
	private File baseDir;
	//ソースコードがあるパスを格納したリスト
	private List<FileNode> javaFileNodeList;

	//テーブルの対応付け
	private Map<MyClass,CodeVisitor> tableMap;

	//アスタクラス->メッセージの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> methodChangeMap;

	//フィールドの前と後を表す対応関係を抽出
	private Map<MyClass, Map<String, String>> fieldChangeMap;

	private Map<CodeVisitor, String> generatedCodesMap;
	private Map<String,String> fileMap;
	private List<ConstructorPanel> cPanelList;
	private Collection<CodeVisitor> codeCollection;

	public MakeTestFile(File baseDir, List<FileNode> javaFileNodeList, Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> methodChangeMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<CodeVisitor, String> generatedCodesMap,
			List<ConstructorPanel> cPaneList,
			Collection<CodeVisitor> codeCollection) {
		this.baseDir = baseDir;
		this.javaFileNodeList = javaFileNodeList;
		this.tableMap = tableMap;
		this.methodChangeMap = methodChangeMap;
		this.fieldChangeMap = fieldChangeMap;
		this.generatedCodesMap = generatedCodesMap;
		this.codeCollection = codeCollection;
		this.cPanelList = cPaneList;
		this.fileMap = new HashMap<String, String>();
	}

	public Map<String, String> getFileMap() {
		return fileMap;
	}

	public boolean make(){
		String skeltonCode = null;
		String fileName = null;
		StringBuilder sb = null;
		List<String> mockParamsList;
		HashMap<String, String> mockMethodMap;
		TestSkeltonCodeVisitor skeVisitor= null;
		CompilationUnit cu = null;
		HashMap<String, String> variableFieldNameMap;

		for(CodeVisitor codeVisitor : generatedCodesMap.keySet()){
			fileName = codeVisitor.getClassName() + "Test" + ".java";
			sb = new StringBuilder();
			makeHeader(sb);
			skeltonCode = generatedCodesMap.get(codeVisitor);

			try {
				cu = JavaParser.parse(new ByteArrayInputStream(skeltonCode.getBytes()));
				//引数にすべてのcoddevisitorのSetを入れる
				skeVisitor = new TestSkeltonCodeVisitor(codeVisitor,tableMap,fieldChangeMap,methodChangeMap,codeCollection);
				variableFieldNameMap = skeVisitor.getVariableFieldNameMap();
				cu.accept(skeVisitor, null);

				mockParamsList = skeVisitor.getMockFieldList();
				mockMethodMap = skeVisitor.getMockMethodMap();

				//テストするメソッドに対して順番を厳守するかどうかの選択を行うUIを表示
				Set<String> testMethodSet = mockMethodMap.keySet();
				SequentialOrderOptionWindow soow = new SequentialOrderOptionWindow(codeVisitor.getClassName(),testMethodSet);
				//キャンセルボタンが押されたら中断
				if (soow.isCanceled()) {
					return false;
				}

				//クラス部分を作る
				makeClassName(sb,codeVisitor.getClassName());

				for(int i=0;i<cPanelList.size();i++){
					if(cPanelList.get(i).getCodeVisitor().equals(codeVisitor)){
						makeMethod(soow,
								sb,
								codeVisitor.getClassName(),
								variableFieldNameMap,
								mockMethodMap,
								mockParamsList,
								cPanelList.get(i));
					}
				}

				//public class ?Test の　ブロック閉
				sb.append("\r}\n");

				fileMap.put(fileName, sb.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		//テストプログラムの生成が成功
		return true;
	}

	private void makeMethod(SequentialOrderOptionWindow soow,
			StringBuilder sb,
			String className,
			HashMap<String, String> variableFieldNameMap,
			HashMap<String, String> mockMethodMap,
			List<String> mockParamsList,
			ConstructorPanel constructorPane) {
		Enumeration<AbstractButton> buttons = constructorPane.getGroup().getElements();
		Map<AbstractButton, String> abstructBtnMap = constructorPane.getAbstractBtnMap();
		AbstractButton button;
		AbstractButton selectedButton = null;
		String paramStr,constructorStr;
		List<String> mockParamStrList = null;
		String[] split_str = null;

		while(buttons.hasMoreElements()){
			button = buttons.nextElement();

			if(button.isSelected()){
				selectedButton = button;
				break;
			}
		}


		for(String methodSigNature_str : mockMethodMap.keySet()){
			DisassemblyMethodSignature disassemblyMethod = new DisassemblyMethodSignature(methodSigNature_str);
			mockParamStrList = new ArrayList<String>();
			sb.append("\n");
			sb.append("\r\t\t"+"@Test"+"\n");
			//パラメータに@Mockedを使うかどうか
			sb.append("\r\t\t"+"public " + "void " + disassemblyMethod.getMethodName() + "_" + "Test" + "(");

			for(int i=0;i < mockParamsList.size();i++){
				paramStr = mockParamsList.get(i);
				sb.append(paramStr);

				//モックの変数名をリストに加える
				split_str = paramStr.split(" ");
				mockParamStrList.add(split_str[split_str.length - 1]);

				if(i<mockParamsList.size()-1){
					sb.append(",");
				}
			}

			sb.append(")"+ " {" +"\n");

			//try-catch
			sb.append("\r\t\t"+"try {"+"\n");

			//init
			sb.append("\n");
			if (selectedButton != null) {
				sb.append("\r\t\t\t"+"//初期化"+"\n"); 
				sb.append("\r\t\t\t"+ className +" object " + "=" +" "+"new "); //objectはテストするクラスに対してのオブジェクト

				if (abstructBtnMap.get(selectedButton) != null) {
					//定義したコンストラクタ
					DisassemblyMethodSignature dm;
					constructorStr = abstructBtnMap.get(selectedButton);
					dm = new DisassemblyMethodSignature(constructorStr);
					constructorStr = dm.toString();
					sb.append(constructorStr +";");
				}else{
					//デフォルトコンストラクタ
					sb.append(className + "()"+";");
				}

				sb.append("\n");
			}
			sb.append("\n");

			//reflectionを用いてフィールド(privateでも)にモックオブジェクトをセットする
			sb.append("\r\t\t\t"+"//フィールドにセットする"+"\n"); 
			sb.append("\r\t\t\t"+"Class clazz"+"="+"object.getClass();"+"\n"); //objectはテストするクラスに対してのオブジェクト
			for(int i_paramStrList = 0 ;i_paramStrList<mockParamStrList.size();i_paramStrList++){
				String mockFieldName = mockParamStrList.get(i_paramStrList);
				sb.append("\r\t\t\t"+"Field field_"+i_paramStrList+ " = " +"clazz.getDeclaredField("+"\""+mockFieldName+"\""+");"+"\n"); 
				sb.append("\r\t\t\t"+"field_"+i_paramStrList+".setAccessible(true);"+"\n"); 
				sb.append("\r\t\t\t"+"field_"+i_paramStrList+".set(object,"+mockFieldName+")"+";"+"\n"); 
			}

			//Replay
			//メソッドは引数がある場合もあるしない場合もあるので修正する
			//また、引数がある場合はユーザに修正を促すようにする
			//ex) object.methodSigNature_str() , object.methodSigNature_str(1)
			//引数は参照型だとnull,プリミティブ型だと0にするようにする
			sb.append("\r\t\t\t"+"//シーケンス図の呼び出し"+"\n");
			sb.append("\r\t\t\t"+"object."+disassemblyMethod+";\n");
			sb.append("\t\n");

			//record
			sb.append("\r\t\t\t"+"//シーケンス図のメッセージ呼び出し系列"+"\n");
			/*
			 * MyVerificationsInOrderとMyExpectationの切り替え
			 * if : シーケンス図の順番を厳守する
			 * else : シーケンス図の順番を厳守しない
			 */
			boolean isProtected = soow.isProtected(methodSigNature_str);
			if (isProtected) {
				sb.append("\r\t\t\t"+ "MyVerificationsInOrder test =" + " new MyVerificationsInOrder() {"+"\n");
			}else{
				sb.append("\r\t\t\t"+ "MyVerifications test ="+"new MyVerifications() {"+"\n");
			}

			sb.append("\r\t\t\t\t"+"{"+"\n");
			sb.append(mockMethodMap.get(methodSigNature_str));
			sb.append("\r\t\t\t\t"+"setSuccess(true);"+"\n");
			sb.append("\r\t\t\t\t"+"}"+"\n");
			sb.append("\r\t\t\t"+"};"+"\n");

			sb.append("\r\t\t\n");

			//テスト成功メッセージ
			sb.append("\r\t\t"+"if (test.isSuccess()) {"+"\n");
			sb.append("\r\t\t\t"+"System.out.println(\"++++++" +
					" "+className+" :: " + methodSigNature_str +
					" のテストに成功しました\");"+"\n");
			if (isProtected) {
				sb.append("\r\t\t\t"+"System.out.println(\"---" +
						"シーケンス図の振る舞い系列の以下が守られています!!"+"\\n" +
						"・回数 ○"+ "\\n" +
						"・順番 ○"+ "\\n" +
						"\");"+"\n");
			}else{
				sb.append("\r\t\t\t"+"System.out.println(\"---" +
						"シーケンス図の振る舞い系列の以下が守られています!!"+"\\n" +
						"・回数 ○"+ "\\n" +
						"・順番 ×"+ "\\n" +
						"\");"+"\n");
			}
			sb.append("\r\t\t"+"}else{"+"\n");
			//テスト失敗メッセージ
			sb.append("\r\t\t\t"+"System.out.println(\"++++++" +
					" "+className+" :: " + methodSigNature_str +
					" のテストに失敗しました\");"+"\n");
			/*
			if (isProtected == false) {
			}else{
			}
			*/
			sb.append("\r\t\t"+"}"+"\n");

			sb.append("\r\t\t\n");

			//throw-error catch
			sb.append("\r\t\t"+"} catch (NoSuchFieldException __error) {"+"\n");
			sb.append("\r\t\t"+"__error.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"} catch (SecurityException __error) {"+"\n");
			sb.append("\r\t\t"+"__error.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"} catch (IllegalArgumentException __error) {"+"\n");
			sb.append("\r\t\t"+"__error.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"} catch (IllegalAccessException __error) {"+"\n");
			sb.append("\r\t\t"+"__error.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"}"+"\n");

			sb.append("\r\t\n");
			sb.append("\r\t}\n");
		}
	}

	//FIXME
	//コンパイルしたソースコードのクラスをimportする
	//ex.) import hoge.Hoge;
	//NEXT
	//実行するスクリプトファイルにはクラスパスを指定する -cp classes:<jar指定がある場合>
	private void makeHeader(StringBuilder sb) {
		Map<String, String> javaFileMap = new HashMap<String, String>();
		Pattern pattern = Pattern.compile("(.+)\\..+$"); 
		Matcher matcher = null;

		for (FileNode fileNode : javaFileNodeList) {
			//ファイル名をインポートするクラス名とする
			String className_str = fileNode.getFileNameRemovedFormat();
			String packageFullPath_str = fileNode.getPath();
			String packagePath_str = null;
			String importPackage_str = null;

			packagePath_str = packageFullPath_str.replaceAll(baseDir.getPath(), "");

			try{
				if (className_str == null || packagePath_str.isEmpty()) {
					continue;
				}
				// /main/hoge/Hoge.java -> /main/hoge/Hoge
				matcher = pattern.matcher(packagePath_str);
				if (matcher.find()) {
					packagePath_str = matcher.group(1);
				}else{
					//置換に失敗
					continue;
				}
				// /main/hoge/Hoge -> main/hoge/Hoge
				packagePath_str = packagePath_str.substring(1, packagePath_str.length());

				// main/hoge/Hoge -> main.hoge.Hoge
				importPackage_str = packagePath_str.replaceAll("/", ".");

				//import Hoge;の場合はインポートしない
				if (importPackage_str.contains(".") == false) {
					continue;
				}

				javaFileMap.put(className_str, importPackage_str);
			} catch (NullPointerException e) {
				e.printStackTrace();
				continue;
			}

		}

		for (String className_str : javaFileMap.keySet()) {
			String importPackage_str = javaFileMap.get(className_str);
			sb.append("import "+importPackage_str+";\n");
		}

		sb.append("\n");

		sb.append("import mockit.Mocked;\n" +
				"\n"+
				"import org.junit.Test;\n\n"+
				"import java.lang.reflect.Field;\n\n");

	}

	private void makeClassName(StringBuilder sb, String className) {
		sb.append("public class " + className + "Test" + " {" +"\n");
	}
}
