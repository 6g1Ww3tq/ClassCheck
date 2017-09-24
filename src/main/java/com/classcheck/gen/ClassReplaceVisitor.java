package com.classcheck.gen;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ClassReplaceVisitor extends VoidVisitorAdapter<Void> {
	
	private String befClassSig;
	private String aftClassSig;
	private String aftClassName;
	
	public ClassReplaceVisitor() {
		super();
		befClassSig = null;
		aftClassSig = null;
		aftClassName = null;
	}

	public void setBefClassSig(String befClassSig) {
		this.befClassSig = befClassSig;
	}
	
	public void setAftClassSig(String aftClassSig) {
		this.aftClassSig = aftClassSig;
	}
	
	public void setAftClassName(String aftClassName) {
		this.aftClassName = aftClassName;
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		NameExpr ne = null;
		StringToModifier stm; 
		List<Integer> modList;

		if (befClassSig == null || aftClassSig == null || aftClassName == null) {
			return ;
		}
		
		if (n.getName().contains(befClassSig)) {
			//クラス名の変更
			ne = new NameExpr();
			ne.setName(aftClassName);
			n.setNameExpr(ne);
			
			//修飾子の変更
			stm = new StringToModifier(aftClassSig);
			modList = stm.toModifierList();
			
			for (Integer integer : modList) {
				n.setModifiers(integer.intValue());
			}
		}

		super.visit(n, arg);
	}
}