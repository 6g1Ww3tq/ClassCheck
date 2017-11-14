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
	private Method umlMethod;
	/** ソースコードで定義されたメソッド　*/
	private MethodDeclaration codeMethod;

	public ReferenceType(List<IClass> javaPackage, DefaultTableModel tableModel,Method umlMethod,MethodDeclaration codeMethod) {
		this.javaPackage = javaPackage;
		this.tableModel = tableModel;
		this.umlMethod = umlMethod;
		this.codeMethod = codeMethod;
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
		String umlMethodRtnType = umlMethod.getReturntype();
		String splits[] = codeMethod.getDeclarationAsString().split("\\(");
		splits = splits[0].split(" ");
		String codeMethodRtnType = splits[splits.length - 2];
		int row = 0;

		if (umlMethodRtnType.contains("[]")) {
			isArrayUML = true;
			umlMethodRtnType = umlMethodRtnType.replaceAll("\\[\\]", "");
		}

		if(codeMethodRtnType.contains("[]")){ 
			isArrayCode = true;
			codeMethodRtnType = codeMethodRtnType.replaceAll("\\[\\]", "");
		}

		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
			return rtnVal;
		}


		//javaパッケージの中に定義されているクラスかどうか調べる
		for(IClass iClass : javaPackage){
			if (iClass.getName().equals(codeMethodRtnType)) {
				if (iClass.getName().equals(umlMethodRtnType)) {
					rtnVal = true;
					return rtnVal;
				}
			}
		}

		for (row = 0 ; row < tableModel.getRowCount() ; row++){
			column_0 = tableModel.getValueAt(row, 0);

			if (column_0 instanceof MyClassCell ){
				myClass = ((MyClassCell) column_0).getMyClass();
				umlClassName = myClass.getName();

				if (umlClassName.equals(umlMethodRtnType)) {
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

					if (codeClassName.equals(codeMethodRtnType)) {
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
