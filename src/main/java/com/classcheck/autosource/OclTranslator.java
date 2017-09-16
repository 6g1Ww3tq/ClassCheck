package com.classcheck.autosource;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IClassifierTemplateParameter;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.ITemplateBinding;

/**
 * @author  
 */
public class OclTranslator {

	static String oclBoolean;
	static String oclInteger;
	static String oclReal;
	static String oclString;
	static String oclCollection;
	static String oclBag;
	static String oclSet;
	static String oclOrderedSet;
	static String oclSequence;

	static boolean oclTranslate;

	/**
	 * @param b
	 * @uml.property  name="oclTranslate"
	 */
	public static void setOclTranslate(boolean b){
		oclTranslate = b;
	}

	public static String typeTranslator(IClass c,IAttribute a){
		if(c.getName().equals("Integer")){
			return oclInteger;
		}else if(c.getName().equals("Boolean")){
			return oclBoolean;
		}else if(c.getName().equals("Real")){
			return oclReal;
		}else if(c.getName().equals("String")){
			return oclString;
		}else if(c.getName().equals("Collection")){
			String tm = a.getTypeModifier();
			return oclCollection+"<"+tm+">";
		}else if(c.getName().equals("Bag")){
			String tm = a.getTypeModifier();
			return oclBag+"<"+tm+">";
		}else if(c.getName().equals("Set")){
			String tm = a.getTypeModifier();
			return oclSet+"<"+tm+">";
		}else if(c.getName().equals("Sequence")){
			String tm = a.getTypeModifier();
			return oclSequence+"<"+tm+">";
		}else if(c.getName().equals("OrderedSet")){
			String tm = a.getTypeModifier();
			return oclOrderedSet+"<"+tm+">";
		}else if(c.getName().equals("Enumeration")){

		}

		return null;
	}

	public static String typeTranslator(IClass c,IOperation o){
		if(c.getName().equals("Integer")){
			return oclInteger;
		}else if(c.getName().equals("Boolean")){
			return oclBoolean;
		}else if(c.getName().equals("Real")){
			return oclReal;
		}else if(c.getName().equals("String")){
			return oclString;
		}else if(c.getName().equals("Collection")){
			String tm = o.getTypeModifier();
			return oclCollection+"<"+tm+">";
		}else if(c.getName().equals("Bag")){
			String tm = o.getTypeModifier();
			return oclBag+"<"+tm+">";
		}else if(c.getName().equals("Set")){
			String tm = o.getTypeModifier();
			return oclSet+"<"+tm+">";
		}else if(c.getName().equals("Sequence")){
			String tm = o.getTypeModifier();
			return oclSequence+"<"+tm+">";
		}else if(c.getName().equals("OrderedSet")){
			String tm = o.getTypeModifier();
			return oclOrderedSet+"<"+tm+">";
		}else if(c.getName().equals("Enumeration")){

		}

		return null;
	}

	public static String typeTranslator(IClass c){
		if(c.getName().equals("Integer")){
			return oclInteger;
		}else if(c.getName().equals("Boolean")){
			return oclBoolean;
		}else if(c.getName().equals("Real")){
			return oclReal;
		}else if(c.getName().equals("String")){
			return oclString;
		}else if(c.getName().equals("Collection")){
			return oclCollection;
		}else if(c.getName().equals("Bag")){
			return oclBag;
		}else if(c.getName().equals("Set")){
			return oclSet;
		}else if(c.getName().equals("Sequence")){
			return oclSequence;
		}else if(c.getName().equals("OrderedSet")){
			return oclOrderedSet;
		}else if(c.getName().equals("Enumeration")){

		}

		return null;
	}

	public static String instanceGenerateTranslator(IClass c){
		if(c.getName().equals("Integer")){
			return oclInteger;
		}else if(c.getName().equals("Boolean")){
			return oclBoolean;
		}else if(c.getName().equals("Real")){
			return oclReal;
		}else if(c.getName().equals("String")){
			return oclString;
		}else if(c.getName().equals("Collection")){
			return definiteCollection(oclCollection);
		}else if(c.getName().equals("Bag")){
			return definiteCollection(oclBag);
		}else if(c.getName().equals("Set")){
			return definiteCollection(oclSet);
		}else if(c.getName().equals("Sequence")){
			return definiteCollection(oclSequence);
		}else if(c.getName().equals("OrderedSet")){
			return definiteCollection(oclOrderedSet);
		}else if(c.getName().equals("Enumeration")){

		}

		return null;
	}

	public static String instanceGenerateTranslator(IClass c,ITemplateBinding tb){
		String g = "";
		if(tb.getActualMap()!=null){
			String s = tb.getActualMap().toString();
			String ss[]=s.split("=", 0);
			g=ss[1].replace("}", "");//マップからジェネリクスを抜き取る

		}
		if(c.getName().equals("Integer")){
			return oclInteger;
		}else if(c.getName().equals("Boolean")){
			return oclBoolean;
		}else if(c.getName().equals("Real")){
			return oclReal;
		}else if(c.getName().equals("String")){
			return oclString;
		}else if(c.getName().equals("Collection")){
			if(!g.equals(""))
				return definiteCollection(oclCollection)+"<"+g+">";
			return definiteCollection(oclCollection);
		}else if(c.getName().equals("Bag")){
			if(!g.equals(""))
				return definiteCollection(oclBag)+"<"+g+">";
			return definiteCollection(oclBag);
		}else if(c.getName().equals("Set")){
			if(!g.equals(""))
				return definiteCollection(oclSet)+"<"+g+">";
			return definiteCollection(oclSet);
		}else if(c.getName().equals("Sequence")){
			if(!g.equals(""))
				return definiteCollection(oclSequence)+"<"+g+">";
			return definiteCollection(oclSequence);
		}else if(c.getName().equals("OrderedSet")){
			if(!g.equals(""))
				return definiteCollection(oclOrderedSet)+"<"+g+">";
			return definiteCollection(oclOrderedSet);
		}else if(c.getName().equals("Enumeration")){

		}

		return null;
	}

	public static boolean isInOclPackage(IClass ic){
		if(oclTranslate==false){
			return false;
		}else if(ic.getContainer() instanceof IPackage && ((INamedElement) ic.getContainer()).getName().equals("ocl")){
			return true;
		}else{
			return false;
		}

	}

	public static boolean isBindOcl(IClass ic){
		if(oclTranslate==false){
			return false;
		}else if(ic.getTemplateBindings().length>0){//ライフラインのベースクラスがテンプレートバインディングを持ち、バインド先ががOCLのパッケージ内なら
			ITemplateBinding tb[] = ic.getTemplateBindings();
			IClass c = tb[0].getTemplate();
			if(c.getContainer() instanceof IPackage && ((INamedElement) c.getContainer()).getName().equals("ocl")){
				return true;
			}
		}
		return false;
	}

	public static String typeTranslator(IClass bind, ITemplateBinding tb) {
		String g = "";
		IClassifierTemplateParameter tp[] = bind.getTemplateParameters();//現状意味なし、本当はMapのキーとして検索に使いたい
		if(tb.getActualMap()!=null){
			String s = tb.getActualMap().toString();
			String ss[]=s.split("=", 0);
			g=ss[1].replace("}", "");

		}
		IClass c = tb.getTemplate();
		if(c.getName().equals("Integer")){
			return oclInteger;
		}else if(c.getName().equals("Boolean")){
			return oclBoolean;
		}else if(c.getName().equals("Real")){
			return oclReal;
		}else if(c.getName().equals("String")){
			return oclString;
		}else if(c.getName().equals("Collection")){
			if(!g.equals(""))
				return oclCollection+"<"+g+">";
			return oclCollection;
		}else if(c.getName().equals("Bag")){
			if(!g.equals(""))
				return oclCollection+"<"+g+">";
			return oclCollection;
		}else if(c.getName().equals("Set")){
			if(!g.equals(""))
				return oclSet+"<"+g+">";
			return oclSet;
		}else if(c.getName().equals("Sequence")){
			if(!g.equals(""))
				return oclSequence+"<"+g+">";
			return oclSequence;
		}else if(c.getName().equals("OrderedSet")){
			if(!g.equals(""))
				return oclOrderedSet+"<"+g+">";
			return oclOrderedSet;
		}else if(c.getName().equals("Enumeration")){

		}
		return null;

	}

	private static String definiteCollection(String s){
		if(s.equals("List")){
			return "ArrayList";
		}else if(s.equals("Set")){
			return "HashSet";
		}else if(s.equals("Map")){
			return "HashMap";
		}else{
			return s;
		}
	}

	/**
	 * @param s
	 * @uml.property  name="oclBoolean"
	 */
	public static void setOclBoolean(String s){
		oclBoolean = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclInteger"
	 */
	public static void setOclInteger(String s){
		oclInteger = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclReal"
	 */
	public static void setOclReal(String s){
		oclReal = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclString"
	 */
	public static void setOclString(String s){
		oclString = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclCollection"
	 */
	public static void setOclCollection(String s){
		oclCollection = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclBag"
	 */
	public static void setOclBag(String s){
		oclBag = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclSet"
	 */
	public static void setOclSet(String s){
		oclSet = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclOrderedSet"
	 */
	public static void setOclOrderedSet(String s){
		oclOrderedSet = s;
	}
	/**
	 * @param s
	 * @uml.property  name="oclSequence"
	 */
	public static void setOclSequence(String s){
		oclSequence = s;
	}
}
