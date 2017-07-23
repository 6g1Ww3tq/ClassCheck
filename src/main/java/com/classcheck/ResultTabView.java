package com.classcheck;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.classcheck.analyzer.source.SourceAnalyzer;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class ResultTabView extends JPanel implements IPluginExtraTabView, ProjectEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel bottonPane;
	private JButton startBtn;
	private JButton folderBtn;
	private JButton exeBtn;
	private TextField textField;
	private JScrollPane textPane;
	private JTextArea textArea;

	public ResultTabView() {
		initComponents();
		addProjectEventListener();
	}


	private void addProjectEventListener() {
		try {
			AstahAPI api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			projectAccessor.addProjectEventListener(this);
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		bottonPane = new JPanel();
		startBtn = new JButton("start");
		folderBtn = new JButton("folder");
		bottonPane.add(startBtn);
		bottonPane.add(folderBtn);

		exeBtn = new JButton("exe");
		bottonPane.add(exeBtn);
		textField = new TextField(5);
		bottonPane.add(textField);

		textArea = new JTextArea(50,20);
		textPane = new JScrollPane(textArea);

		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createLabelPane();
			}
		});

		folderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectFolder(getComponent());
			}

		});

		exeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ProcessBuilder pb = new ProcessBuilder(textField.getText());
				try {
					Process p = pb.start();

					p.waitFor();

					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					StringBuilder sb = new StringBuilder();

					for(String line = br.readLine(); line != null ; line = br.readLine()){
						sb.append(line);
					}

					textArea.setText(sb.toString());
				} catch (IOException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
			}
		});

		add(bottonPane, BorderLayout.NORTH);
		add(textPane, BorderLayout.CENTER);
		setVisible(true);
	}

	private void createLabelPane() {
		Analyze analyze;
		try {
			analyze = new Analyze(AstahAPI.getAstahAPI());
			analyze.doAnalyze();
			textArea.setText(analyze.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param comp
	 */
	private void selectFolder(Component comp) {
		SourceAnalyzer sa = null; 
		JFileChooser chooser = new JFileChooser();
		FileNode fileNode = null;

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(comp);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			Tree tree = new Tree(new FileNode(chooser.getSelectedFile()) , ".java$");
			StringBuilder sb = new StringBuilder();
			Iterator<FileNode> it = tree.iterator();

			while (it.hasNext()) {
				fileNode = (FileNode) it.next();

				if (fileNode!=null) {
					sb.append(fileNode+"\n");
					try {
						sa = new SourceAnalyzer(fileNode);
						
						sa.doAnalyze();
						sb.append("this file is : " + fileNode + "\n" + sa.getMessage());
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}

			}

			textArea.setText(sb.toString());
		}
	}

	@Override
	public void projectChanged(ProjectEvent e) {
	}

	@Override
	public void projectClosed(ProjectEvent e) {
	}

	@Override
	public void projectOpened(ProjectEvent e) {
	}

	@Override
	public void addSelectionListener(ISelectionListener listener) {
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return "Show ResultTabView here";
	}

	@Override
	public String getTitle() {
		return "ResultView";
	}

	public void activated() {
	}

	public void deactivated() {
	}
}