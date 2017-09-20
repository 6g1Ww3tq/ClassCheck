package com.classcheck.basic;

public class Pocket<E> {
	E obj;
	
	public Pocket(E obj) {
		this.obj = obj;
	}
	
	public E get(){
		return obj;
	}
	
	public void set(E obj){
		this.obj = obj;
	}
}
