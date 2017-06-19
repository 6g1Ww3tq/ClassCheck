package com.classcheck;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CompileTabView extends JPanel implements IPluginExtraTabView, ProjectEventListener {

	private static final long serialVersionUID = 1L;
	private JPanel bottonPane;
	private JButton folderBtn;
	private JButton javacBtn;
	private JButton runBtn;
	private Label maintextLabel;
	private TextField mainFile;
	private JScrollPane folderPane;
	private JScrollPane msgPane;
	private JTextArea folderTextArea;
	private JTextArea msgTextArea;
	private File compileFolder;

	public CompileTabView() {
		initComponents();
		addEvent();
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

		javacBtn = new JButton("javac");
		folderBtn = new JButton("folder");
		maintextLabel = new Label("mainClass :");
		mainFile = new TextField(20);
		runBtn = new JButton("run");

		bottonPane = new JPanel();
		bottonPane.add(folderBtn);
		bottonPane.add(javacBtn);
		bottonPane.add(maintextLabel);
		bottonPane.add(mainFile);
		bottonPane.add(runBtn);

		folderTextArea = new JTextArea(10,10);
		folderPane = new JScrollPane(folderTextArea);

		msgTextArea = new JTextArea(10,10);
		msgPane = new JScrollPane(msgTextArea);

		add(bottonPane, BorderLayout.NORTH);
		add(folderPane, BorderLayout.WEST);
		add(msgPane,BorderLayout.CENTER);
		setVisible(true);
	}

	private void addEvent() {
		folderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectFolder(getComponent());
			}

		});

		javacBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> command = new ArrayList<String>();
				ProcessBuilder pb = new ProcessBuilder();
				BufferedReader br = null;
				StringBuilder sb;

				command.add("javac");
				command.add(compileFolder.toString()+"/"+mainFile.getText());
				command.add("-d");
				command.add(compileFolder.toString());
				pb.command(command);
				try {
					Process process = pb.start();

					process.waitFor();

					br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					sb = new StringBuilder();

					for(String line = br.readLine(); line != null ; line = br.readLine()){
						sb.append(line+"\n");
					}

					msgTextArea.setText(sb.toString());

					if (br!=null) {
						br.close();
					}
				} catch (IOException e1) {
					msgTextArea.setText(e1.getMessage());
				} catch (InterruptedException e1) {
					msgTextArea.setText(e1.getMessage());
				}
			}

		});

		runBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> command = new ArrayList<String>();
				ProcessBuilder pb = new ProcessBuilder();
				String str = mainFile.getText();
				BufferedReader br = null;
				StringBuilder sb;

				command.add("java");
				command.add("-cp");
				command.add(compileFolder.toString()+"/");
				command.add(str.substring(0, str.lastIndexOf(".java")));

				pb.command(command);
				try {
					Process process = pb.start();

					process.waitFor();

					br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					sb = new StringBuilder();

					for(String line = br.readLine(); line != null ; line = br.readLine()){
						sb.append(line+"\n");
					}

					msgTextArea.setText(sb.toString());
					if (br!=null) {
						br.close();
					}
				} catch (IOException e1) {
					msgTextArea.setText(e1.getMessage());
				} catch (InterruptedException e1) {
					msgTextArea.setText(e1.getMessage());
				}
			}

		});
	}


	/**
	 * @param comp
	 */
	private void selectFolder(Component comp) {
		JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(comp);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			compileFolder = chooser.getSelectedFile();
			folderTextArea.setText(compileFolder.toString());
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
		return "Show CompileTabView here";
	}

	@Override
	public String getTitle() {
		return "CompileTabView";
	}

	public void activated() {
	}

	public void deactivated() {
	}
}