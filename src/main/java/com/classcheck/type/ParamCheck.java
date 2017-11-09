package com.classcheck.type;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.model.IClass;
import com.classcheck.analyzer.source.CodeVisitor;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

public class ParamCheck {

	private List<IClass> javaPackage;
	private DefaultTableModel tableModel;
	private String[] codeParams;
	private List<Parameter> umlParams;

	public ParamCheck(List<IClass> javaPackage, DefaultTableModel tableModel,
			String[] params, List<Parameter> parameters) {
		this.javaPackage = javaPackage;
		this.tableModel = tableModel;
		this.codeParams = params;
		this.umlParams = parameters;
	}

	public boolean evaluate() {
		boolean rtnVal = false;
		Object column_0,column_1;
		JComboBox box_1;
		MyClass myClass;
		CodeVisitor codeVisitor;
		Type umlDecType;
		String codeDecType;

		try{
			for(int i=0;i<codeParams.length;i++){
				codeDecType = codeParams[i];
				umlDecType = umlParams.get(i).getType();

				if (sameArrayType(umlDecType.toString(), codeDecType) &&
						sameGeneralType(umlDecType.toString(), codeDecType)) {
					rtnVal = true;
				}else{
					rtnVal = false;
					break;
				}
			}
		}catch(NullPointerException e){
			rtnVal = false;
			e.printStackTrace();
		}


		return rtnVal;
	}

	/**
	 * ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
	 * @param umlDecType
	 * @param codeDecType
	 * @return
	 */
	private boolean sameArrayType(String umlDecType,String codeDecType){
		boolean rtnVal = false;
		//型が配列かどうか
		boolean isArrayCode = false;
		boolean isArrayUML = false;

		if (umlDecType.contains("\\[\\]")) {
			isArrayUML = true;
			umlDecType = umlDecType.replaceAll("\\[\\]", "");
		}

		if(codeDecType.contains("\\[\\]")){ 
			isArrayCode = true;
			codeDecType = codeDecType.replaceAll("\\[\\]", "");
		}

		//ソースコードとクラス図の定義が同じ配列、あるいは単一であるか判断する
		if (isArrayUML != isArrayCode) {
			rtnVal = false;
		}else{
			rtnVal = true;
		}

		return rtnVal;
	}

	/**
	 * ソースコードとクラス図の定義が同じ参照型、あるいはjavaパッケージ内にある同じ型
	 * @param umlDecType
	 * @param codeDecType
	 * @return
	 */
	private boolean sameGeneralType(String umlDecType,String codeDecType){
		boolean rtnVal = false;
		boolean isContainCode = false;
		boolean isContainUML = false;

		//javaパッケージの中に定義されているクラスかどうか調べる
		for(IClass iClass : javaPackage){
			if (iClass.getName().equals(umlDecType)) {
				isContainUML = true;
			}
		}

		//同様
		for(IClass iClass : javaPackage){
			if (iClass.getName().equals(codeDecType)) {
				isContainCode = true;
			}
		}

		if (isContainUML && 
				isContainCode) {
			if (umlDecType.equals(codeDecType)) {
				rtnVal = true;
			}else{
				rtnVal = false;
			}
		}else{
			rtnVal = false;
		}

		return rtnVal;
	}
}
