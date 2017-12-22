package com.classcheck.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.classcheck.panel.SequentialOptionPanel;

public class SequentialOrderOptionWindow extends JDialog {

	private boolean opened;
	private boolean canceled;
	private String className;
	private Set<String> testMethodSet;

	List<SequentialOptionPanel> panelList;

	private JButton okButton;
	private JButton cancelButton;

	public SequentialOrderOptionWindow(String className, Set<String> testMethodSet) {
		this.opened = true;
		this.canceled = false;
		this.className = className;
		this.testMethodSet = testMethodSet;
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");

		setTitle(" 「 " + className + " 」 " + "クラス" + 
		"のテストプログラム設定");
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		initComponent();
		initEvents();

		setModal(true);

		pack();
		setVisible(true);
	}

	private void initComponent() {
		Container mainContainer = getContentPane();
		JPanel mainPanel = new JPanel(new BorderLayout());
		JScrollPane mainScrollPane = new JScrollPane(mainPanel);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 5));
		JPanel methodsPanel = null;
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("シーケンス図で定義されている振る舞い系列の順番を守るかどうか設定を行います");
		label.setFont(new Font("SansSerif", Font.BOLD, 14));
		titlePanel.add(label);

		panelList = new ArrayList<SequentialOptionPanel>();
		SequentialOptionPanel soPanel = null;

		for(String methodSigNature_str : testMethodSet){
			soPanel = new SequentialOptionPanel(className ,methodSigNature_str);
			panelList.add(soPanel);
		}

		methodsPanel = new JPanel(new GridLayout(testMethodSet.size() , 1 , 3,3));
		for (SequentialOptionPanel panel : panelList) {
			methodsPanel.add(panel);
		}
		
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		mainPanel.add(titlePanel,BorderLayout.NORTH);
		mainPanel.add(methodsPanel,BorderLayout.CENTER);
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);

		mainContainer.add(mainScrollPane);
	}
	
	private void initEvents() {
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Component c = (Component)e.getSource();
				Window w = SwingUtilities.getWindowAncestor(c);
				w.dispose();
				canceled = false;
				opened = false;
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Component c = (Component)e.getSource();
				Window w = SwingUtilities.getWindowAncestor(c);
				w.dispose();
				canceled = true;
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				opened = true;
			}

			@Override
			public void windowClosed(WindowEvent e) {
				opened = false;
				canceled = true;
				super.windowClosed(e);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				opened = false;
				canceled = true;
				super.windowClosing(e);
			}
		});
	}

	public List<SequentialOptionPanel> getPanelList() {
		return panelList;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public boolean isProtected(String targetMethodSigNature){
		boolean isProtected = true;
		
		//テストメソッドがあるパネルを探す
		for(int i_panelList = 0;i_panelList < panelList.size() ; i_panelList++){
			SequentialOptionPanel panel = panelList.get(i_panelList);
			
			//見つけた場合、シーケンス図の順番を守るかしないか判定を返却する
			if (panel.getMethodSigNature_str().equals(targetMethodSigNature)) {
				return panel.yesChecked();
			}
		}
		
		return isProtected;
	}
}