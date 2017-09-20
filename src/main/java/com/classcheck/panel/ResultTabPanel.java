package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.analyzer.source.SourceAnalyzer;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Config;
import com.classcheck.autosource.ConfigView;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.SourceGenerator;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.FileTree;
import com.classcheck.window.ConfigJDialog;
import com.classcheck.window.MatcherWindow;
import com.classcheck.window.DebugMessageWindow;
import com.classcheck.window.TextMessageWindow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResultTabPanel extends JPanel implements IPluginExtraTabView, ProjectEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel bottonPane;
	private JButton expBtn;
	private JButton folderBtn;
	private JButton sequenceBtn;

	private List<CodeVisitor> codeVisitorList;
	FileTree baseDirTree;

	List<IClass> classList;
	List<ISequenceDiagram> diagramList;

	ProjectAccessor projectAccessor;
	private JButton configBtn;

	AstahAPI api;

	private ClassBuilder cb;
	private static Config config;

	ByteArrayOutputStream baos;
	public ResultTabPanel() {
		initComponents();
		addProjectEventListener();
		initVariables();
		initDebugWindow();
	}

	private void initDebugWindow(){
		new DebugMessageWindow("Debug",true);
	}

	private void initVariables() {
		classList = new ArrayList<IClass>();
		diagramList = new ArrayList<ISequenceDiagram>();

		config = new Config();
		config.activate();

		codeVisitorList = new ArrayList<CodeVisitor>();
	}


	private void initComponents() {
		setLayout(new BorderLayout());

		bottonPane = new JPanel();
		expBtn = new JButton("実験");
		folderBtn = new JButton("folder..");
		bottonPane.add(expBtn);
		bottonPane.add(folderBtn);

		sequenceBtn = new JButton("seqDiagram");
		bottonPane.add(sequenceBtn);

		configBtn = new JButton("seqConfig");
		bottonPane.add(configBtn);
		btnEventInit();

		add(bottonPane, BorderLayout.NORTH);
		setVisible(true);
	}

	private void btnEventInit() {
		expBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DebugMessageWindow.clearText();
				//				new ConfigJDialog(false);
				System.out.println("実験しました。。。");
				DebugMessageWindow.msgToOutPutTextArea();
			}
		});

		folderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//SampleMethodVisitor.setClassBuilder(cb);
				selectFolder(getComponent());
			}

		});

		sequenceBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				MatcherWindow ctw = null;

				config.activate();
				create_class_sequence_list();

				try {
					cb = new SourceGenerator().run(classList, diagramList);
					MyClass myClass = null;

					for(int i=0;i<cb.getclasslistsize();i++){
						myClass = cb.getClass(i);
						sb.append(myClass.toString());
					}

					if (baseDirTree!=null) {
						ctw = new MatcherWindow(cb,codeVisitorList,baseDirTree);
						ctw.setTitle("テストプログラムの生成");
					}
				} catch (UnExpectedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}

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
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future;
		final JFileChooser chooser = new JFileChooser();

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rtnVal = chooser.showOpenDialog(comp);
		if(rtnVal == JFileChooser.APPROVE_OPTION) {
			future = executor.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					SourceAnalyzer sa = null; 
					FileNode fileNode = null;
					baseDirTree = new FileTree(new FileNode(chooser.getSelectedFile()) , ".java$");
					StringBuilder sb = new StringBuilder();
					Iterator<FileNode> it = baseDirTree.iterator();

					while (it.hasNext()) {
						fileNode = (FileNode) it.next();

						if (fileNode!=null) {
							try {
								sa = new SourceAnalyzer(fileNode);

								sa.doAnalyze();

								sb.append(fileNode+"\n");
								codeVisitorList.add(sa.getCodeVisitor());

							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
						}

					}

					System.out.println(sb.toString());

					return null;
				}
			});
			try {
				future.get(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("time out!!");
				future.cancel(true);
				baseDirTree = null;
				e.printStackTrace();
			}
			executor.shutdownNow();
			DebugMessageWindow.msgToOutPutTextArea();
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