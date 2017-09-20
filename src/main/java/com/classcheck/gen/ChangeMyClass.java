package com.classcheck.gen;

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.classcheck.autosource.MyClass;
import com.classcheck.window.DebugMessageWindow;

public class ChangeMyClass {

	private Map<MyClass, List<JPanel>> mapPanelList;
	private DefaultTableModel tableModel;

	public ChangeMyClass(Map<MyClass, List<JPanel>> mapPanelList,DefaultTableModel tableModel) {
		this.mapPanelList = mapPanelList;
		this.tableModel = tableModel;
	}

	public void change() {
		List<JPanel> panelList;
		Component component;
		JLabel astahSigLabel = null;
		JComboBox<String> codeSigBox = null;

		for (MyClass myClass : mapPanelList.keySet()) {
			panelList = mapPanelList.get(myClass);

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
						System.out.println(astahSigLabel.getText() +
								" : " + codeSigBox.getSelectedItem());
					}
				}
			}
		}

		DebugMessageWindow.msgToOutPutTextArea();
	}
}
