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

		for(CodeVisitor codeVisitor : generatedCodesMap.keySet()){
			fileName = codeVisitor.getClassName() + "Test" + ".java";
			sb = new StringBuilder();
			makeHeader(sb);
			skeltonCode = generatedCodesMap.get(codeVisitor);

			try {
				cu = JavaParser.parse(new ByteArrayInputStream(skeltonCode.getBytes()));
				//引数にすべてのcoddevisitorのSetを入れる
				skeVisitor = new TestSkeltonCodeVisitor(codeVisitor,tableMap,fieldChangeMap,methodChangeMap,codeCollection);
				cu.accept(skeVisitor, null);

				mockParamsList = skeVisitor.getMockFieldList();
				mockMethodMap = skeVisitor.getMockMethodMap();

				makeClassName(sb,codeVisitor.getClassName());

				for(int i=0;i<cPanelList.size();i++){
					if(cPanelList.get(i).getCodeVisitor().equals(codeVisitor)){
						makeMethod(sb,
								codeVisitor.getClassName(),
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


		for(String methodName : mockMethodMap.keySet()){
			mockParamStrList = new ArrayList<String>();
			sb.append("\n");
			sb.append("\r\t"+"@Test"+"\n");
			//パラメータに@Mockedを使うかどうか
			sb.append("\r\t"+"public " + "void " + methodName +"(");

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
			sb.append("\r\t"+"try {"+"\n");
			
			//init
			sb.append("\r\t"+"/*==================初期化（コンストラクタ）=================="+"\n");
			sb.append("\r\t"+"*\t\t\t\t\t\t\t\t\t\t\t\t*"+"\n");
			sb.append("\r\t"+"*\t\t\t※コンストラクタを編集してください\t\t\t*"+"\n");
			sb.append("\r\t"+"*\t\t\t\t\t\t\t\t\t\t\t\t*/"+"\n");
			//そのクラスのコンストラクタを書く(コンストラクタの隣にラジオボタンを作る?)
			//ただし@Mockedのパラメータを入れるか
			//プリミティブだけを入れるのか
			//どうかを考える
			//FIXME
			//オブジェクトのコンストラクタは参照型だとnull,プリミティブ型だと0にするようにする
			sb.append("\n");
			if (selectedButton != null) {
				sb.append("\r\t\t"+ className +" object " + "=" +" "+"new "); //objectはテストするクラスに対してのオブジェクト
				
				if (abstructBtnMap.get(selectedButton) != null) {
					//定義したコンストラクタ
					constructorStr = abstructBtnMap.get(selectedButton);
					sb.append(constructorStr +";");
				}else{
					//デフォルトコンストラクタ
					sb.append(className + "()"+";");
				}
				
				sb.append("        ");
				sb.append("// <=== コンストラクタを編集してください");
				sb.append("\n");
			}
			sb.append("\n");
			sb.append("\r\t"+"//=========================================================="+"\n");

			sb.append("\n");
			
			//reflectionを用いてフィールド(privateでも)にモックオブジェクトをセットする
			sb.append("\r\t\t"+"//フィールドにセットする"+"\n"); 
			sb.append("\r\t\t"+"Class clazz"+"="+"object.getClass();"+"\n"); //objectはテストするクラスに対してのオブジェクト
			for(int i_paramStrList = 0 ;i_paramStrList<mockParamStrList.size();i_paramStrList++){
				String mockFieldName = mockParamStrList.get(i_paramStrList);
				sb.append("\r\t\t"+"Field field_"+i_paramStrList+ " = " +"clazz.getDeclaredField("+"\""+mockFieldName+"\""+");"+"\n"); 
				sb.append("\r\t\t"+"field_"+i_paramStrList+".setAccessible(true);"+"\n"); 
				sb.append("\r\t\t"+"field_"+i_paramStrList+".set(object,"+mockFieldName+")"+";"+"\n"); 
			}

			//record
			sb.append("\r\t\t"+"//シーケンス図のメッセージ呼び出し系列"+"\n");
			sb.append("\r\t\t"+"new StrictExpectations() {"+"\n");
			sb.append("\r\t\t\t"+"{"+"\n");
			sb.append(mockMethodMap.get(methodName));
			sb.append("\r\t\t\t"+"}"+"\n");
			sb.append("\r\t\t"+"};"+"\n");

			sb.append("\n");

			//Replay
			//FIXME
			//メソッドは引数がある場合もあるしない場合もあるので修正する
			//また、引数がある場合はユーザに修正を促すようにする
			//ex) object.methodName() , object.methodName(1)
			//引数は参照型だとnull,プリミティブ型だと0にするようにする
			sb.append("\r\t\t"+"//シーケンス図の呼び出し"+"\n");
			sb.append("\r\t\t"+"object."+methodName+"()"+";\n");
			sb.append("\n");

			//throw-error catch
			sb.append("\r\t"+"} catch (NoSuchFieldException e) {"+"\n");
			sb.append("\r\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t"+"} catch (SecurityException e) {"+"\n");
			sb.append("\r\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t"+"} catch (IllegalArgumentException e) {"+"\n");
			sb.append("\r\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t"+"} catch (IllegalAccessException e) {"+"\n");
			sb.append("\r\t"+"e.printStackTrace();"+"\n");
			sb.append("\r\t"+"}"+"\n");
			
			sb.append("\n");
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
