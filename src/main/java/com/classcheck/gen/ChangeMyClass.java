package com.classcheck.gen;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.panel.AstahAndSourcePanel;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;

public class ChangeMyClass {

	private Map<MyClass, List<JPanel>> mapPanelList;
	private DefaultTableModel tableModel;
	private Map<MyClass, CodeVisitor> codeMap;
	private AstahAndSourcePanel astahAndSourcePane;

	public ChangeMyClass(AstahAndSourcePanel astahAndSourcePane,
			DefaultTableModel tableModel) {
		this.astahAndSourcePane = astahAndSourcePane;
		this.mapPanelList = astahAndSourcePane.getMapPanelList();
		this.codeMap = astahAndSourcePane.getCodeMap();
		this.tableModel = tableModel;
	}

	public void change() {
		List<JPanel> panelList = null;
		Component component;
		JLabel astahSigLabel = null;
		JComboBox<String> codeSigBox = null;
		ClassReplace cr = null;
		MethodReplace mr = null;

		CompilationUnit cu = null;
		List<BodyDeclaration> members = null;
		StringBuilder sb = null;

		DebugMessageWindow.clearText();

		for (MyClass myClass : mapPanelList.keySet()) {
			panelList = mapPanelList.get(myClass);

			mr = new MethodReplace(myClass.toString());

			//メソッド名の変更
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
						mr.setBefore(astahSigLabel.getText());
						mr.setAfter(codeSigBox.getSelectedItem().toString());
						mr.replace();
					}
				}
			}

			//クラス名変更
			cr = new ClassReplace(mr.getBase());
			cr.setBefore(myClass.getClassSig());
			//System.out.println("before : "+myClass.getClassSig());
			cr.setAfter(codeMap.get(myClass).getClassSig());
			//System.out.println("after : " + codeMap.get(myClass).getClassSig());
			cr.replace();
			//クラス名とメソッド名の変更
			//System.out.println(cr.getBase());


			if (cr != null) {
				try {
					sb = new StringBuilder(cr.getBase());
					//フィールドのクラス名を変更
					for(MyClass mc : codeMap.keySet()){
						cu = JavaParser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
						cu.accept(new FieldReplaceVisitor(mc.getName(),codeMap.get(mc).getClassName()), null);

						//編集したクラスを表示
						//System.out.println(cu.toString());
						//内容を更新
						sb = new StringBuilder(cu.toString());
					}
					//編集したクラスを表示
					System.out.println(sb.toString());
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
