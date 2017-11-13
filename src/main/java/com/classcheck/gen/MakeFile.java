package com.classcheck.gen;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.panel.ConstructorPanel;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.Parameter;

public class MakeFile {

	private Map<CodeVisitor, String> generatedCodesMap;
	private Map<String,String> fileMap;
	private List<ConstructorPanel> cPanelList;
	private Collection<CodeVisitor> codeCollection;

	public MakeFile(Map<CodeVisitor, String> generatedCodesMap, List<ConstructorPanel> cPaneList, Collection<CodeVisitor> codeCollection) {
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
				skeVisitor = new TestSkeltonCodeVisitor(codeCollection);
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
				// TODO 自動生成された catch ブロック
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

		while(buttons.hasMoreElements()){
			button = buttons.nextElement();

			if(button.isSelected()){
				selectedButton = button;
				break;
			}
		}


		for(String methodName : mockMethodMap.keySet()){
			sb.append("\n");
			sb.append("\r\t"+"@Test"+"\n");
			//パラメータに@Mockedを使うかどうか
			sb.append("\r\t"+"public " + "void " + methodName +"(");

			for(int i=0;i < mockParamsList.size();i++){
				sb.append(mockParamsList.get(i));

				if(i<mockParamsList.size()-1){
					sb.append(",");
				}
			}

			sb.append(")"+ " {" +"\n");

			//init
			sb.append("\r\t"+"//init"+"\n");
			//そのクラスのコンストラクタを書く(コンストラクタの隣にラジオボタンを作る?)
			//ただし@Mockedのパラメータを入れるか
			//プリミティブだけを入れるのか
			//どうかを考える
			if (selectedButton != null) {
				sb.append("\r\t"+ className +" object " + "=" +" "+"new ");
				
				if (abstructBtnMap.get(selectedButton) != null) {
					//定義したコンストラクタ
					sb.append(abstructBtnMap.get(selectedButton) +";\n");
				}else{
					//デフォルトコンストラクタ
					sb.append(className + "()"+";\n");
				}
			}

			//record
			sb.append("\r\t"+"//record"+"\n");
			sb.append("\r\t"+"new StrictExpectations() {"+"\n");
			sb.append("\r\t\t"+"{"+"\n");
			sb.append(mockMethodMap.get(methodName));
			sb.append("\r\t\t"+"}"+"\n");
			sb.append("\r\t"+"};"+"\n");

			//Replay
			sb.append("\r\t"+"//replay"+"\n");
			sb.append("\r\t"+"object."+methodName+"()"+";\n");

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
				"import org.junit.Test;\n\n");
	}

}
