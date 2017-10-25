package com.classcheck.gen;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.MethodCompPanel;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;

public class ChangeMyClass {

	private Map<MyClass, List<JPanel>> mapPanelList;
	private Map<MyClass, CodeVisitor> codeMap;
	private MethodCompPanel mcp;
	private Map<MyClass, Map<String, String>> changeMap;
	private ArrayList<String> generatedCodes;

	public ChangeMyClass(MethodCompPanel mcp) {
		this.mcp = mcp;
		this.mapPanelList = mcp.getMapPanelList();
		this.codeMap = mcp.getCodeMap();
		
		this.generatedCodes = new ArrayList<String>();
	}

	public ChangeMyClass(MethodCompPanel mcp,
			Map<MyClass, Map<String, String>> changeMap) {
		this(mcp);
		this.changeMap = changeMap;
	}
	
	public ArrayList<String> getGeneratedCodes() {
		return generatedCodes;
	}

	public void change() {
		List<JPanel> panelList = null;
		Component component;
		JLabel astahSigLabel = null;
		JComboBox<String> codeSigBox = null;
		ClassNameReplace cnr = null;
		MethodSigReplace msr = null;

		SearchVarVisitor svr = null;

		CompilationUnit cu = null;
		StringBuilder sb = null;

		DebugMessageWindow.clearText();

		for (MyClass myClass : mapPanelList.keySet()) {
			panelList = mapPanelList.get(myClass);

			msr = new MethodSigReplace(myClass.toString());

			//メソッド名の変更
			for (JPanel panel : panelList) {
				for (int i = 0; i < panel.getComponentCount(); i++) {
					component = panel.getComponent(i);

					if (component instanceof JLabel) {
						astahSigLabel = (JLabel) component;
					}

					if (component instanceof JComboBox<?>) {
						codeSigBox = (JComboBox) component;
					}
				}
				//astah sig : code sig を取得完了

				if (astahSigLabel != null && codeSigBox != null) {
					if (!astahSigLabel.getText().contains("(左)スケルトンコードのメソッド")) {
						msr.setBefore(astahSigLabel.getText());
						msr.setAfter(codeSigBox.getSelectedItem().toString());
						msr.replace();
					}
				}
			}

			//クラス名変更
			cnr = new ClassNameReplace(msr.getBase());
			cnr.setBefore(myClass.getClassSig());
			//System.out.println("before : "+myClass.getClassSig());
			cnr.setAfter(codeMap.get(myClass).getClassSig());
			//System.out.println("after : " + codeMap.get(myClass).getClassSig());
			cnr.replace();
			//クラス名とメソッド名の変更
			//System.out.println(cnr.getBase());


			if (cnr != null) {
				try {
					sb = new StringBuilder(cnr.getBase());
					//TODO
					//フィールドの変数とローカル変数に対応するクラスを調べるvarsMap
					cu = JavaParser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
					svr = new SearchVarVisitor();
					cu.accept(svr,null);

					/*
					//TODO
					//ローカル変数クラスを変更する	
					cu = JavaParser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
					cu.accept(new LocalDecReplaceVisitor(codeMap,svr.getVarsMap(),svr.getReplaceBeginLines()),null);
					sb = new StringBuilder(cu.toString());

					//TODO
					//ローカル変数のメソッドの変更を行う
					cu = JavaParser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
					cu.accept(new LocalMethodReplaceVisitor(codeMap,svr.getVarsMap(),svr.getReplaceBeginLines(),changeMap),null);
					sb = new StringBuilder(cu.toString());
					*/

					//フィールドのクラス名を変更
					for(MyClass mc : codeMap.keySet()){

						//フィールドのクラスの変更
						cu = JavaParser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
						cu.accept(new FieldReplaceVisitor(mc.getName(),codeMap.get(mc).getClassName()), null);

						//内容を更新
						sb = new StringBuilder(cu.toString());
					}
					//編集したクラスを表示
					System.out.println(sb.toString());
					
					generatedCodes.add(sb.toString());
				} catch (ParseException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					System.out.println(e.toString());
				}
			}
		}

		DebugMessageWindow.msgToOutPutTextArea();

	}
}
