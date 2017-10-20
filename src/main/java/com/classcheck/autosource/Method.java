package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.ITemplateBinding;

/**
 * @author  
 */
public class Method {
	IClass c;
	/**
	 * @uml.property  name="operation"
	 */
	IOperation operation;
	String definition;
	String visibility;
	String isStatic;
	String returntype;
	String name;
	String[] params;
	List<String> process;
	String returnString;
	static boolean addDebugPrint;
	static boolean addDefinitionConditions;
	static boolean addAssert;


	Method(IOperation o,IClass ic){
		c=ic;
		operation=o;
		process = new ArrayList<String>();
		//定義
		if(!o.getDefinition().equals("") || isConditionExist()){
			String s[] = o.getDefinition().split("\\n",0);
			for(int i=0;i<s.length;i++){
				if(i==0){
					definition = "\t/**\r\n\t *"+s[i];
				}else if(i!=0){
					definition +="\r\n\t *"+s[i];
				}
				if(i==s.length-1){
					if(addDefinitionConditions){
						String pre[]=o.getPreConditions();//事前条件
						if(pre.length>0){
							for(int j=0;j<pre.length;j++){
								definition +="\r\n\t *事前条件 : "+pre[j];
							}
						}String post[]=o.getPostConditions();//事後条件
						if(post.length>0){
							for(int j=0;j<post.length;j++){
								definition +="\r\n\t *事後条件 : "+post[j];
							}
						}
						if(!o.getBodyCondition().equals("")){
							definition +="\r\n\t *本体条件 : "+o.getBodyCondition();//本体条件
						}
					}
					definition +="\r\n\t */\r\n";
				}
			}
		}else{
			definition = "";
		}


		if(o.getName().equals("main")){//mainメソッドなら
			visibility="public ";
			isStatic="static ";
			returntype="void ";
			name=o.getName();
			params =new String[1];
			params[0] = "String[] args";
			//process="";
			returnString = "";

		}else if(o.getName().equals(ic.getName())){//コンストラクタなら
			visibility="public ";
			isStatic="";
			returntype="";
			name=o.getName();
			IParameter p[]=o.getParameters();
			params =new String[p.length];
			for(int i=0;i<p.length;i++){
				IClass pt = p[i].getType();
				String pstring="";
				if(OclTranslator.isInOclPackage(pt)){//oclパッケージ内のクラスなら変換
					pstring = OclTranslator.typeTranslator(pt);
				}else if(OclTranslator.isBindOcl(pt)){
					ITemplateBinding tb[] = pt.getTemplateBindings();
					pstring =OclTranslator.typeTranslator(pt,tb[0]);
				}else if(isBind(pt)){
					ITemplateBinding tb[] = pt.getTemplateBindings();
					pstring = getBindName(tb[0]);
				}else{
					pstring = p[i].getTypeExpression();
				}
				params[i]=pstring+" "+p[i].getName();

			}
			///process="";
			returnString = "";

		}else{//mainでもコンストラクタでもなければ
			if(o.isPublicVisibility()){
				visibility="public ";
			}else if(o.isPackageVisibility()){
				visibility="package ";
			}else if(o.isProtectedVisibility()){
				visibility="protected ";
			}else if(o.isPrivateVisibility()){
				visibility="private ";
			}else{
				visibility="";
			}

			if(o.isStatic()){
				isStatic="static ";
			}else{
				isStatic="";

			}

			IClass return_o =o.getReturnType();
			String return_s = "";
			if(OclTranslator.isInOclPackage(return_o)){//oclパッケージ内のクラスなら変換
				return_s = OclTranslator.typeTranslator(return_o);
			}else if(OclTranslator.isBindOcl(return_o)){
				ITemplateBinding tb[] = return_o.getTemplateBindings();
				return_s = OclTranslator.typeTranslator(return_o,tb[0]);
			}else if(isBind(return_o)){
				ITemplateBinding tb[] = return_o.getTemplateBindings();
				return_s = getBindName(tb[0]);
			}else{
				return_s = o.getReturnTypeExpression();
			}

			if(return_s.equals("void") || return_s.equals("")){
				returnString = "";
			}else if(return_s.equals("boolean")){
				returnString = "\t\treturn false;\r\n"; //タブ2個入れてreturn文入れて改行
			}else if(return_s.equals("int") || return_s.equals("float") || return_s.equals("double") || return_s.equals("byte") ||return_s.equals("short") ||return_s.equals("long") || return_s.equals("char")){
				returnString = "\t\treturn 0;\r\n";
			}else{
				returnString = "\t\treturn null;\r\n";
			}
			returntype=o.getReturnTypeExpression();
			if(o.getReturnType().getContainer() instanceof IPackage){//oclパッケージ内のクラスなら変換
				IPackage p = (IPackage) o.getReturnType().getContainer();
				if(p.getName().equals("ocl")){
					returntype=OclTranslator.typeTranslator(o.getReturnType(),o);
				}else if(OclTranslator.isBindOcl(o.getReturnType())){
					ITemplateBinding tb[] = o.getReturnType().getTemplateBindings();
					returntype=OclTranslator.typeTranslator(o.getReturnType(),tb[0]);
				}
			}
			name=o.getName();

			IParameter p[]=o.getParameters();
			params =new String[p.length];
			for(int i=0;i<p.length;i++){
				IClass pt = p[i].getType();
				String pstring="";
				if(OclTranslator.isInOclPackage(pt)){//oclパッケージ内のクラスなら変換
					pstring = OclTranslator.typeTranslator(pt);
				}else if(OclTranslator.isBindOcl(pt)){
					ITemplateBinding tb[] = pt.getTemplateBindings();
					pstring =OclTranslator.typeTranslator(pt,tb[0]);
				}else if(isBind(pt)){
					ITemplateBinding tb[] = pt.getTemplateBindings();
					pstring = getBindName(tb[0]);
				}else{
					pstring = p[i].getTypeExpression();
				}
				params[i]=pstring+" "+p[i].getName();
			}
			//process="";
		}

	}

	Method(IOperation o){//実装したインターフェイスのメソッド用

		definition = "\t@Override\t//実装したインターフェイスのメソッド **自動追加したコメント**\r\n";

		operation=o;
		visibility="public ";
		isStatic="";

		if(o.getReturnTypeExpression().equals("void") || o.getReturnTypeExpression().equals("")){
			returnString = "";
		}else if(o.getReturnTypeExpression().equals("boolean")){
			returnString = "\t\treturn false;\r\n"; //タブ2個入れてreturn文入れて改行
		}else if(o.getReturnTypeExpression().equals("int") || o.getReturnTypeExpression().equals("float") || o.getReturnTypeExpression().equals("double") || o.getReturnTypeExpression().equals("byte") ||o.getReturnTypeExpression().equals("short") ||o.getReturnTypeExpression().equals("long") || o.getReturnTypeExpression().equals("char")){
			returnString = "\t\treturn 0;\r\n";
		}else{
			returnString = "\t\treturn null;\r\n";
		}
		returntype=o.getReturnTypeExpression();
		if(o.getReturnType().getContainer() instanceof IPackage){//oclパッケージ内のクラスなら変換
			IPackage p = (IPackage) o.getReturnType().getContainer();
			if(p.getName().equals("ocl")){
				returntype=OclTranslator.typeTranslator(o.getReturnType());
			}else if(OclTranslator.isBindOcl(o.getReturnType())){
				ITemplateBinding tb[] = o.getReturnType().getTemplateBindings();
				returntype=OclTranslator.typeTranslator(o.getReturnType(),tb[0]);
			}
		}
		name=o.getName();
		IParameter p[]=o.getParameters();
		params =new String[p.length];
		for(int i=0;i<p.length;i++){
			params[i]=p[i].getTypeExpression()+" "+p[i].getName();
		}
		//process="";


	}

	private boolean isConditionExist(){
		String pre[] = operation.getPreConditions();//事前条件
		String post[] =operation.getPostConditions();//事後条件
		if(pre.length>0 || post.length>0 || !operation.getBodyCondition().equals("")){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * @param b
	 * @uml.property  name="addAssert"
	 */
	public static void setAddAssert(boolean b){
		addAssert = b;
	}

	public static void setDebagPrint(boolean b){
		addDebugPrint = b;
	}

	public static void setDefinitionConditions(boolean b){
		addDefinitionConditions = b;
	}

	public String toString(){
		String s="\r\n"+definition+"\t"+visibility+isStatic+returntype+" "+name+"(";
		for(int i=0;i<params.length;i++){
			if(i!=0)
				s+=",";
			s+=params[i];
		}
		s+="){\r\n";

		if(addDebugPrint){
			s+="\t\tSystem.out.println(\""+c.getName()+"."+operation.getName()+"\");\t//デバッグプリント **自動追加したコメント**\r\n";
		}

		String preCondition ="";
		if(addAssert){//事前条件
			String pre[] = operation.getPreConditions();
			for(int i=0;i<pre.length;i++){
				preCondition += "\t\t//assert "+pre[i]+";\t//事前条件 **自動追加したコメント**\r\n";
			}
		}
		s+=preCondition;
		for(int i=0;i<process.size();i++){
			s+=process.get(i);
		}
		String postCondition="";
		if(addAssert){//事後条件
			String post[] = operation.getPostConditions();
			for(int i=0;i<post.length;i++){
				postCondition += "\t\t//assert "+post[i]+";\t//事後条件 **自動追加したコメント**\r\n";
			}
		}
		s+=postCondition;
		s+=returnString+"\t}";
		return s;
	}

	public String getBody(){
		String st = new String();
		if(addDebugPrint){
			st+="\t\tSystem.out.println(\""+c.getName()+"."+operation.getName()+"\");\t//デバッグプリント **自動追加したコメント**\r\n";
		}

		String preCondition ="";
		if(addAssert){//事前条件
			String pre[] = operation.getPreConditions();
			for(int i=0;i<pre.length;i++){
				preCondition += "\t\t//assert "+pre[i]+";\t//事前条件 **自動追加したコメント**\r\n";
			}
		}
		st+=preCondition;
		for(int i=0;i<process.size();i++){
			st+=process.get(i);
		}
		String postCondition="";
		if(addAssert){//事後条件
			String post[] = operation.getPostConditions();
			for(int i=0;i<post.length;i++){
				postCondition += "\t\t//assert "+post[i]+";\t//事後条件 **自動追加したコメント**\r\n";
			}
		}
		st+=postCondition;
		
		if (returnString.contains("null")) {
			return st;
		}else{
			st+=returnString;
			return st;
		}
	}

	public String toSignature(){
		String s = "\r\n"+definition+"\t"+returntype+" "+name+"(";
		for(int i=0;i<params.length;i++){
			if(i!=0)
				s+=",";
			s+=params[i];
		}
		s+=");";
		return s;
	}


	/**
	 * @return
	 * @uml.property  name="operation"
	 */
	public IOperation getOperation(){
		return operation;
	}

	public void setProcess(String s){
		process.add(s);
	}

	public boolean isBind(IClass ic){
		if(ic.getTemplateBindings().length>0){
			return true;
		}else{
			return false;
		}
	}

	private String getBindName(ITemplateBinding tb){
		String g = "";
		if(tb.getActualMap()!=null){
			String s = tb.getActualMap().toString();
			String ss[]=s.split("=", 0);
			g=ss[1].replace("}", "");//マップからジェネリクスを抜き取る

		}
		if(!g.equals("")){
			return tb.getTemplate().getName()+"<"+g+">";
		}else{
			return tb.getTemplate().getName();
		}
	}

	public String getSignature(){
		String s = new String();
		
		if (definition.contains(" ")) {
			s += definition+" ";
		}
		s += visibility+isStatic+returntype+" "+name+"(";
		for(int i=0;i<params.length;i++){
			if(i!=0)
				s+=",";
			s+=params[i];
		}
		s+=")";
		return s;
	}
	
	public String getReturnString() {
		return returnString;
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getParams() {
		return params;
	}
	
	public String getReturntype() {
		return returntype;
	}
	
	public String getModifiers(){
		StringBuilder rtnSB = new StringBuilder();
		
		if (!visibility.isEmpty()) {
			rtnSB.append(visibility);
		}
		
		if (!isStatic.isEmpty()) {
			rtnSB.append(isStatic);
		}
		
		return rtnSB.toString();
	}
}