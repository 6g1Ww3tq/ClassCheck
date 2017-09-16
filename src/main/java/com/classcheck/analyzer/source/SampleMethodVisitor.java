package com.classcheck.analyzer.source;

import com.classcheck.autosource.ClassBuilder;
import com.classcheck.autosource.Method;
import com.classcheck.autosource.MyClass;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import org.apache.lucene.search.spell.LevensteinDistance;
import org.osgi.util.cdma.MEIDCondition;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class SampleMethodVisitor extends VoidVisitorAdapter<Void> {

	private static ClassBuilder cb;
	private StringBuilder sbMsg;

	public SampleMethodVisitor() {
		sbMsg = new StringBuilder();
	}

	
	@Override
	public void visit(ClassOrInterfaceDeclaration classDec, Void arg1) {
		sbMsg.append(classDec.getNameExpr()+"\n");
		List<BodyDeclaration> list = classDec.getMembers();
		
		for (BodyDeclaration bodyDeclaration : list) {
			sbMsg.append(bodyDeclaration+"\n");
			if (bodyDeclaration instanceof MethodDeclaration) {
				MethodDeclaration m = (MethodDeclaration) bodyDeclaration;
				sbMsg.append("--isMethod--\n");
			}
			if (bodyDeclaration instanceof ConstructorDeclaration) {
				
			}
		}
		super.visit(classDec, arg1);
	}

	@Override
	public void visit(MethodDeclaration methodDec, Void arg) {
//
//		BlockStmt methodBody = methodDec.getBody();
//		List<Parameter> paramList = methodDec.getParameters();
//		Parameter param;
//		List<Statement> stList = methodBody.getStmts();
//		MyClass myClass = null;
//		List<Method> methodList;
//		Method targetMethod = null;
//		LevensteinDistance levensteinAlgorithm = new LevensteinDistance();
//		float distance;
//		float mostNearDistance = -1;
//
//		for(int i=0;i<cb.getclasslistsize();i++){
//			myClass = cb.getClass(i);
//			methodList = myClass.getMethods();
//
//			for (Method method : methodList) {
//				sbMsg.append("「"+method.getSignature()+"」");
//				sbMsg.append("と");
//				sbMsg.append("「"+methodDec.getDeclarationAsString()+"」"+" の距離:");
//				distance = levensteinAlgorithm.getDistance(method.getSignature(), methodDec.getDeclarationAsString());
//				if (distance > mostNearDistance) {
//					mostNearDistance = distance;
//					targetMethod = method;
//				}
//				sbMsg.append(distance+"\n");
//			}
//		}
//		
//		sbMsg.append("\n");
//		sbMsg.append("結果(最も近い数値):"+mostNearDistance+"\n");
//		if (targetMethod != null) {
//			sbMsg.append("ソースコード:\n"+methodDec.getDeclarationAsString()+"\n");
//			sbMsg.append("ターゲットシーケンス:\n"+targetMethod.getSignature()+"\n");
//			sbMsg.append("中身の確認：\n");
//			sbMsg.append("-ソースコード-\n");
//			for (Statement statement : stList) {
//				sbMsg.append(statement+"\n");
//			}
//			sbMsg.append("-シーケンス-\n");
//			sbMsg.append(targetMethod.getBody());
//			/*
//			sbMsg.append("中身の比較数値:"+levensteinAlgorithm.getDistance(stList.toString(), targetMethod.getBody())+"\n");
//			sbMsg.append("ソースコードがシーケンスのステートメントを含んでいるかの判定:"+stList.toString().contains(targetMethod.getBody())+"\n");
//			*/
//		}
//
//		//メソッドすべてを取得する
//		/*
//		sbMsg.append(methodDec.getDeclarationAsString()+"{\n");
//		for (Statement statement : stList) {
//			sbMsg.append(statement+"\n");
//		}
//
//		sbMsg.append("}\n");
//		 */
//
//		//メソッドの指定
//		/*
//		if (Modifier.isPublic(methodDec.getModifiers()) && methodDec.getParameters().isEmpty() && methodDec.getType().toString().equals("void")) {
//			sbMsg.append(methodDec.getType()+" ");
//			sbMsg.append(methodDec.getName()+"()");
//			sbMsg.append(methodDec.getBody()+"\n");
//		}
//		*/

		super.visit(methodDec, arg);
	}

	public String getMessage() {
		return sbMsg.toString();
	}

	public static void setClassBuilder(ClassBuilder cb) {
		SampleMethodVisitor.cb = cb;
	}
}
