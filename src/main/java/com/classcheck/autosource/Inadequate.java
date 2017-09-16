package com.classcheck.autosource;

import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;

public class Inadequate {
	INamedElement model;
	INamedElement badElement;
	String errorString;

	public Inadequate(INamedElement diagram,INamedElement element,String s){
		model = diagram;
		badElement = element;
		errorString = s;
	}

	public String toString(){
		if(badElement!=null){
			if(badElement instanceof IMessage){
				return model.getName()+"\t"+ ((IMessage) badElement).getIndex()+" "+badElement.getName()+"\t"+errorString;
			}else{
				return model.getName()+"\t"+ badElement.getName()+"\t"+errorString;
			}
		}else{
			return model.getName()+"\t"+errorString;
		}
	}

	public Object[] toArray(){
		Object object[] = new Object[3];
		object[0] = model.getName();
		String s = "";
		if(badElement instanceof IMessage){
			s = ((IMessage) badElement).getIndex()+": "+badElement.getName();
		}else if(badElement != null){
			s = badElement.getName();
		}
		object[1] = s;
		object[2] = errorString;
		return object;
	}

}
