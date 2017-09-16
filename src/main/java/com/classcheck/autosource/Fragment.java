package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IInteractionOperand;
import com.change_vision.jude.api.inf.model.IInteractionUse;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;

/**
 * @author  
 */
public class Fragment {
	ICombinedFragment fragment;
	List<INamedElement> elements;
	String type;
	IOperation activator;//起動メッセージのメソッド
	ILifeline lifeline;
	String guard;



	Fragment(ICombinedFragment f,IOperation o,int operand,ILifeline l){//oはMessageChangeで今作ってるメソッド
		fragment = f;
		elements=new ArrayList<INamedElement>();
		lifeline = l;
		build(o,operand);

	}

	public static Fragment[] getFragments(ICombinedFragment f,IOperation o,ILifeline l){
		IInteractionOperand operands[]=f.getInteractionOperands();


		Fragment fragments[] = new Fragment[operands.length];
		for(int i=0;i<operands.length;i++){
			Fragment fragment = new Fragment(f,o,i,l);
			fragments[i]=fragment;
		}
		return fragments;
	}

	private void build(IOperation op,int operand){
		IInteractionOperand operands[]=fragment.getInteractionOperands();
		if(checkExistLifeLine(operands[operands.length-1].getLifelines(),lifeline)){//フラグメントが複数あるとライフラインが含まれない可能性かあるため
			INamedElement e[] = lifeline.getFragments(operands[operand]);//mikanknsei
			guard = operands[operand].getGuard();
			//if(m!=null && m[0].getActivator()!=null)
			//activator=m[0].getActivator().getOperation();
			if(fragment.isAlt()){
				if(operand==0){
					type = "if";
				}else if(operand==operands.length-1 && guard == ""){
					type = "else";
				}else{
					type = "else if";
				}
			}else if(fragment.isLoop()){
				type = "while";
			}else{
				type = "fragment";
			}

			for(int i=0;i<e.length;i++){
				if(e[i] instanceof IMessage){
					if(lifeline.equals(((IMessage) e[i]).getSource())){
						elements.add(e[i]);
					}

				}else if(e[i] instanceof IInteractionUse || e[i] instanceof ICombinedFragment){
					elements.add(e[i]);
				}
			}
		}
	}


	/**
	 * @param t
	 * @uml.property  name="type"
	 */
	public void setType(String t){
		type = t;
	}

	public boolean isElementExist(){
		if(elements.size()==0){
			return false;
		}else{
			return true;
		}
	}

	public INamedElement getFirstElement(){
		if(elements.size()==0){
			return null;
		}
		return elements.get(0);
	}

	public INamedElement getLastElement(){
		int last=elements.size()-1;
		if(elements.size()==0){
			return null;
		}else if(elements.get(last) instanceof ICombinedFragment){
			return getFragmentLastElement((ICombinedFragment) elements.get(last));
		}else{
			return elements.get(last);
		}
	}

	private INamedElement getFragmentLastElement(ICombinedFragment f){
		IInteractionOperand operands[]=f.getInteractionOperands();
		if(checkExistLifeLine(operands[operands.length-1].getLifelines(),lifeline)){//フラグメントが複数あるとライフラインが含まれない可能性かあるため
			INamedElement e[] = lifeline.getFragments(operands[operands.length-1]);
			int esize = e.length-1;//e.length-1なんだけどe.length==0の時バグるから作成
			if(esize!=-1 && e[esize] instanceof ICombinedFragment){
				return getFragmentLastElement((ICombinedFragment) e[esize]);
			}else if(esize!=-1){
				return e[esize];
			}
		}
		return null;
	}

	private boolean checkExistLifeLine(ILifeline[] ls,ILifeline l){
		for(int i=0;i<ls.length;i++){
			if(ls[i].equals(l)){
				return true;
			}
		}
		return false;
	}


	public String toString(){
		String s;
		if(type.equals("else")){
			s = type + "{";
		}else{
			s = type + "(" + guard + "){";
		}
		return s;
	}

}

