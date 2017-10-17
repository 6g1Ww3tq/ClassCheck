package com.classcheck.autosource;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IMultiplicityRange;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.ITemplateBinding;

/**
 * @author  
 */
public class Field {
	static String multiTranslate;
	IAttribute attribute;
	String definition;
	String visibility;
	/**
	 * @uml.property  name="type"
	 */
	String type;
	/**
	 * @uml.property  name="name"
	 */
	String name;
	String isStatic;
	String isFinal;
	String initial;//初期値
	boolean isMulti;//多重度2以上または集約
	boolean isOcl;


	Field(IAttribute a){
		attribute=a;
		isOcl=false;

		//定義
		if(!a.getDefinition().equals("")){
			String s[] = a.getDefinition().split("\\n",0);
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


		if(a.isPublicVisibility()){
			visibility="public ";
		}else if(a.isPackageVisibility()){
			visibility="";
		}else if(a.isProtectedVisibility()){
			visibility="protected ";
		}else if(a.isPrivateVisibility()){
			visibility="private ";
		}else{
			visibility="";
		}

		type= a.getTypeExpression();
		if(a.getType().getContainer() instanceof IPackage){//oclパッケージ内のクラスなら変換
			IPackage p = (IPackage) a.getType().getContainer();
			if(OclTranslator.isInOclPackage(a.getType())){
				isOcl=true;
				type=OclTranslator.typeTranslator(a.getType());
			}else if(OclTranslator.isBindOcl(a.getType())){
				isOcl=true;
				ITemplateBinding tb[] = a.getType().getTemplateBindings();
				type = OclTranslator.typeTranslator(a.getType(), tb[0]);
			}
		}

		name= a.getName();
		if(name.equals("")){
			name = a.getType().getName();
		}
		if(a.isStatic()){
			isStatic="static ";
		}else{
			isStatic="";
		}
		if(!a.isChangeable()){
			isFinal ="final ";
		}else{
			isFinal="";
		}

		if(!a.getInitialValue().equals("")){
			initial = " = "+a.getInitialValue();
		}else{
			initial="";
		}

		isMulti = isMultiplicity(a);
	}

	/**
	 * @param s
	 * @uml.property  name="multiTranslate"
	 */
	public static void setMultiTranslate(String s){
		multiTranslate = s;
	}



	private boolean isMultiplicity(IAttribute ia){
		IAssociation association =ia.getAssociation();
		if(ia.getAssociation()!=null){//関連があれば
			IAttribute attributes[] = association.getMemberEnds();
			if(attributes[0].equals(ia)){//関連の反対側の関連端が集約化調べる
				if(attributes[1].isAggregate()){
					return true;
				}
			}else if(attributes[1].equals(ia)){
				if(attributes[0].isAggregate()){
					return true;
				}
			}
		}

		IMultiplicityRange[] m = ia.getMultiplicity();
		for(int i=0;i<m.length;i++){
			if(m[i].getUpper()>1 || m[i].getUpper()==IMultiplicityRange.UNLIMITED){
				return true;
			}
		}

		return false;
	}


	public String toString(){
		String s=definition;
		if(multiTranslate.equals("配列") || multiTranslate.equals("なし") || multiTranslate.equals("Array") || multiTranslate.equals("none")){
			s+="\t";
			s+=visibility;
			s+=isStatic;
			s+=isFinal;
			s+=type;
			if(isMulti && (multiTranslate.equals("配列") || multiTranslate.equals("Array")))
				s+="[]";
			s+=" "+name;
			s+=initial;
			s+=";";
			return s;
		}else if(multiTranslate.equals("List") ||multiTranslate.equals("Set") || multiTranslate.equals("Map")){
			s+="\t";
			s+=visibility;
			s+=isStatic;
			s+=isFinal;
			if(isOcl){
				s+=type;
			}else if(isMulti && multiTranslate.equals("List")){
				s+="List<"+type+">";
			}else if(isMulti && multiTranslate.equals("Set")){
				s+="Set<"+type+">";
			}else if(isMulti && multiTranslate.equals("Map")){
				s+="Map<"+type+">";
			}else{
				s+=type;
			}
			s+=" "+name;
			s+=initial;
			s+=";";
			return s;
		}
		return "";
	}

	public String interfaceString(){
		String s=definition;
		s+="\t";
		s+=type+" ";
		s+=name;
		s+=initial;
		s+=";";
		return s;
	}

	/**
	 * @return
	 * @uml.property  name="type"
	 */
	public String getType(){
		return type;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName(){
		return name;
	}
	
	public IAttribute getAttribute() {
		return attribute;
	}
	
	public String getDefinition() {
		return definition;
	}
	
	public String getVisibility() {
		return visibility;
	}

}
