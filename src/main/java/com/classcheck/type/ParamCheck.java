package com.classcheck.type;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.model.IClass;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.classcheck.autosource.MyClassCell;
import com.classcheck.window.DebugMessageWindow;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

public class ParamCheck {

	private List<IClass> javaPackage;
	private DefaultTableModel tableModel;
	private String[] umlMethodParams;
	private List<Parameter> codeMethodParams;

	public ParamCheck(List<IClass> javaPackage, DefaultTableModel tableModel,
			String[] umlMethodParams, List<Parameter> codeMethodParams) {
		this.javaPackage = javaPackage;
		this.tableModel = tableModel;
		this.umlMethodParams = umlMethodParams;
		this.codeMethodParams = codeMethodParams;
	}

	public boolean evaluate() {
		boolean rtnVal = false;
		Object column_0,column_1;
		JComboBox box_1;
		MyClass myClass;
		CodeVisitor codeVisitor;
		Parameter codeType;
		String codeTypeStr;
		String umlType;

		//引数なしの場合
		if (umlMethodParams.length == 0 &&
				codeMethodParams.isEmpty()) {
			rtnVal = true;
			return rtnVal;
		}
		
		try{
			System.out.println("umlMethodParams : "+umlMethodParams);
			System.out.println("codeMethodParams : "+codeMethodParams);
			for(int i=0;i<umlMethodParams.length;i++){
				umlType = umlMethodParams[i];
				codeType = codeMethodParams.get(i);
				
				//配列が「String args[]」のように後ろに［］が来るので型の後に来るように調整
				if (codeType.toString().contains("[]")) {
					codeTypeStr = codeType.toString().split(" ")[0] + "[]";
				}else{
					codeTypeStr = codeType.toString().split(" ")[0];
				}

				//スケルトンコードのメソッドのパラメータのそれぞれについて１番目（型）を見る
				umlType = umlType.split(" ")[0];

				if (isSameType(codeTypeStr, umlType) &&
						sameGeneralType(removeArray(codeTypeStr),removeArray(umlType))) {
					rtnVal = true;
				}else{
					rtnVal = false;
					break;
				}
			}
		}catch(NullPointerException e){
			rtnVal = false;
			System.out.println(e.toString());
		}

		return rtnVal;
	}

	/**
	 * ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
	 * @param codeType
	 * @param umlType
	 * @return
	 */
	private boolean isSameType(String codeType,String umlType){
		boolean rtnVal = false;
		//型が配列かどうか
		boolean isArrayCode = false;
		boolean isArrayUML = false;

		if (isArrayType(codeType)) {
			isArrayUML = true;
		}

		if (isArrayType(umlType)) {
			isArrayCode = true;
		}

		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
		}else{
			rtnVal = true;
		}

		return rtnVal;
	}

	private boolean isArrayType(String type){
		boolean isArray = false;

		if (type.contains("[]")) {
			isArray = true;
		}
		return isArray;
	}

	private String removeArray(String type){

		if (type.contains("[]")) {
			type = type.replaceAll("\\[\\]", "");
		}

		return type;
	}

	/**
	 * ソースコードとクラス図の定義が同じ参照型、あるいはjavaパッケージ内にある同じ型
	 * または基本的な型であるかどうかを比較する
	 * @param codeType
	 * @param umlType
	 * @return
	 */
	private boolean sameGeneralType(String codeType,String umlType){
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

		//javaパッケージの中に定義されているクラスかどうか調べる
		for(IClass iClass : javaPackage){
			if (iClass.getName().equals(codeType)) {
				if (iClass.getName().equals(umlType)) {
					rtnVal = true;
					return rtnVal;
				}

			}
		}

		//同じ基本型であるかどうかを調べる
		if (new BasicType(umlType, codeType).evaluate()) {
			rtnVal = true;
			return rtnVal;
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
