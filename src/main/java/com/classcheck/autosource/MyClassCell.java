package com.classcheck.autosource;

import com.change_vision.jude.api.inf.model.IClass;

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
