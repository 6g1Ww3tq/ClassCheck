package sample;

import java.io.IOException;
import java.util.Iterator;

import com.classcheck.analyzer.source.SourceAnalyzer;
import com.classcheck.analyzer.source.SourceVisitor;
import com.classcheck.tree.FileNode;
import com.classcheck.tree.Tree;

public class SampleAST {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SourceVisitor visitor = null;
		SourceAnalyzer sa = null; 
		FileNode fileNode = null;
		Tree tree = new Tree(new FileNode("src/main/java/com/classcheck") , ".java$");
		StringBuilder sb = new StringBuilder();
		Iterator<FileNode> it = tree.iterator();

		while (it.hasNext()) {
			fileNode = (FileNode) it.next();

			if (fileNode!=null) {
				sb.append(fileNode+"\n");
				try {
					sa = new SourceAnalyzer(fileNode);
					visitor = new SourceVisitor();
					sa.accept(visitor);

					sb.append("this file is : " + fileNode + "\n" + visitor.toString());

				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}

		}

		System.out.println(sb.toString());
	}
}