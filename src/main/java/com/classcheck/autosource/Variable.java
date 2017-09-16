package com.classcheck.autosource;



public class Variable {

	String Type;
	String Name;


	Variable(String type,String name){
		Type=type;
		Name=name;
	}

	public boolean isSame(Variable v){
		return this.equals(v);
	}

	public boolean isSame(String type ,String name){
		if(Type.equals(type) && Name.equals(name)){
			return true;
		}else{
			return false;
		}
	}

}
