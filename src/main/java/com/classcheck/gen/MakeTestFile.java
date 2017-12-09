package com.classcheck.gen;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.ConstructorPanel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class MakeTestFile {
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

	public MakeTestFile(Map<MyClass, CodeVisitor> tableMap,
			Map<MyClass, Map<String, String>> methodChangeMap,
			Map<MyClass, Map<String, String>> fieldChangeMap,
			Map<CodeVisitor, String> generatedCodesMap,
			List<ConstructorPanel> cPaneList,
			Collection<CodeVisitor> codeCollection) {
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

	public void make(){
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

				makeClassName(sb,codeVisitor.getClassName());

				for(int i=0;i<cPanelList.size();i++){
					if(cPanelList.get(i).getCodeVisitor().equals(codeVisitor)){
						makeMethod(sb,
								codeVisitor.getClassName(),
								variableFieldNameMap,
								mockMethodMap,
								mockParamsList,
								cPanelList.get(i));
					}
				}

				fileMap.put(fileName, sb.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
	}

	private void makeMethod(StringBuilder sb,
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

			//record
			sb.append("\r\t\t\t"+"//シーケンス図のメッセージ呼び出し系列"+"\n");
			sb.append("\r\t\t\t"+"new StrictExpectations() {"+"\n");
			sb.append("\r\t\t\t\t"+"{"+"\n");
			sb.append(mockMethodMap.get(methodSigNature_str));
			sb.append("\r\t\t\t\t"+"}"+"\n");
			sb.append("\r\t\t\t"+"};"+"\n");

			sb.append("\r\t\n");

			//Replay
			//メソッドは引数がある場合もあるしない場合もあるので修正する
			//また、引数がある場合はユーザに修正を促すようにする
			//ex) object.methodSigNature_str() , object.methodSigNature_str(1)
			//引数は参照型だとnull,プリミティブ型だと0にするようにする
			sb.append("\r\t\t\t"+"//シーケンス図の呼び出し"+"\n");
			sb.append("\r\t\t\t"+"object."+disassemblyMethod+";\n");
			sb.append("\t\n");

			//throw-error catch
			sb.append("\r\t\t"+"} catch (NoSuchFieldException e) {"+"\n");
			sb.append("\r\t\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"} catch (SecurityException e) {"+"\n");
			sb.append("\r\t\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"} catch (IllegalArgumentException e) {"+"\n");
			sb.append("\r\t\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"} catch (IllegalAccessException e) {"+"\n");
			sb.append("\r\t\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t\t"+"}"+"\n");

			sb.append("\r\t\n");
			sb.append("\r\t}\n");
			sb.append("\r}\n");
		}
	}

	private void makeClassName(StringBuilder sb, String className) {
		sb.append("public class " + className + "Test" + " {" +"\n");
	}

	private void makeHeader(StringBuilder sb) {
		sb.append("import mockit.StrictExpectations;\n" +
				"import mockit.Mocked;\n" +
				"\n"+
				"import org.junit.Test;\n\n"+
				"import java.lang.reflect.Field;\n\n");
	}

}
