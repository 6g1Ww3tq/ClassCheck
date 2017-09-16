package com.classcheck.autosource;

/*
 * @author  
 */
public class Config {
	boolean addDebugPrint;
	/**
	 * @uml.property  name="autoInstanceGenerate"
	 */
	boolean autoInstanceGenerate;
	boolean oclTranslate;
	/**
	 * @uml.property  name="addAssert"
	 */
	boolean addAssert;
	boolean addDefinitionCondition;

	String oclBoolean;
	String oclInteger;
	String oclReal;
	String oclString;
	String oclCollection;
	String oclBag;
	String oclSet;
	String oclOrderedSet;
	String oclSequence;

	String classCollection;

	public Config(){
		addDebugPrint = false;
		autoInstanceGenerate = true;
		addAssert = false;
		addDefinitionCondition = true;;
		oclTranslate = true;

		oclBoolean = "boolean";
		oclInteger = "int";
		oclReal = "double";
		oclString = "String";
		oclCollection = "List";
		oclBag = "List";
		oclSet = "Set";
		oclOrderedSet = "List";
		oclSequence = "List";

		classCollection = "Array";
	}

	Config(boolean debag,boolean instance,boolean assertion,boolean condition,boolean ocl,String multi){
		addDebugPrint = debag;
		autoInstanceGenerate = instance;
		addAssert = assertion;
		addDefinitionCondition = condition;
		oclTranslate = ocl;
		classCollection = multi;
	}

	public void setOclTranslate(String b,String i,String d,String s,String c,String bag,String set,String order,String sq){
		oclBoolean = b;
		oclInteger = i;
		oclReal = d;
		oclString = s;
		oclCollection = c;
		oclBag = bag;
		oclSet = set;
		oclOrderedSet = order;
		oclSequence = sq;
	}

	public String getClassCollction(){
		return classCollection;
	}

	public boolean isAddDebagPrint(){
		return addDebugPrint;
	}

	/**
	 * @return
	 * @uml.property  name="autoInstanceGenerate"
	 */
	public boolean isAutoInstanceGenerate(){
		return autoInstanceGenerate;
	}

	public boolean isOcLTranslate(){
		return oclTranslate;
	}

	public boolean isDefinitionCondition(){
		return addDefinitionCondition;
	}

	/**
	 * @return
	 * @uml.property  name="addAssert"
	 */
	public boolean isAddAssert(){
		return addAssert;
	}

	public String toString(){
		String s="";
		s+="addDebagPrint = "+addDebugPrint+"\r\n";
		s+="autoInstanceGenerate = "+autoInstanceGenerate+"\r\n";
		s+="classCollection = "+classCollection+"\r\n";
		s+="oclTranslate = "+oclTranslate+"\r\n";
		s+="oclBoolean = "+oclBoolean+"\r\n";
		s+="oclInteger = "+oclInteger+"\r\n";
		s+="oclReal = "+oclReal+"\r\n";
		s+="oclString = "+oclString+"\r\n";
		s+="oclCollection = "+oclCollection+"\r\n";
		s+="oclBag = "+oclBag+"\r\n";
		s+="oclSet = "+oclSet+"\r\n";
		s+="oclOrderedSet = "+oclOrderedSet+"\r\n";
		s+="oclSequence = "+oclSequence+"\r\n";

		return s;
	}

	public void activate() {
		Method.setDebagPrint(addDebugPrint);
		ProcessBuilder.setAutoGenerateInstance(autoInstanceGenerate);
		Method.setDefinitionConditions(addDefinitionCondition);
		Method.setAddAssert(addAssert);
		Field.setMultiTranslate(classCollection);
		OclTranslator.setOclTranslate(oclTranslate);

		OclTranslator.setOclBoolean(oclBoolean);
		OclTranslator.setOclInteger(oclInteger);
		OclTranslator.setOclReal(oclReal);
		OclTranslator.setOclString(oclString);
		OclTranslator.setOclCollection(oclCollection);
		OclTranslator.setOclBag(oclBag);
		OclTranslator.setOclSet(oclSet);
		OclTranslator.setOclOrderedSet(oclOrderedSet);
		OclTranslator.setOclSequence(oclSequence);
	}

}
