package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
	private JPanel genPane;
	private JPanel folderPane;
	private JButton expBtn;
	private JButton folderBtn;
	private JButton genBtn;
	private JTextField folderTextField;

	private File sourceLoc;

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
		JPanel northPane = new JPanel();
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

		folderBtn = new JButton("フォルダ");
		folderPane = new JPanel();
		folderPane.setLayout(new FlowLayout(FlowLayout.CENTER,5,3));
		folderTextField = new JTextField(50);
		folderTextField.setDragEnabled(true);
		folderTextField.setToolTipText("<html>"+
				"ソースコードが存在する<br>" +
				"フォルダを選択してください" +
				"</html>");
		folderPane.add(folderTextField);

		northPane.add(genPane, BorderLayout.NORTH);
		northPane.add(folderTextField, BorderLayout.CENTER);
		northPane.add(new JLabel("フォルダ選択 : "),BorderLayout.WEST);
		northPane.add(folderBtn,BorderLayout.EAST);

		add(northPane,BorderLayout.NORTH);
		setVisible(true);
	}

	private void initEvents() {
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
				SourceGenerator sg = null;
				MatcherWindow ctw = null;

				//スケルトンコードのメソッド内でインスタンスを生成していないか
				//チェックを行う
				SkeltonCodeAnalyzer sca = null;
				SkeltonCodeVisitor scv = null;

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
								JOptionPane.showMessageDialog(getParent(), "シーケンス図のメッセージが不正です", "error", JOptionPane.ERROR_MESSAGE);
								return ;
							}
						}

						ctw = new MatcherWindow(cb,codeVisitorList,baseDirTree);
						ctw.setTitle("テストプログラムの生成");
					} catch (UnExpectedException e1) {
						// TODO 自動生成された catch ブロック
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
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future;
		final JFileChooser chooser = new JFileChooser();
		chooser.setDragEnabled(true);

		if (!folderTextField.getText().isEmpty()) {
			file = new File(folderTextField.getText());
			
			if (folderTextField.getText().isEmpty() || !file.isDirectory()) {
				chooser.setCurrentDirectory(new File(projectPath));
			}else{
				chooser.setCurrentDirectory(new File(folderTextField.getText()));
				chooser.setSelectedFile(file);
			}
		}else{
			chooser.setCurrentDirectory(new File(projectPath));
		}

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int rtnVal = chooser.showOpenDialog(parentComponent);
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

			//テキストフィールドにフォルダパスを入力
			folderTextField.setText(chooser.getSelectedFile().getPath());
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