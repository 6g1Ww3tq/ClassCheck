package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

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
import com.classcheck.window.MessageDialog;

public class AddonTabPanel extends JPanel implements IPluginExtraTabView, ProjectEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DebugMessageWindow debugWindow;
	private JCheckBox debugCheckBox;
	private JButton outStreamDebugWindowBtn;
	private JButton folderBtn;
	private JButton genBtn;
	private JButton helpBtn;
	private JButton jarSelectBtn;
	private JButton compileBtn;
	private JTextField folderTextField;
	private static String compileDirPath = new String();
	private static boolean compileSuccess = false;
	private static JTextField jarPathTextField;
	private static String sourceFolderPath = null;

	private static String[] encodingsArray = {
		"",
		"utf-8",
		"sjis"
	};
	private static JComboBox<String> encodingBox;

	//javaファイルのパスやデータを格納するリストを用意する(import文に使用する)
	private List<FileNode> javaFileNodeList;

	private List<CodeVisitor> codeVisitorList;
	FileTree baseDirTree;

	List<IClass> classList;
	List<ISequenceDiagram> diagramList;

	ProjectAccessor projectAccessor;
	private String projectPath;
	private String projectDir;

	private JButton configBtn;

	AstahAPI api;

	private ClassBuilder cb;
	private static Config config;

	ByteArrayOutputStream baos;

	public AddonTabPanel() {
		initConfig();
		initComponents();
		initEvents();
		addProjectEventListener();
		initConfig();
		initVariables();
		initDebugWindow();
	}

	public static String getSourceFolderPath() {
		return sourceFolderPath;
	}

	public static JTextField getJarPathTextField() {
		return jarPathTextField;
	}

	public static String getEncodingFormat() {
		Object obj = encodingBox.getSelectedItem();
		String format = null;

		if (obj instanceof String) {
			format = (String) obj;
		}

		return format;
	}

	private void initDebugWindow(){
		debugWindow = new DebugMessageWindow(debugCheckBox,"Debug",true);
	}

	private void initConfig() {
		config = new Config();
		config.activate();
		ToolTipManager.sharedInstance().setInitialDelay(250);
		ToolTipManager.sharedInstance().setReshowDelay(250);
		ToolTipManager.sharedInstance().setDismissDelay(60000);
	}

	private void initVariables() {
		classList = new ArrayList<IClass>();
		diagramList = new ArrayList<ISequenceDiagram>();
		codeVisitorList = new ArrayList<CodeVisitor>();
	}


	private void initComponents() {
		setLayout(new BorderLayout(3,3));
		JPanel mainPanel = new JPanel(new BorderLayout());
		JScrollPane mainScrollPane = new JScrollPane(mainPanel);
		JPanel debugPane = new JPanel(new BorderLayout());
		JPanel folderPane = new JPanel(new BorderLayout());
		JLabel folderLabel = null;
		JPanel boxPane = new JPanel();
		boxPane.setLayout(new BoxLayout(boxPane, BoxLayout.Y_AXIS));
		JPanel jarPane = new JPanel(new BorderLayout());
		JPanel compilePane = new JPanel(new BorderLayout());
		JPanel generatePane = new JPanel();
		baseDirTree = null;

		debugCheckBox = new JCheckBox();
		outStreamDebugWindowBtn = new JButton("標準出力");
		outStreamDebugWindowBtn.setVisible(true);
		JPanel panel = new JPanel();
		panel.add(new JLabel("デバックモード:"));
		panel.add(debugCheckBox);
		panel.add(outStreamDebugWindowBtn);
		debugPane.add(panel,BorderLayout.CENTER);

		folderTextField = new JTextField(50);
		folderTextField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		folderTextField.setDragEnabled(true);
		folderTextField.setToolTipText("<html>"+
				"ソースコードが存在する<br>" +
				"フォルダを選択してください" +
				"</html>");
		folderBtn = new JButton("フォルダ選択 ..");
		folderBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
		folderLabel = new JLabel("1.ソースコードが存在するフォルダ : ");
		folderLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		folderPane.add(folderLabel,BorderLayout.WEST);
		folderPane.add(folderTextField, BorderLayout.CENTER);
		folderPane.add(folderBtn,BorderLayout.EAST);

		jarPathTextField = new JTextField(50);
		jarPathTextField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		jarPathTextField.setToolTipText("<html>"+
				"jarファイルが存在する<br>" +
				"ファイルを選択してください" +
				"</html>");
		jarSelectBtn = new JButton();
		jarSelectBtn.setText("jar選択 ..");
		jarPane.add(new JLabel("ライブラリを使用する場合はパスを指定 : "),BorderLayout.WEST);
		jarPane.add(jarPathTextField,BorderLayout.CENTER);
		jarPane.add(jarSelectBtn,BorderLayout.EAST);

		encodingBox = new JComboBox<String>(encodingsArray);
		encodingBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		encodingBox.setToolTipText("<html>"+
				"文字コードを指定してください	" +
				"</html>");
		compileBtn = new JButton("2.コンパイル");
		compileBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
		compileBtn.setToolTipText("<html>"+
				"ソースコードのコンパイル" +
				"</html>");
		panel = new JPanel();
		panel.add(new JLabel("コンパイル時の文字コードの指定 : "));
		panel.add(encodingBox);
		panel.add(compileBtn);
		compilePane.add(panel,BorderLayout.EAST);

		genBtn = new JButton("3.生成");
		genBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
		genBtn.setToolTipText("<html>"+
				"テストプログラム生成" +
				"</html>");
		configBtn = new JButton("設定");
		configBtn.setVisible(false);
		helpBtn = new JButton("ヘルプ");
		helpBtn.setToolTipText("<html>"+
				"ヘルプ" +
				"</html>");

		generatePane.add(genBtn);
		generatePane.add(configBtn);
		generatePane.add(helpBtn);

		//デバックモード
		debugPane.setVisible(false);

		boxPane.add(debugPane);
		boxPane.add(jarPane);
		boxPane.add(compilePane);

		JPanel northPane = new JPanel(new BorderLayout());
		JPanel northPane_2 = new JPanel(new BorderLayout());
		northPane.add(folderPane,BorderLayout.NORTH);
		northPane.add(boxPane,BorderLayout.CENTER);
		northPane_2.add(northPane,BorderLayout.NORTH);
		northPane_2.add(generatePane,BorderLayout.SOUTH);

		mainPanel.add(northPane_2,BorderLayout.NORTH);



		add(mainScrollPane);
		setVisible(true);
	}

	private void initEvents() {

		helpBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desktop = Desktop.getDesktop();
				String resourcePath = "/doc/help.pdf";

				try {
					File helpFile = new File(projectDir + "/.help.pdf");
					ClassLoader classLoader = getClass().getClassLoader();
					FileUtils.copyURLToFile(classLoader.getResource(resourcePath),helpFile);

					desktop.open(helpFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

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

		outStreamDebugWindowBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DebugMessageWindow.clearText();
				System.out.println("標準出力.....");
				DebugMessageWindow.msgToTextArea();
			}
		});

		folderBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectFolder(getComponent());
			}

		});

		jarSelectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectJarFile(getComponent());
			}

		});

		compileBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ExecutorService executor = Executors.newSingleThreadExecutor();
				Future<?> future;

				future = executor.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						//コンパイルした時点のディレクトリを記憶
						compileDirPath = folderTextField.getText();
						//前提条件
						//テキストフィールド(ソースコードのパス)に書かれているディレクトリが存在しない
						if (exsistFolderWrittenFolderText() == false) {
							return "error";
							//テキストフィールド(ソースコードのパス)に書かれているディレクトリが空の場合
						}else if(folderTextField.getText().isEmpty()){
							return "error";
						}
						//testディレクトリが存在する場合は削除する
						File testDir = new File(folderTextField.getText() + "/test");
						if (testDir.isDirectory()) {
							FileUtils.forceDelete(testDir);
						}
						
						File workSpaceDir = new File(folderTextField.getText());
						//出力先のクラスフォルダを作成
						File classesDir = new File(workSpaceDir+"/test/classes");
						try {
							FileUtils.forceMkdir(classesDir);
						} catch (IOException e) {
							e.printStackTrace();
						}
						String[] javacCommand = null;
						List<String> javacList = new ArrayList<String>();
						javacList.add("javac");

						//ライブラリが必要な場合
						if (jarPathTextField.getText() != null) {
							javacList.add("-cp");
							javacList.add(jarPathTextField.getText());
						}

						FileTree javaFileTree = new FileTree(new FileNode(workSpaceDir) , ".java$");
						StringBuilder rtnMsg_sb = new StringBuilder();

						//ソースコードのファイルを指定
						List<FileNode> javaFileNode = javaFileTree.getFileNodeList();
						for (FileNode fileNode : javaFileNode) {
							javacList.add(fileNode.getPath());
						}

						//クラスファイルの出力先
						javacList.add("-d");
						javacList.add(classesDir.getPath());

						javacCommand = javacList.toArray(new String[javacList.size()]);

						Runtime javacRunTime = Runtime.getRuntime();
						Process p = javacRunTime.exec(javacCommand, null, workSpaceDir);
						p.waitFor();
						InputStream inputStream = p.getInputStream();
						InputStream errorStream = p.getErrorStream();
						BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));
						BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(errorStream));
						String readLine = null;

						//"***StandardStream
						while((readLine = inputBufferedReader.readLine()) != null){
							rtnMsg_sb.append(readLine+"\n");
						}
						//"***ErrorStream
						while((readLine = errorBufferedReader.readLine()) != null){
							rtnMsg_sb.append(readLine+"\n");
						}

						inputBufferedReader.close();
						errorBufferedReader.close();
						inputStream.close();
						errorStream.close();


						System.out.println(rtnMsg_sb.toString());
						DebugMessageWindow.msgToTextArea();

						//標準出力やエラー出力を文字列として返却
						return rtnMsg_sb.toString();
					}
				});

				try {
					String rtnTextMsg = (String) future.get(3, TimeUnit.SECONDS);
					MessageDialog dialogMsg = null;
					if(rtnTextMsg.equals("error")){
						JOptionPane.showMessageDialog(getParent(), "存在するフォルダを選択してください", "error", JOptionPane.ERROR_MESSAGE);
						compileSuccess = false;
					}else{
						if (rtnTextMsg.isEmpty()) {
							JOptionPane.showMessageDialog(getParent(), "コンパイルに成功しました", "成功", JOptionPane.INFORMATION_MESSAGE);
							compileSuccess = true;
						}else{
							/*
							 * テストに失敗した場合は失敗メッセージを表示し、
							 * testディレクトリを削除する
							 */
							dialogMsg = new MessageDialog("コンパイルに失敗しました");
							compileSuccess = false;
							File workSpaceDir = new File(folderTextField.getText());
							File testDir = new File(workSpaceDir+"/test");
							try {
								FileUtils.deleteDirectory(testDir);
							} catch (IOException fileError) {
								fileError.printStackTrace();
							}
							dialogMsg.setTextArea(rtnTextMsg);
						}
					}
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				} catch (ExecutionException e2) {
					e2.printStackTrace();
				} catch (TimeoutException e2) {
					System.out.println("time out!!");
					future.cancel(true);
					baseDirTree = null;
					e2.printStackTrace();
				}

				executor.shutdownNow();

			}
		});

		genBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//前提条件
				//テキストフィールド(ソースコードのパス)に書かれているディレクトリが存在しない
				if (exsistFolderWrittenFolderText() == false) {
					JOptionPane.showMessageDialog(getParent(), "存在するフォルダを選択してください", "error", JOptionPane.ERROR_MESSAGE);
					return ;
					//テキストフィールド(ソースコードのパス)に書かれているディレクトリが空の場合
				}else if(folderTextField.getText().isEmpty()){
					JOptionPane.showMessageDialog(getParent(), "存在するフォルダを選択してください", "error", JOptionPane.ERROR_MESSAGE);
					return ;
				}else if (!MatcherWindow.isClosed()) {
					JOptionPane.showMessageDialog(getParent(), "テストプログラム生成ウィンドウを閉じてください", "info", JOptionPane.INFORMATION_MESSAGE);
					return ;
				}else if (compileSuccess == false) {
					JOptionPane.showMessageDialog(getParent(), "ソースコードをコンパイルしてください", "error", JOptionPane.ERROR_MESSAGE);
					return ;
				}else if (compileDirPath.equals(folderTextField.getText()) == false){
					JOptionPane.showMessageDialog(getParent(), "一度コンパイルを行ってください", "error", JOptionPane.ERROR_MESSAGE);
					return ;
				}

				//初期化	
				initVariables();
				List<IClass> javaPackage;

				SourceGenerator sg = null;
				MatcherWindow ctw = null;

				//スケルトンコードのメソッド内でインスタンスを生成していないか
				//チェックを行う
				SkeltonCodeAnalyzer sca = null;
				SkeltonCodeVisitor scv = null;


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
						sourceFolderPath = folderTextField.getText();
						ctw = new MatcherWindow(javaPackage,cb,codeVisitorList,baseDirTree,javaFileNodeList);
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

	private boolean exsistFolderWrittenFolderText(){
		File file = null;
		boolean exsist = true;

		if (!folderTextField.getText().isEmpty()) {
			file = new File(folderTextField.getText());

			if (!file.isDirectory()) {
				exsist = false;
				return exsist;
			}
		}else{
			return exsist;
		}
		return exsist;
	}

	private void create_class_sequence_list() {
		classList = SourceGenerator.getClassList();
		diagramList = SourceGenerator.getSequenceDiagramList();
	}


	private void addProjectEventListener() {
		try {
			int lastIndex = 0;
			api = AstahAPI.getAstahAPI();
			projectAccessor = api.getProjectAccessor();
			projectPath = projectAccessor.getProjectPath();
			lastIndex = projectPath.lastIndexOf("/");
			projectDir = projectPath.substring(0, lastIndex);
			projectAccessor.addProjectEventListener(this);
		} catch (ClassNotFoundException e) {
			e.getMessage();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void selectJarFile(Component parentComponent) {
		File file;
		final JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter jarFilter = new FileNameExtensionFilter("JARファイル", "jar"); 
		fileChooser.setDragEnabled(true);

		if (!MatcherWindow.isClosed()) {
			JOptionPane.showMessageDialog(getParent(), "テストプログラム生成ウィンドウを閉じてください", "info", JOptionPane.INFORMATION_MESSAGE);
			return ;
		}

		//ファイルセレクトにjarファイルだけを選択するようにフィルターをかける
		fileChooser.addChoosableFileFilter(jarFilter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int selected = fileChooser.showOpenDialog(parentComponent);
		if(selected == JFileChooser.FILES_ONLY) {
			file = fileChooser.getSelectedFile();

			//jarTextFiledに入力
			jarPathTextField.setText(file.toString());
		}
	}

	private void selectFolder(Component parentComponent) {
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
				//javaファイルのパスやデータのリストを用意する
				javaFileNodeList = new ArrayList<FileNode>();

				while (it.hasNext()) {
					fileNode = (FileNode) it.next();

					if (fileNode!=null) {
						try {
							sa = new SourceAnalyzer(fileNode);

							sa.doAnalyze();

							sb.append(fileNode+"\n");
							//javaファイルのパスやデータを格納するリストに追加する
							if (javaFileNodeList.contains(fileNode) == false) {
								javaFileNodeList.add(fileNode);
							}
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
		DebugMessageWindow.msgToTextArea();
	}

	@Override
	public void projectChanged(ProjectEvent e) {
	}

	@Override
	public void projectClosed(ProjectEvent e) {
		File helpFile = new File(projectDir + "/.help.pdf");
		File tmpFolder = new File(projectPath + "/.tmp");
		if (helpFile.exists()) {
			helpFile.delete();
		}else if(tmpFolder.exists()){
			tmpFolder.delete();
		}
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
		return "Show TestProgramView here";
	}

	@Override
	public String getTitle() {
		return "テストプログラム生成";
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