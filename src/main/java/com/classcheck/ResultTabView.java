package com.classcheck;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.ILifelineLink;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.classcheck.analyzer.source.SourceAnalyzer;
import com.classcheck.autosouce.ProcessBuilder;
import com.classcheck.autosouce.SourceGenerator;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultTabView extends JPanel implements IPluginExtraTabView, ProjectEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel bottonPane;
	private JButton startBtn;
	private JButton folderBtn;
	private JButton sequenceBtn;
	private JScrollPane textPane;
	private JTextArea textArea;

	List<IClass> classList;
	List<ISequenceDiagram> diagramList;

	ProjectAccessor projectAccessor;

	public ResultTabView() {
		initComponents();
		addProjectEventListener();
		classList = new ArrayList<IClass>();
		diagramList = new ArrayList<ISequenceDiagram>();
	}


	private void initComponents() {
		setLayout(new BorderLayout());

		bottonPane = new JPanel();
		startBtn = new JButton("start");
		folderBtn = new JButton("folder");
		bottonPane.add(startBtn);
		bottonPane.add(folderBtn);

		sequenceBtn = new JButton("seqDiagram");
		bottonPane.add(sequenceBtn);

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

		sequenceBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();

				create_class_sequence_list();

				try {
					List<ProcessBuilder> operationList = new SourceGenerator().run(classList, diagramList);
					IOperation operation;
					ILifeline lifeLine;

					for (ProcessBuilder processBuilder : operationList) {

						/*
						sb.append(processBuilder.getIClass()+"\n");
						sb.append(processBuilder.getCallOperation()+"\n");
						sb.append(processBuilder.getProcess()+"\n");
						 */

						lifeLine = processBuilder.getBaselifeline();
						sb.append(lifeLine + ":");

						operation = processBuilder.getCallOperation();
						sb.append(operation.getTypeModifier()+" ");
						sb.append(operation.getReturnTypeExpression()+" ");
						sb.append(operation.getName()+"(");
						for (IParameter param : operation.getParameters()) {
							sb.append(param.getType()+" ");
							sb.append(param.getName());
							sb.append(",");
						}
						sb.append(")\n");
					}
				} catch (UnExpectedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}

				textArea.setText(sb.toString());
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


	private void create_class_sequence_list() {
		classList = SourceGenerator.getClassList();
		diagramList = SourceGenerator.getSequenceDiagramList();
	}


	private void addProjectEventListener() {
		try {
			AstahAPI api = AstahAPI.getAstahAPI();
			projectAccessor = api.getProjectAccessor();
			projectAccessor.addProjectEventListener(this);
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
	}

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

	private void printDB_ILifeLines(StringBuilder sb,ILifeline iLifeline) {
		sb.append("*****Print ILifeLines*****\n");
		sb.append("----Base----\n");
		sb.append(iLifeline.getBase()+"\n");

		sb.append("----Fragments----\n");
		for (INamedElement iname : iLifeline.getFragments()) {
			sb.append(iname+"\n");
		}

		sb.append("----LifeLineLinks----\n");
		for (ILifelineLink link : iLifeline.getLifelineLinks()) {
			sb.append(iLifeline+"\n");
		}

		sb.append("**********\n");
	}


	private void printDB_IMessage(StringBuilder sb,IMessage iMessage) {
		IOperation operation = iMessage.getOperation();
		/*
		sb.append("*****Print IMessage*****\n");
		sb.append("----Activator----\n");
		sb.append(iMessage.getActivator()+"\n");
		 */

		sb.append("******Operation******\n");
		sb.append(operation.getReturnType()+" ");
		sb.append(operation+" (");
		for (IParameter param : operation.getParameters()) {
			sb.append(param.getType() + " " + param.getName() +",");
		}
		sb.append(")\n");

		sb.append("**********\n");
	}


	private void printDB_INameElement(StringBuilder sb,INamedElement iNamedElement) {
		sb.append("*****Print INameElement*****\n");
		sb.append("----Name----\n");
		sb.append(iNamedElement.getName()+"\n");
		sb.append("----Definition----\n");
		sb.append(iNamedElement.getDefinition()+"\n");
		sb.append("----Type----\n");
		sb.append(iNamedElement.getTypeModifier()+"\n");
		sb.append("----Dependencies----\n");
		for (IDependency iDependency : iNamedElement.getClientDependencies()) {
			sb.append("--client--\n");
			sb.append(iDependency.getClient()+"\n");
			sb.append("--Supplier--\n");
			sb.append(iDependency.getSupplier()+"\n");
		}
		sb.append("**********\n");
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

	@Override
	public void activated() {
	}

	@Override
	public void deactivated() {
	}
}