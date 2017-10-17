package com.classcheck.panel;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.classcheck.autosource.ClassBuilder;

public class FieldCompPanel extends JPanel{
	
	private ClassBuilder cb;
	StatusBar fcpSourceStatus;

	public FieldCompPanel(ClassBuilder cb) {
		fcpSourceStatus = null;
		setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		this.cb = cb;

		JPanel samplePanel = new JPanel();
		samplePanel.add(new JLabel("hello"));
		
		add(samplePanel);
		setVisible(true);
	}

	/**
	 * パネルからもステータスのテキストを変更可能にする
	 * @param text
	 */
	public void setStatusText(String text){
		fcpSourceStatus.setText(text);
	}

	public void setStatus(StatusBar fcpSourceStatus) {
		this.fcpSourceStatus = fcpSourceStatus;
	}
}