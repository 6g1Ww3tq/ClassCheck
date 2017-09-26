package com.classcheck.autosource;


public class MyClassCell{

	private MyClass myClass;

	public MyClassCell(MyClass myClass) {
		this.myClass = myClass;
	}
	
	@Override
	public String toString() {
		return myClass.getName();
	}
	
	public MyClass getMyClass() {
		return myClass;
	}
}
