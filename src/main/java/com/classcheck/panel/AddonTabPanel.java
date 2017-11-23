package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.analyzer.source.SkeltonCodeAnalyzer;
import com.classcheck.analyzer.source.SkeltonCodeVisitor;
import com.classcheck.analyzer.source.SourceAnalyzer;
import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Config;
import com.classcheck.autosource.ConfigView;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.SourceGenerator;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.FileTree;
import com.classcheck.window.DebugMessageWindow;
import com.classcheck.window.MatcherWindow;

public class AddonTabPanel extends JPanel implements IPluginExtraTabView, ProjectEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DebugMessageWindow debugWindow;
	private JPanel genPane;
	private JCheckBox debugCheckBox;
	private JPanel folderPane;
	private JButton expBtn;
	private JButton folderBtn;
	private JButton genBtn;
	private JTextField folderTextField;

	private List<CodeVisitor> codeVisitorList;
	FileTree baseDirTree;

	List<IClass> classList;
	List<ISequenceDiagram> diagramList;

	ProjectAccessor projectAccessor;
	String projectPath;
	private JButton configBtn;

	AstahAPI api;

	private ClassBuilder cb;
	private static Config config;

	ByteArrayOutputStream baos;

	public AddonTabPanel() {
		initComponents();
		initEvents();
		addProjectEventListener();
		initVariables();
		initDebugWindow();
	}

	private void initDebugWindow(){
		debugWindow = new DebugMessageWindow("Debug",true);
	}

	private void initVariables() {
		classList = new ArrayList<IClass>();
		diagramList = new ArrayList<ISequenceDiagram>();

		config = new Config();
		config.activate();

		codeVisitorList = new ArrayList<CodeVisitor>();

		ToolTipManager.sharedInstance().setInitialDelay(250);
		ToolTipManager.sharedInstance().setReshowDelay(250);
		ToolTipManager.sharedInstance().setDismissDelay(60000);
	}


	private void initComponents() {
		JPanel actionPane = new JPanel();
		JPanel debugPane = new JPanel();
		JPanel northPane = new JPanel();

		actionPane.setLayout(new FlowLayout());
		debugCheckBox = new JCheckBox();

		northPane.setLayout(new BorderLayout(3, 3));
		setLayout(new BorderLayout());
		baseDirTree = null;

		genPane = new JPanel();

		expBtn = new JButton("実験");
		genPane.add(expBtn);

		genBtn = new JButton("生成");
		genPane.add(genBtn);

		configBtn = new JButton("設定");
		genPane.add(configBtn);

		folderBtn = new JButton("選択");
		folderPane = new JPanel();
		folderPane.setLayout(new FlowLayout(FlowLayout.CENTER,5,3));
		folderTextField = new JTextField(50);
		folderTextField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		folderTextField.setDragEnabled(true);
		folderTextField.setToolTipText("<html>"+
				"ソースコードが存在する<br>" +
				"フォルダを選択してください" +
				"</html>");
		folderPane.add(folderTextField);

		debugPane.add(new JLabel("デバックモード:"));
		debugPane.add(debugCheckBox);

		actionPane.add(debugPane);
		actionPane.add(genPane);
		northPane.add(actionPane, BorderLayout.NORTH);
		northPane.add(folderTextField, BorderLayout.CENTER);
		northPane.add(new JLabel("フォルダ : "),BorderLayout.WEST);
		northPane.add(folderBtn,BorderLayout.EAST);

		add(northPane,BorderLayout.NORTH);
		setVisible(true);
	}

	private void initEvents() {

		debugCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (debugCheckBox.isSelected()) {
					debugWindow.setVisible(true);
				}else{
					debugWindow.setVisible(false);
				}
			}
		});

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
				selectFolder(getComponent());
			}

		});

		genBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<IClass> javaPackage;
				File file;

				SourceGenerator sg = null;
				MatcherWindow ctw = null;

				//スケルトンコードのメソッド内でインスタンスを生成していないか
				//チェックを行う
				SkeltonCodeAnalyzer sca = null;
				SkeltonCodeVisitor scv = null;


				if (!MatcherWindow.isClosed()) {
					JOptionPane.showMessageDialog(getParent(), "テストプログラム生成ウィンドウを閉じてください", "info", JOptionPane.INFORMATION_MESSAGE);
					return ;
				}
				
				
				if (!folderTextField.getText().isEmpty()) {
					file = new File(folderTextField.getText());

					if (!file.isDirectory()) {
						JOptionPane.showMessageDialog(getParent(), "存在するフォルダを選択してください", "error", JOptionPane.ERROR_MESSAGE);
						return ;
					}
				}else{
					JOptionPane.showMessageDialog(getParent(), "存在するフォルダを選択してください", "error", JOptionPane.ERROR_MESSAGE);
					return ;
				}

				makeCodeVisitorList(folderTextField.getText());

				config.activate();
				create_class_sequence_list();

				if (baseDirTree!=null && !classList.isEmpty() && !diagramList.isEmpty()) {
					try {
						sg = new SourceGenerator();
						cb = sg.run(classList, diagramList);
						MyClass myClass = null;

						//山田さんのチェックに引っかかるかどうか判定
						//山田さんのエラーメッセージを出力して終了
						if (!sg.isGeneratable()) {
							return ;
						}

						for(int i=0;i<cb.getclasslistsize();i++){
							myClass = cb.getClass(i);
							sca = new SkeltonCodeAnalyzer(myClass);
							sca.doAnalyze();
							scv = sca.getSkeltonCodeVisitor();

							//Point p = new Point(start,end);
							//のようなステートメントがメソッド内で書かれているか
							//チェックを行う
							if (scv.isWrittenNewSt()) {
								JOptionPane.showMessageDialog(getParent(), "クラス図またはシーケンス図のメッセージが不正です", "error", JOptionPane.ERROR_MESSAGE);
								return ;
							}
						}

						javaPackage = sg.getClassList("java");
						ctw = new MatcherWindow(javaPackage,cb,codeVisitorList,baseDirTree);
						ctw.setTitle("テストプログラムの生成");
					} catch (UnExpectedException e1) {
						e1.printStackTrace();
					}
				}else{
					if (classList.isEmpty()) {
						JOptionPane.showMessageDialog(getComponent(), "クラスがありません");
					}
					if (diagramList.isEmpty()) {
						JOptionPane.showMessageDialog(getComponent(), "シーケンス図がありません");
					}
					if (baseDirTree == null) {
						JOptionPane.showMessageDialog(getComponent(), "フォルダの読み込みに失敗しました");
					}
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
			projectPath = projectAccessor.getProjectPath();
			projectAccessor.addProjectEventListener(this);
		} catch (ClassNotFoundException e) {
			e.getMessage();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void selectFolder(Component parentComponent) {
		//初期化	
		initVariables();

		File file;
		final JFileChooser chooser = new JFileChooser();
		chooser.setDragEnabled(true);

		if (!MatcherWindow.isClosed()) {
			JOptionPane.showMessageDialog(getParent(), "テストプログラム生成ウィンドウを閉じてください", "info", JOptionPane.INFORMATION_MESSAGE);
			return ;
		}

		if (!folderTextField.getText().isEmpty()) {
			file = new File(folderTextField.getText());

			if (!file.isDirectory()) {
				chooser.setCurrentDirectory(new File(projectPath));
			}else{
				chooser.setCurrentDirectory(new File(folderTextField.getText()));
			}
		}else{
			chooser.setCurrentDirectory(new File(projectPath));
		}

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int rtnVal = chooser.showOpenDialog(parentComponent);
		if(rtnVal == JFileChooser.APPROVE_OPTION) {
			//テキストフィールドにフォルダパスを入力
			folderTextField.setText(chooser.getSelectedFile().getPath());
		}

	}

	private void makeCodeVisitorList(final String projectPath){
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future;

		future = executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				SourceAnalyzer sa = null; 
				FileNode fileNode = null;
				baseDirTree = new FileTree(new FileNode(projectPath) , ".java$");
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
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.out.println("time out!!");
			future.cancel(true);
			baseDirTree = null;
			e.printStackTrace();
		}

		executor.shutdownNow();
		DebugMessageWindow.msgToOutPutTextArea();
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