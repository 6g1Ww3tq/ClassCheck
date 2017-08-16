package com.classcheck.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.util.Version;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.classcheck.analyzer.source.SourceAnalyzer;
import com.classcheck.autosouce.ClassBuilder;
import com.classcheck.autosouce.Config;
import com.classcheck.autosouce.ConfigView;
import com.classcheck.autosouce.Method;
import com.classcheck.autosouce.MyClass;
import com.classcheck.autosouce.SourceGenerator;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.Tree;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResultTabView extends JPanel implements IPluginExtraTabView, ProjectEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel bottonPane;
	private JButton classChBtn;
	private JButton folderBtn;
	private JButton sequenceBtn;
	private JScrollPane textPane;
	private JScrollPane textPane_2;
	private JTextArea textArea;
	private JTextArea textArea_2;

	List<IClass> classList;
	List<ISequenceDiagram> diagramList;

	ProjectAccessor projectAccessor;
	private JButton configBtn;

	AstahAPI api;

	private static Config config;

	public ResultTabView() {
		initComponents();
		addProjectEventListener();
		classList = new ArrayList<IClass>();
		diagramList = new ArrayList<ISequenceDiagram>();

		config = new Config();
		config.activate();

	}


	private void initComponents() {
		setLayout(new BorderLayout());

		bottonPane = new JPanel();
		classChBtn = new JButton("class");
		folderBtn = new JButton("folder..");
		bottonPane.add(classChBtn);
		bottonPane.add(folderBtn);

		sequenceBtn = new JButton("seqDiagram");
		bottonPane.add(sequenceBtn);

		configBtn = new JButton("config");
		bottonPane.add(configBtn);

		textArea = new JTextArea(40,20);
		textPane = new JScrollPane(textArea);

		textArea_2 = new JTextArea(40,20);
		textPane_2 = new JScrollPane(textArea_2);

		btnEventInit();

		add(bottonPane, BorderLayout.NORTH);
		add(textPane, BorderLayout.CENTER);
		add(textPane_2, BorderLayout.EAST);
		setVisible(true);
	}

	private void btnEventInit() {
		classChBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					StringBuilder sb = new StringBuilder();
					ClassBuilder cb = new SourceGenerator().run(classList, diagramList);
					MyClass myClass = null;
					Reader reader = null;
					Document doc = null;

					/*
					for(int i=0;i<cb.getclasslistsize();i++){
						doc = new Document();
						myClass = cb.getClass(i);
						reader = new StringReader(myClass.toString());
					}
					 */

					LevensteinDistance levensteinAlgorithm = new LevensteinDistance();

					sb.append("結果①:" + levensteinAlgorithm.getDistance("resolution", "revolution")+"\n");
					sb.append("結果②:" + levensteinAlgorithm.getDistance("take", "sake")+"\n");
					sb.append("結果③:" + levensteinAlgorithm.getDistance("teacher", "teach")+"\n");
					sb.append("結果④:" + levensteinAlgorithm.getDistance("let it go", "let's and go")+"\n");
					sb.append("結果⑤:" + levensteinAlgorithm.getDistance("mountaingorilla", "fish")+"\n");

					TextMessageWindow tmw = new TextMessageWindow();
					tmw.setTextArea(sb.toString());
					tmw.setTitle("レーベンシュタイン距離");

				} catch (UnExpectedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}

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
				TextMessageWindow tmw = null;

				config.activate();
				create_class_sequence_list();

				try {
					ClassBuilder cb = new SourceGenerator().run(classList, diagramList);
					MyClass myClass = null;
					List<Method> methodList = null;
					ISequenceDiagram iSequence = null;
					ILifeline[] iLifelines = null;
					IOperation iOperation = null;
					Map<Method, MyClass> checkTargetMethodList = new HashMap<>();
					ArrayList<IDiagram> empTargetList = new ArrayList<>();
					boolean existTarget=false;

					for(int i=0;i<cb.getclasslistsize();i++){
						myClass = cb.getClass(i);
						sb.append(myClass.toString());
					}

				} catch (UnExpectedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}

				textArea.setText(sb.toString());
				tmw = new TextMessageWindow();
				tmw.setTextArea(sb.toString());
				tmw.setTitle("シーケンス図を読み取り");
			}
		});

		configBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//ConfigJDialog configDialog = new ConfigJDialog(true);
				try {
					ConfigView cv = new ConfigView(config,api.getViewManager().getMainFrame());
					cv.setVisible(true);
				} catch (InvalidUsingException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
			}
		});

	}

	private void create_class_sequence_list() {
		classList = SourceGenerator.getClassList();
		diagramList = SourceGenerator.getSequenceDiagramList();
	}


	private void addProjectEventListener() {
		try {
			api = AstahAPI.getAstahAPI();
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
		TextMessageWindow tmw = null;

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rtnVal = chooser.showOpenDialog(comp);
		if(rtnVal == JFileChooser.APPROVE_OPTION) {
			Tree tree = new Tree(new FileNode(chooser.getSelectedFile()) , ".java$");
			StringBuilder sb = new StringBuilder();
			Iterator<FileNode> it = tree.iterator();

			while (it.hasNext()) {
				fileNode = (FileNode) it.next();

				if (fileNode!=null) {
					try {
						sa = new SourceAnalyzer(fileNode);

						sa.doAnalyze();

						sb.append(fileNode+"\n");
						sb.append(sa.getMessage());
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
				}

			}

			textArea_2.setText(sb.toString());
			tmw = new TextMessageWindow();
			tmw.setTextArea(sb.toString());
			tmw.setTitle("コード読み取り");
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

	@Override
	public void activated() {
	}

	@Override
	public void deactivated() {
	}


	public static void setConfig(Config c) {
		config = c;
	}
}