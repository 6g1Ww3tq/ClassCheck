package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.IRealization;

/**
 * @author  
 */
public class MyClass {
	/* クラスの情報を保持するクラス
	 * 最終的にここから1つのクラス分のソースコードができるようにこのクラスにデータを入れていく*/
	IClass iClass;
	String definition;//定義
	List<Field> fields;
	List<Method> methods;
	/**
	 * @uml.property  name="name"
	 */
	String name;
	String generalization;//継承するクラス
	IClass realizations[];//実装するインターフェイス

	@Override
	public MyClass clone() throws CloneNotSupportedException {
		MyClass clMyClass = new MyClass(iClass);
		IClass reals[] = new IClass[realizations.length];
		clMyClass.setDefinition(new String(definition));
		clMyClass.setFields(new ArrayList<Field>(fields));
		clMyClass.setMethods(new ArrayList<Method>(methods));
		clMyClass.setName(new String(name));
		clMyClass.setGeneralization(new String(generalization));
		
		for(int i=0;i<realizations.length;i++){
			reals[i] = realizations[i];
		}
		clMyClass.setRealizations(reals);
		
		return clMyClass;
	}
	
	public void setGeneralization(String generalization) {
		this.generalization = generalization;
	}
	
	public void setRealizations(IClass[] realizations) {
		this.realizations = realizations;
	}
	
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public MyClass(IClass ic){
		iClass=ic;
		//定義
		if(!iClass.getDefinition().equals("")){
			String s[] = iClass.getDefinition().split("\n",0);
			for(int i=0;i<s.length;i++){
				if(i==0){
					definition = "\t/**\r\n\t *"+s[i];
				}else if(i!=0){
					definition +="\r\n\t *"+s[i];
				}
				if(i==s.length-1){
					definition +="\r\n\t */\r\n";
				}
			}
		}else{
			definition = "";
		}

		generalization = "";
		IGeneralization g[] = iClass.getGeneralizations();
		if(g.length>0){
			generalization = g[0].getSuperType().getName();
		}

		IRealization r[] = iClass.getClientRealizations();
		realizations = new IClass[r.length];
		for(int i=0;i<r.length;i++){
			realizations[i] = (IClass) r[i].getSupplier();
		}

		fields=new ArrayList<Field>();
		methods=new ArrayList<Method>();

	}
	
	public String getClassSig(){
		String rtnSig = "";
		
		rtnSig += iClass.getTypeModifier();
		if (!iClass.getTypeModifier().isEmpty()) {
			rtnSig += " ";
		}
		rtnSig += "class " + iClass.getName();
		return rtnSig;
	}

	public void addField(Field f){
		fields.add(f);
	}

	public void addMethod(Method m){
		methods.add(m);
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName(){
		return name;
	}

	public void addProcess(ProcessBuilder mc,String sequenceName){
		/*クラス内のどのメソッドにprosessを追加するかを判別して追加*/
		IOperation o=mc.getCallOperation();
		if(o==null){//起動メッセージが無い場合null
			for(int i=0;i<methods.size();i++){
				if(methods.get(i).getOperation().getName().equals(sequenceName)){ //メソッド名がシーケンス図名と同じなら
					mc.setFields(fields);
					mc.processBuild();
					methods.get(i).setProcess(mc.getProcess());
				}
			}
		}else{
			for(int i=0;i<methods.size();i++){
				if(methods.get(i).getOperation().equals(mc.getCallOperation())){
					mc.setFields(fields);
					mc.processBuild();
					methods.get(i).setProcess(mc.getProcess());
				}
			}
		}
	}

	public String toString(){
		String s=definition;
		if(isEnum()){
			s+="enum "+name+"{\r\n";
			for(int i=0;i<fields.size();i++){
				s+="\t"+fields.get(i).getName()+",\r\n";
				if(i==fields.size()-1){
					s+="\r\n\t;\r\n";
				}
			}
			for(int i=0;i<methods.size();i++){
				s+=methods.get(i).toString()+"\r\n";
			}
			s+="}";
			return s;

		}else if(isInterface()){
			s+="interface "+name;
			if(!generalization.equals("")){
				s+=" extends "+generalization;
			}
			s+="{\r\n";
			for(int i=0;i<fields.size();i++){
				s+=fields.get(i).interfaceString()+"\r\n";
			}
			for(int i=0;i<methods.size();i++){
				s+=methods.get(i).toSignature()+"\r\n";
			}

			s+="}";
			return s;

		}else{
			
			s+="class "+name;
			if(!generalization.equals("")){
				s+=" extends "+generalization;
			}
			for(int i=0;i<realizations.length;i++){
				if(i==0){
					s+=" implements "+realizations[i].getName();
				}else{
					s+=" , "+realizations[i].getName();
				}
			}
			s+="{\r\n";
			for(int i=0;i<fields.size();i++){
				s+=fields.get(i).toString()+"\r\n";
			}

			for(int i=0;i<methods.size();i++){
				s+=methods.get(i).toString()+"\r\n";
			}

			for(int i=0;i<realizations.length;i++){
				IOperation o[] = realizations[i].getOperations();
				for(int j=0;j<o.length;j++){
					s+= new Method(o[j]).toString()+"\r\n";
				}
			}

			s+="}";
			return s;
		}
	}

	private boolean isEnum(){
		String type[] = iClass.getStereotypes();
		for(int i=0;i<type.length;i++){
			if(type[i].equals("enum")){
				return true;
			}
		}
		return false;
	}

	private boolean isInterface(){
		String type[] = iClass.getStereotypes();
		for(int i=0;i<type.length;i++){
			if(type[i].equals("interface")){
				return true;
			}
		}
		return false;
	}

	public IClass getIClass(){
		return iClass;
	}

	/**
	 * @param n
	 * @uml.property  name="name"
	 */
	public void setName(String n){
		name=n;
	}
	
	public List<Method> getMethods() {
		return methods;
	}
	
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
	
	public List<Field> getFields() {
		return fields;
	}
	
	public String getDefinition() {
		return definition;
	}
}
