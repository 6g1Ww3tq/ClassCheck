package com.classcheck.type;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.model.IClass;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.Field;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.MyClassCell;
import com.github.javaparser.ast.body.FieldDeclaration;
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

	/** クラス図で定義された型　*/
	private String umlType;
	/** ソースコードで定義された型　*/
	private String codeType;

	private ReferenceType(List<IClass> javaPackage,DefaultTableModel tableModel){
		this.javaPackage = javaPackage;
		this.tableModel = tableModel;
	}

	public ReferenceType(List<IClass> javaPackage, DefaultTableModel tableModel,Method umlMethod,MethodDeclaration codeMethod) {
		this(javaPackage,tableModel);
		initMethod(umlMethod,codeMethod);
	}

	public ReferenceType(List<IClass> javaPackage,
			DefaultTableModel tableModel, Field umlField,
			FieldDeclaration codeField) {
		this(javaPackage,tableModel);
		initField(umlField,codeField);
	}

	private void initField(Field umlField, FieldDeclaration codeField) {
		this.umlType = umlField.getType();
		this.codeType = codeField.toString();
		String splits[] = codeType.split("=");
		splits = splits[0].split(" ");
		//配列が「String args[]」のように後ろに［］が来るので型の後に来るように調整
		if (codeType.contains("[]")) {
			codeType = splits[splits.length - 2] + "[]";
		}else{
			codeType = splits[splits.length - 2];
		}

		System.out.println("umlType(R):"+umlType);
		System.out.println("codeType(R):"+codeType);
	}

	private void initMethod(Method umlMethod, MethodDeclaration codeMethod) {
		umlType = umlMethod.getReturntype();
		String splits[] = codeMethod.getDeclarationAsString().split("\\(");
		splits = splits[0].split(" ");
		codeType = splits[splits.length - 2];
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
		int row = 0;

		if (umlType.contains("[]")) {
			isArrayUML = true;
			umlType = umlType.replaceAll("\\[\\]", "");
		}

		if(codeType.contains("[]")){ 
			isArrayCode = true;
			codeType = codeType.replaceAll("\\[\\]", "");
		}

		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
			return rtnVal;
		}


		//javaパッケージの中に定義されているクラスかどうか調べる
		for(IClass iClass : javaPackage){
			if (iClass.getName().equals(codeType)) {
				if (iClass.getName().equals(umlType)) {
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

				if (umlClassName.equals(umlType)) {
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

					if (codeClassName.equals(codeType)) {
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
