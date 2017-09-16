package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IGate;
import com.change_vision.jude.api.inf.model.IInteractionUse;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.model.ITemplateBinding;

/*1つのメソッドの中身を作るクラス*/
/**
 * @author  
 */
public class ProcessBuilder {
	static boolean autoGenerateInstance;

	IClass c;//呼び出し元のメソッドが属するクラス
	ILifeline baselifeline;//operationのターゲット,mのソース
	IOperation operation;//呼び出し元のメソッド
	/**
	 * @uml.property  name="process"
	 */
	String process;
	List<INamedElement> elements;	//ライフラインから読みたい　未成功
	List<IMessage> messages;
	List<Variable> variables;//メソッドローカル変数のリスト
	List<Fragment> fragments;

	ProcessBuilder(){
		messages=new ArrayList<IMessage>();
		variables = new ArrayList<Variable>();
		fragments = new ArrayList<Fragment>();
	}

	ProcessBuilder(IMessage m,ILifeline lifeline){//ライフラインから読みたい　未成功
		operation = m.getOperation();
		baselifeline=lifeline;
		elements=new ArrayList<INamedElement>();
		variables = new ArrayList<Variable>();
		fragments = new ArrayList<Fragment>();
	}

	public void addElement(INamedElement e){//ライフラインから読みたい　未成功
		elements.add(e);
	}


	public void addMessage(IMessage m){
		messages.add(m);
	}

	/**
	 * @param b
	 * @uml.property  name="autoGenerateInstance"
	 */
	public static void setAutoGenerateInstance(boolean b){
		autoGenerateInstance = b;
	}




	public void build(){
		/*全てのメッセージが格納されているとしてメッセージから必要な情報を取得し、proscess以外のインスタンスのフィールドを完成させる
		 * */

		c= baselifeline.getBase();
		if(operation!=null){
			IParameter p[] = operation.getParameters();

			for(int i=0;i<p.length;i++){
				Variable v= new Variable(p[i].getTypeExpression(),p[i].getName());
				variables.add(v);
		}


		}

	}

	public void setFields(List<Field> fieldList){
		for(int i=0;i<fieldList.size();i++){
			Variable v = new Variable(fieldList.get(i).getType(),fieldList.get(i).getName());
			variables.add(v);
		}
	}


	public void processBuild(){
		int fragmentTab=0;//複合フラグメントの数を数えてタブを入れるための変数
		String s="";

		for(int i=0;i<elements.size();i++){
			INamedElement e=elements.get(i);
			IOperation o = null;
			ILifeline l = null;

			if(e instanceof ICombinedFragment){
				Fragment f[] = Fragment.getFragments((ICombinedFragment) e,o,baselifeline);
				for(int j=0;j<f.length;j++){
					if(f[j].isElementExist())
						fragments.add(f[j]);

				}
				/*複合フラグメント用処理 開始判定*/
				for(int j=0;j<fragments.size();j++){
					if(e.equals(fragments.get(j).getFirstElement())){
						s+=addTab(fragmentTab)+"\t\t"+fragments.get(j).toString()+"\r\n";
					fragmentTab += 1;
					}
				}
			}else{

				IMessage m=null;
				if(e instanceof IMessage){
					m= (IMessage)e;

					/*相互作用の利用のための処理 どのメソッドを呼んでいるかを探す*/
					/*シーケンス図のはじめに黒丸から始まるメッセージを入れる←呼ばれるメソッド*/
					//↑を変更　インデックス＝＝１のメッセージ
					if(m.getTarget() instanceof IGate){
						ISequenceDiagram sd =  ((IGate) m.getTarget()).getInteractionUse().getSequenceDiagram();//リンク先のシーケンス図
						IMessage mes[] = sd.getInteraction().getMessages();
						IMessage baseMessage = getBaseMessage(mes);//シーケンス図の基点となるメソッドを取得、このメソッドを呼び出す
						l = (ILifeline) baseMessage.getTarget();
						o = baseMessage.getOperation();
					}else{
						o=((IMessage) m).getOperation();
						l=(ILifeline) m.getTarget();
					}

				}else if(e instanceof IInteractionUse){
					ISequenceDiagram sd =  ((IInteractionUse) e).getSequenceDiagram();//リンク先のシーケンス図

					IMessage mes[] = sd.getInteraction().getMessages();
					m = getBaseMessage(mes);//シーケンス図の基点となるメソッドを取得、このメソッドを呼び出す
					l = (ILifeline) m.getTarget();
					o = m.getOperation();
				}


				/*複合フラグメント用処理 開始判定*/
				for(int j=0;j<fragments.size();j++){
					if(e.equals(fragments.get(j).getFirstElement())){
						s+=addTab(fragmentTab)+"\t\t"+fragments.get(j).toString()+"\r\n";
					fragmentTab += 1;
					}
				}

				if(m.isCreateMessage()){//クリエイトメッセージなら
					String comment = "";
					String params ="";
					if(!m.getArgument().equals("")){
						params=m.getArgument();
					}
					if(!(o==null)){
						comment = "\t//"+o.getName()+" **自動追加したコメント**";
					}

					String lname=l.getName();
					if(lname.equals("")){
						 lname = l.getBase().getName();
						 if(lname.equals("") && isBind(l.getBase())){
							 ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							 if(tb.length>0){
								 lname = tb[0].getTemplate().getName();
							 }
						 }
					}

					if(OclTranslator.isInOclPackage(l.getBase())){//oclパッケージ内のクラスなら変換
						s+=addTab(fragmentTab)+"\t\t"+OclTranslator.typeTranslator(l.getBase())+" "+l.getName()+" = "+"new "+OclTranslator.instanceGenerateTranslator(l.getBase())+"("+params+");"+comment+"\r\n";
					}else if(OclTranslator.isBindOcl(l.getBase())){
						ITemplateBinding tb[] = l.getBase().getTemplateBindings();
						s+=addTab(fragmentTab)+"\t\t"+OclTranslator.typeTranslator(l.getBase(),tb[0])+" "+l.getName()+" = "+"new "+OclTranslator.instanceGenerateTranslator(tb[0].getTemplate(),tb[0])+"("+params+");"+comment+"\r\n";
					}else if(isBind(l.getBase())){							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
						s+=addTab(fragmentTab)+"\t\t"+getBindName(tb[0])+" "+l.getName()+" = "+"new "+getBindName(tb[0])+"("+params+");"+comment+"\r\n";
					}else{
						s+=addTab(fragmentTab)+"\t\t"+l.getBase().getName()+" "+l.getName()+" = "+"new "+l.getBase()+"("+params+");"+comment+"\r\n";
					}
					Variable v = new Variable(l.getBase().getName(),l.getName());
					variables.add(v);

				}
				else{

				String returnType = "";
				String returnName= m.getReturnValueVariable();
				String equal="";
				if(!returnName.equals("")){//返り値変数があれば返り値タイプも表示
					if(!isConstructor(o,m)){//コンストラクタでなければ
						returnType = o.getReturnType().getName();
						if(OclTranslator.isInOclPackage(o.getReturnType())){//oclパッケージ内のクラスなら変換
							returnType = OclTranslator.typeTranslator(o.getReturnType());
						}else if(OclTranslator.isBindOcl(o.getReturnType())){
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							returnType = OclTranslator.typeTranslator(o.getReturnType(),tb[0]);
						}else if(isBind(o.getReturnType())){
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							returnType = getBindName(tb[0]);
						}else{
							returnType = o.getReturnType().getName();
						}

					}else{//コンストラクタで返り血変数あり
						returnType = l.getBase().getName();
						if(OclTranslator.isInOclPackage(l.getBase())){//oclパッケージ内のクラスなら変換
							returnType = OclTranslator.typeTranslator(l.getBase());
						}else if(OclTranslator.isBindOcl(l.getBase())){
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							returnType = OclTranslator.typeTranslator(l.getBase(),tb[0]);
						}else if(isBind(l.getBase())){
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							returnType = getBindName(tb[0]);
						}else{
							returnType = l.getBase().getName();
						}
					}

					if(isExistVariable(returnType,returnName)){
						returnType = "";
						returnName+=" ";
					}else{
						Variable v = new Variable(returnType,returnName);
						variables.add(v);
						returnType+=" ";
						returnName+=" ";
					}
					equal="= ";

				}else{
					if(isConstructor(o,m) && !m.getTarget().getName().equals("")){//返り値変数名なしでコンストラクタ
						returnType = l.getBase().getName();
						if(OclTranslator.isInOclPackage(l.getBase())){//oclパッケージ内のクラスなら変換
							returnType = OclTranslator.typeTranslator(l.getBase());
						}else if(OclTranslator.isBindOcl(l.getBase())){
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							returnType = OclTranslator.typeTranslator(l.getBase(),tb[0]);
						}else if(isBind(l.getBase())){
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							returnType = getBindName(tb[0]);
						}else{
							returnType = l.getBase().getName();
						}
						returnName = m.getTarget().getName();

						if(isExistVariable(returnType,returnName)){
							returnType = "";
							returnName+=" ";
						}else{
							Variable v = new Variable(returnType,returnName);
							variables.add(v);
							returnType+=" ";
							returnName+=" ";
						}
						equal="= ";
					}
				}


				String instanceName=l.getName();
				String lname;
				if(instanceName.equals("")){
					 lname = l.getBase().getName();
					 if(lname.equals("") && isBind(l.getBase())){
						 ITemplateBinding tb[] = l.getBase().getTemplateBindings();
						 if(tb.length>0){
							 lname = tb[0].getTemplate().getName();
						 }
					 }
				}else{
					 lname = instanceName;
				}
				//コンストラクタ、自クラスメソッド、Staticでなければインスタンス自動生成
				//相互作用の利用の場合、indexは1　
				if(!isConstructor(o,m) && !o.isStatic() && (!m.getTarget().equals(m.getSource()) || m.getIndex().equals("1") ) &&  !m.isCreateMessage()){
					if(e instanceof IInteractionUse && !c.equals(l.getBase())){//相互作用の利用でライフラインのベースクラスがちがければ
						Variable v = new Variable(l.getBase().getName(),instanceName);
						variables.add(v);
						String ltype=l.getBase().getName();
						String ltype2=l.getBase().getName();
						if(OclTranslator.isInOclPackage(l.getBase())){//oclパッケージ内のクラスなら変換
							ltype = OclTranslator.typeTranslator(l.getBase());
							ltype2= OclTranslator.instanceGenerateTranslator(l.getBase());
						}else if(OclTranslator.isBindOcl(l.getBase())){//oclをバインドしてれば
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							ltype = OclTranslator.typeTranslator(l.getBase(),tb[0]);
							ltype2= OclTranslator.instanceGenerateTranslator(tb[0].getTemplate(),tb[0]);
						}else if(isBind(l.getBase())){//バインドしてれば
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							ltype = getBindName(tb[0]);
							ltype2 = ltype;
						}

						if(autoGenerateInstance){
							s+=addTab(fragmentTab)+"\t\t"+ltype+" "+lname+" = "+"new "+ltype2+"();"+"\t//インスタンス生成文を自動追加 **自動追加したコメント**"+"\r\n";//インスタンス生成文とコメントを追加
						}else{
							s+=addTab(fragmentTab)+"\t\t//インスタンス "+lname+" は作成されていません **自動追加したコメント**\r\n";
						}

					}else if(!isExistVariable(l.getBase().getName(),instanceName) && !(e instanceof IInteractionUse)){//インスタンスが無ければ
						Variable v = new Variable(l.getBase().getName(),instanceName);
						variables.add(v);
						String ltype=l.getBase().getName();
						String ltype2=l.getBase().getName();
						if(OclTranslator.isInOclPackage(l.getBase())){//oclパッケージ内のクラスなら変換
							ltype = OclTranslator.typeTranslator(l.getBase());
							ltype2= OclTranslator.instanceGenerateTranslator(l.getBase());
						}else if(OclTranslator.isBindOcl(l.getBase())){//oclをバインドしてれば
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							ltype = OclTranslator.typeTranslator(l.getBase(),tb[0]);
							ltype2= OclTranslator.instanceGenerateTranslator(tb[0].getTemplate(),tb[0]);
						}else if(isBind(l.getBase())){//バインドしてれば
							ITemplateBinding tb[] = l.getBase().getTemplateBindings();
							ltype = getBindName(tb[0]);
							ltype2 = ltype;
						}

						if(autoGenerateInstance){
							s+=addTab(fragmentTab)+"\t\t"+ltype+" "+lname+" = "+"new "+ltype2+"();"+"\t//インスタンス生成文を自動追加 **自動追加したコメント**"+"\r\n";//インスタンス生成文とコメントを追加
						}else{
							s+=addTab(fragmentTab)+"\t\t//インスタンス "+lname+" は作成されていません **自動追加したコメント**\r\n";
						}
					}

				}

				//クリエイトメッセージならインスタンスを生成 ←変更しました、現状到達不可能
				if(m.isCreateMessage()){
					Variable v = new Variable(l.getBase().getName(),instanceName);
					variables.add(v);

					String ltype=l.getBase().getName();
					String ltype2=l.getBase().getName();
					if(OclTranslator.isInOclPackage(l.getBase())){//oclパッケージ内のクラスなら変換
						ltype = OclTranslator.typeTranslator(l.getBase());
						ltype2= OclTranslator.instanceGenerateTranslator(l.getBase());
					}else if(OclTranslator.isBindOcl(l.getBase())){//oclをバインドしてれば
						ITemplateBinding tb[] = l.getBase().getTemplateBindings();
						ltype = OclTranslator.typeTranslator(l.getBase(),tb[0]);
						ltype2= OclTranslator.instanceGenerateTranslator(tb[0].getTemplate(),tb[0]);
					}else if(isBind(l.getBase())){//バインドしてれば
						ITemplateBinding tb[] = l.getBase().getTemplateBindings();
						ltype = getBindName(tb[0]);
						ltype2 = ltype;
					}


					if(!isConstructor(o,m))
						s+=addTab(fragmentTab)+"\t\t"+ltype+" "+lname+" = "+"new "+ltype2+"();\r\n";//インスタンス生成文を追加
				}


				if(instanceName.equals("") || o.isStatic()){
					instanceName=l.getBase().getName();
				}
				instanceName+=".";
				if(isConstructor(o,m))instanceName="";
				if(m.getTarget().equals(m.getSource()) && !(e instanceof IInteractionUse)){
					instanceName="this.";
				}else if(e instanceof IInteractionUse && c.equals(l.getBase())){
					instanceName="this.";
				}

				String methodName="";
				if(isConstructor(o,m)){
					methodName="new "+l.getBase().getName();
				}else{
					methodName += o.getName();
				}
				String params ="";
				if(!m.getArgument().equals("")){
					params=m.getArgument();
				}else {
					params ="";
					/*
					IParameter p[] = o.getParameters();//引数が無ければメソッドの引数を自動で入れる←考え直し！
					for(int j=0;j<p.length;j++){
						if(j!=0)params += ",";
						params += p[j].getName();
					}
					*/
				}

				s += addTab(fragmentTab)+"\t\t"+returnType+returnName+equal+instanceName+methodName+"("+params+");"+"\r\n";
				}

				/*複合フラグメント用処理　括弧閉じ判定*/
				for(int j=0;j<fragments.size();j++){
					if(!(fragments.get(j).getLastElement()==null) && e.equals(fragments.get(j).getLastElement())){//nullのとき文追加
						fragmentTab -= 1;
						s+=addTab(fragmentTab)+"\t\t}\r\n";
					}
				}


			}
		}

		process=s;



	}

	private boolean isBind(IClass ic){
		if(ic.getTemplateBindings().length>0){
			return true;
		}else{
			return false;
		}
	}

	private String getBindName(ITemplateBinding tb){
		String g = "";
		if(tb.getActualMap()!=null){
			String s = tb.getActualMap().toString();
			String ss[]=s.split("=", 0);
			g=ss[1].replace("}", "");//マップからジェネリクスを抜き取る

		}
		if(!g.equals("")){
			return tb.getTemplate().getName()+"<"+g+">";
		}else{
			return tb.getTemplate().getName();
		}
	}

	private IMessage getBaseMessage(IMessage[] mes) {
		IMessage m;
		for(int i=0;;i++){
			String index = mes[i].getIndex();
			if(index!=null && index.equals("1")){
				m=mes[i];
				break;
			}
		}
		return m;
	}

	private String addTab(int fragmentTab){
		String s="";
		for(int j=0;j<fragmentTab;j++){
			s+="\t";
		}
		return s;
	}

	public boolean isExistVariable(String type,String name){
		for(int i=0;i<variables.size();i++){
			if(variables.get(i).isSame(type,name)){
				return true;
			}
		}
		return false;
	}

	private boolean isCreate(IMessage m){
		String stereo[] =m.getStereotypes();
		for(int i=0;i<stereo.length;i++){
			if(stereo[i].equals("create")){
				return true;
			}
		}
		return false;
	}

	private boolean isConstructor(IOperation o,IMessage m){
		if(o!=null && o.getName().equals(((ILifeline) m.getTarget()).getBase().getName())){
			return true;
		}else{
			return false;
		}
	}


	/**
	 * @return
	 * @uml.property  name="process"
	 */
	public String getProcess(){
		return process;
	}

	public IOperation getCallOperation(){
		return operation;
	}

	public IClass getIClass(){
		return c;
	}

	public ILifeline getBaselifeline() {
		return baselifeline;
	}

}
