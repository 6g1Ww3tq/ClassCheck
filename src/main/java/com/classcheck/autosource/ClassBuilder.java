package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IOperation;

public class ClassBuilder {

	List<MyClass> classlist;
	ClassBuilder(){
		classlist =new ArrayList<MyClass>();
	}

	public void addClass(IClass c){
		MyClass cp =new MyClass(c);
		cp.setName(c.getName());
		IAttribute a[] = c.getAttributes() ;
		for(int i=0;i<a.length;i++){
			Field f=new Field(a[i]);
			if(a[i].getNavigability().equals("Navigable")){ //誘導可能なら追加
				cp.addField(f);
			}
		}
		IOperation o[]=c.getOperations();
		for(int i=0;i<o.length;i++){
			Method m=new Method(o[i],c);
			cp.addMethod(m);
		}
		classlist.add(cp);
	}

	public void addProcess(List<ProcessBuilder> methodList,String sequenceName){
		for(int i=0;i<methodList.size();i++){
			for(int j=0;j<classlist.size();j++){
				if(classlist.get(j).getIClass().equals(methodList.get(i).getIClass())){
					classlist.get(j).addProcess(methodList.get(i),sequenceName);
				}
			}
			methodList.get(i).getIClass();//なにこれ？デバッグのごみ？
		}
	}

	public MyClass getClass(int i){
		return classlist.get(i);
	}

	public int getclasslistsize(){
		return classlist.size();
	}
	
	public List<MyClass> getClasslist() {
		return classlist;
	}
}
