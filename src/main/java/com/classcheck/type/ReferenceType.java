package com.classcheck.type;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.model.IClass;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.MyClassCell;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * ソースコードで宣言されている参照型が
 * テーブルと対応付けた型になっているか
 * あるいは
 * 「java」パッケージで定義されているクラスであるか
 * どうか調べるクラス
 * @author masa
 *
 */
public class ReferenceType {
	private List<IClass> javaPackage;
	private DefaultTableModel tableModel;

	/** クラス図で定義されたメソッド　*/
	private Method method;
	/** ソースコードで定義されたメソッド　*/
	private MethodDeclaration methodDec;

	public ReferenceType(List<IClass> javaPackage, DefaultTableModel tableModel,Method method,MethodDeclaration methodDec) {
		this.javaPackage = javaPackage;
		this.tableModel = tableModel;
		this.method = method;
		this.methodDec = methodDec;
	}

	public boolean evaluate() {
		boolean rtnVal = false;
		//型が配列かどうか
		boolean isArrayCode = false;
		boolean isArrayUML = false;
		Object column_0,column_1;
		JComboBox box_1;
		MyClass myClass;
		CodeVisitor codeVisitor;
		String umlClassName,codeClassName;
		String methodRtnType = method.getReturntype();
		String splits[] = methodDec.getDeclarationAsString().split("\\(");
		splits = splits[0].split(" ");
		String methodDecRtnType = splits[splits.length - 2];
		int row = 0;
		
		if (methodRtnType.contains("\\[\\]")) {
			isArrayUML = true;
			methodRtnType = methodRtnType.replaceAll("\\[\\]", "");
		}
		
		if(methodDecRtnType.contains("\\[\\]")){ 
			isArrayCode = true;
			methodDecRtnType = methodDecRtnType.replaceAll("\\[\\]", "");
		}
		
		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
			return rtnVal;
		}


		//javaパッケージの中に定義されているクラスかどうか調べる
		for(IClass iClass : javaPackage){
			if (iClass.getName().equals(methodDecRtnType)) {
				rtnVal = true;
				return rtnVal;
			}
		}

		for (row = 0 ; row < tableModel.getRowCount() ; row++){
			column_0 = tableModel.getValueAt(row, 0);

			if (column_0 instanceof MyClassCell ){
				myClass = ((MyClassCell) column_0).getMyClass();
				umlClassName = myClass.getName();

				System.out.println(">>>>umlClassName " + umlClassName);
				System.out.println(">>>>methodRtnType " + methodRtnType);

				if (umlClassName.equals(methodRtnType)) {
					rtnVal = true;
					break;
				}

			} else {
				rtnVal = false;
				break;
			}
		}

		if (rtnVal) {
			column_1 = tableModel.getValueAt(row, 1);

			if(column_1 instanceof JComboBox){
				box_1 = (JComboBox) column_1;
				if (box_1.getSelectedItem() instanceof CodeVisitor){
					codeVisitor = (CodeVisitor) box_1.getSelectedItem();
					codeClassName = codeVisitor.getClassName();

					if (codeClassName.equals(methodDecRtnType)) {
						rtnVal = true;
					}else{
						rtnVal = false;
					}
				}
			}
		}

		return rtnVal;
	}
}
