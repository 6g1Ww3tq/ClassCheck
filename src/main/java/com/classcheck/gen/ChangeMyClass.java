package com.classcheck.gen;

import java.awt.Component;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;

import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.window.DebugMessageWindow;

public class ChangeMyClass {

	private Map<MyClass, List<JPanel>> mapPanelList;
	private DefaultTableModel tableModel;
	private Map<MyClass, CodeVisitor> codeMap;

	public ChangeMyClass(Map<MyClass, List<JPanel>> mapPanelList,Map<MyClass, CodeVisitor> codeMap, DefaultTableModel tableModel) {
		this.mapPanelList = mapPanelList;
		this.codeMap = codeMap;
		this.tableModel = tableModel;
	}

	public void change() {
		List<JPanel> panelList;
		Component component;
		JLabel astahSigLabel = null;
		JComboBox<String> codeSigBox = null;
		Pattern pattern = null;
		Matcher matcher = null;
		ClassReplace cr = null;
		MethodReplace mr = null;

		DebugMessageWindow.clearText();
		for (MyClass myClass : mapPanelList.keySet()) {
			panelList = mapPanelList.get(myClass);

			cr = new ClassReplace(myClass.toString());
			cr.setBefore(myClass.getName());
			cr.setAfter(codeMap.get(myClass).getClassName());
			cr.replace();
			mr = new MethodReplace(cr.getBase());

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

			System.out.println(mr.getBase());
		}

		DebugMessageWindow.msgToOutPutTextArea();
	}
}
