package com.classcheck.autosouce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInteractionOperand;
import com.change_vision.jude.api.inf.model.IInteractionUse;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;

public class SourceGenerator {

	JFrame window;
	ProjectAccessor projectAccessor;

	public SourceGenerator(){
		AstahAPI api;
		try {
			api = AstahAPI.getAstahAPI();
			projectAccessor = api.getProjectAccessor();
			window = api.getViewManager().getMainFrame();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvalidUsingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public SourceGenerator(ProjectAccessor projectAccessor){
		AstahAPI api;
		try {
			api = AstahAPI.getAstahAPI();
			this.projectAccessor = projectAccessor;
			window = api.getViewManager().getMainFrame();
		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvalidUsingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public static List<IClass> getClassList(){
		AstahAPI api;
		try {
			api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			IModel imodel = projectAccessor.getProject();
			List<IClass> classList=new ArrayList<IClass>();
			getAllClasses(imodel,classList);
			return classList;

		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static List<ISequenceDiagram> getSequenceDiagramList(){
		AstahAPI api;
		try {
			api = AstahAPI.getAstahAPI();
			ProjectAccessor projectAccessor = api.getProjectAccessor();
			IModel imodel = projectAccessor.getProject();
			// IDiagram[] diagrams =imodel.getDiagrams();
			List<ISequenceDiagram> diagramList = new ArrayList<ISequenceDiagram>();
			getAllSequences(imodel,diagramList);
			/* for(int i=0;i<diagrams.length;i++){
		        	if(diagrams[i] instanceof ISequenceDiagram){
		        		diagramList.add((ISequenceDiagram) diagrams[i]);
		        	}
			 }*/

			return diagramList;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		}
		return null;

	}

	private  static void getAllSequences(INamedElement element, List<ISequenceDiagram> diagramList) {
		if (element instanceof IPackage) {
			for(INamedElement ownedNamedElement : ((IPackage)element).getOwnedElements()) {//パッケージ内のモデル要素のみ取得（図は含まない）
				getAllSequences(ownedNamedElement, diagramList);
			}
			for(IDiagram diagrams : element.getDiagrams()){//パッケージ内の図要素を取得
				getAllSequences(diagrams,diagramList);
			}
		} else if (element instanceof ISequenceDiagram) {
			diagramList.add((ISequenceDiagram)element);

		}else if( element instanceof IUseCase){
			for(IDiagram diagrams :((IUseCase)element).getDiagrams()){
				getAllSequences(diagrams,diagramList);
			}
		}
	}

	/*
	 * シーケンス図を読み取る!!!!
	 */
	public  List<ProcessBuilder> run(List<IClass> classList, List<ISequenceDiagram> diagramList) throws UnExpectedException {
		List<ProcessBuilder> methodList = null;
		try {
			DiagramChecker dc = new DiagramChecker();
			ClassBuilder cb =new ClassBuilder();
			for(int i=0;i<classList.size();i++){

				IClass c= classList.get(i);
				dc.classCheck(c);
				cb.addClass(c);

			}
			/*ここまででクラスからスケルトンができる*/
			/*シーケンス図が適切かを判別する*/

			for(int i=0;i<diagramList.size();i++){
				if(diagramList.get(i) instanceof ISequenceDiagram){
					dc.sequenceCheck((ISequenceDiagram) diagramList.get(i));
				}
			}
			if(dc.getInadequateSize()!=0){
				JOptionPane.showMessageDialog(window.getParent(),"モデルを修正してください");
				dc.showIndequateDialog();
			}else{
				/*ここからシーケンスを読む*/
				for(int i=0;i<diagramList.size();i++){

					//ライフラインから読みたい
					ILifeline l[] =((ISequenceDiagram) diagramList.get(i)).getInteraction().getLifelines();
					methodList = new ArrayList<ProcessBuilder>();
					organizeElement(l,methodList);
					cb.addProcess(methodList,diagramList.get(i).getName());


				}


				//JOptionPane.showMessageDialog(window.getParent(),"ok");

			/*
	        if(output(cb,projectAccessor)){
	        	JOptionPane.showMessageDialog(window.getParent(),"OUTPUT_SUCCESS");
	        }else{
	        	JOptionPane.showMessageDialog(window.getParent(),"OUTPUT_FAILURE");

	        }
			 */

			}







		} catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.", "Alert", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		return methodList;

	}




	//ライフラインから読みたい
	private void organizeElement(ILifeline[] l ,List<ProcessBuilder> methodlist){
		ProcessBuilder mc=null;

		//l[0]のはじめの要素はactivatorにしたいため、入れ替え
		for(int i= 1;i<l.length;i++){
			INamedElement e[] = l[0].getFragments();
			if(e.length>0 && !(e[0] instanceof IMessage)){
				ILifeline tmp = l[0];
				l[0]=l[i];
				l[i]=tmp;
			}else{
				break;
			}
		}

		for(int i=0;i<l.length;i++){
			INamedElement elements[] = l[i].getFragments();
			IMessage activator;

			//自分に戻るメッセージは2重で取得されるため1つ削除
			for(int j=0;j<elements.length;j++){
				for(int k=0;k<elements.length;k++){
					if(j!=k && elements[j]!=null && elements[j].equals(elements[k])){
						elements[k] = null;
					}
				}
			}

			/*ILifelife.getFragments()ではフラグメント内のメッセージは取得できないが、
			 * 内部メソッドだけはフラグメント内似合っても取れてしまうため、
			 * フラグメント内を検索し、同じ要素が見つかればILifelife.getFragments()で
			 * 取れてしまった要素はelementsから0削除する*/
			for(int j = 0;j<elements.length;j++){
				if(elements[j] instanceof ICombinedFragment){
					ICombinedFragment cf=(ICombinedFragment) elements[j];

					for(int k=0;k<elements.length;k++){
						if(elements[k] instanceof IMessage && l[i].equals(((IMessage) elements[k]).getSource()) && l[i].equals(((IMessage) elements[k]).getTarget())){
							boolean duplicate = isDuplicate(cf,(IMessage) elements[k],l[i]);
							if(duplicate){
								elements[k] = null;
							}
						}
					}
				}
			}

			//一つ目の要素が複合フラグメントなら中の要素をelementに入れる
			if(elements.length>0 && elements[0] instanceof ICombinedFragment){
				IInteractionOperand[] o = ((ICombinedFragment) elements[0]).getInteractionOperands();
				List<INamedElement> list = new ArrayList<INamedElement>();
				for(int j= 0; j<o.length;j++){
					INamedElement e[] = l[i].getFragments(o[j]);
					for(int k=0;k<e.length;k++){
						list.add(e[k]);
					}
				}
				elements = (INamedElement[])list.toArray(new INamedElement[0]);
			}

			//判別してリストに追加していく
			for(int j=0;j<elements.length;j++){
				//if(elements[j] instanceof IMessage && l[i].equals(((IMessage) elements[j]).getTarget()) && !l[i].equals(((IMessage) elements[j]).getSource()) && !((IMessage) elements[j]).isReturnMessage()){	//メッセージのターゲットがl[i]ならば
				if(elements[j] instanceof IMessage && !((IMessage) elements[j]).isReturnMessage() && l[i].equals(((IMessage) elements[j]).getTarget()) && !(((IMessage) elements[j]).getIndex()==null) && (((IMessage) elements[j]).getIndex().equals("1") ||!l[i].equals(((IMessage) elements[j]).getSource())) ){
					//起動メッセージなら
					if(!(mc==null)){//今あるmcをリストに入れてから新しいmcをつくる
						mc.build();
						methodlist.add(mc);
					}
					activator = (IMessage) elements[j];
					mc= new ProcessBuilder(activator,l[i]);//l[i]はactivatorのターゲット

				}else if(elements[j] instanceof ICombinedFragment){
					mc=organizeFragment(mc,(ICombinedFragment)elements[j],l[i],methodlist);
				}else if(elements[j] instanceof IMessage ){//returnメッセージでなければ
					if(l[i].equals(((IMessage) elements[j]).getSource()) && !((IMessage) elements[j]).isReturnMessage() && !((IMessage) elements[j]).isDestroyMessage() && !(((IMessage) elements[j]).getIndex()==null))//デストロイ、リターン、インデックスなしは無視
						mc.addElement(elements[j]);
				}else if(elements[j] instanceof IInteractionUse){
					mc.addElement(elements[j]);
				}
			}

			if(i==l.length-1){//一番最後のmcをリストに追加
				mc.build();
				methodlist.add(mc);
			}
		}
	}



	private ProcessBuilder organizeFragment(ProcessBuilder mc,ICombinedFragment f,ILifeline l,List<ProcessBuilder> methodlist){//再帰呼び出しする　フラグメントが複数あった場合のため
		mc.addElement(f);
		IInteractionOperand o[] =f.getInteractionOperands();
		for(int i=0;i<o.length;i++){
			if(checkExistLifeLine(o[i].getLifelines(),l)){//フラグメントが複数あるとライフラインが含まれない可能性かあるため
				INamedElement e [] = l.getFragments(o[i]);
				//フラグメント内の自クラス内のメソッドを呼ぶメッセージは2重で取得されるため、同じメッセージは削除する
				for(int j=0;j<e.length;j++){
					for(int k=0;k<e.length;k++){
						if(j!=k && e[j]!=null && e[j].equals(e[k])){
							e[k] = null;
						}
					}
				}
				for(int j=0;j<e.length;j++){
					if(e[j] instanceof IMessage && l.equals(((IMessage) e[j]).getSource()) && !((IMessage) e[j]).isReturnMessage()){
						mc.addElement(e[j]);
					}else if(e[j] instanceof IMessage && !l.equals(((IMessage) e[j]).getSource()) && l.equals(((IMessage) e[j]).getTarget()) && !((IMessage) e[j]).isReturnMessage()){
						mc.build();
						methodlist.add(mc);//これまでのmcをリストに追加
						IMessage activator = (IMessage) e[j];
						mc= new ProcessBuilder(activator,l);
					}else if(e[j] instanceof IInteractionUse){
						mc.addElement(e[j]);
					}else if(e[j] instanceof ICombinedFragment){
						organizeFragment(mc,(ICombinedFragment) e[j],l,methodlist);
					}
				}
			}
		}

		return mc;
	}

	//複合フラグメント内にmがあればtrueを返す
	private boolean isDuplicate(ICombinedFragment f,IMessage m,ILifeline l){
		IInteractionOperand o[] = f.getInteractionOperands();
		for(int i=0;i<o.length;i++){
			if(checkExistLifeLine(o[i].getLifelines(),l)){//フラグメントが複数あるとライフラインが含まれない可能性かあるため
				INamedElement e[] = l.getFragments(o[i]);
				for(int j=0;j<e.length;j++){
					if(e[j] instanceof IMessage && e[j].equals(m)){
						return true;
					}else if(e[j] instanceof ICombinedFragment){
						if(isDuplicate((ICombinedFragment) e[j],m,l)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean checkExistLifeLine(ILifeline[] ls,ILifeline l){
		for(int i=0;i<ls.length;i++){
			if(ls[i].equals(l)){
				return true;
			}
		}
		return false;
	}

	private  static void getAllClasses(INamedElement element, List<IClass> classList)
			throws ClassNotFoundException, ProjectNotFoundException {
		if (element instanceof IPackage) {
			if(!element.getName().equals("java") && !element.getName().equals("ocl")){
				for(INamedElement ownedNamedElement : ((IPackage)element).getOwnedElements()) {
					getAllClasses(ownedNamedElement, classList);
				}
			}
		} else if (element instanceof IClass) {
			if(!(element instanceof IUseCase) && !isActor((IClass) element))
				classList.add((IClass)element);
			for(IClass nestedClasses : ((IClass)element).getNestedClasses()) {
				getAllClasses(nestedClasses, classList);
			}
		}
	}

	//クラスのステレオタイプにactorがあるかを判別するメソッド
	private static boolean isActor(IClass c){
		String s[] = c.getStereotypes();
		for(int i=0;i<s.length;i++){
			if(s[i].equals("actor")){
				return true;
			}
		}
		return false;
	}

	private boolean output(ClassBuilder cb,ProjectAccessor projectAccessor){
		try{
			String path = "";
			String[] paths = projectAccessor.getProjectPath().split("\\\\");
			for(int i=0;i<paths.length-1;i++){
				path += paths[i]+"\\\\";
			}

			for(int i=0;i<cb.getclasslistsize();i++){
				cb.getClass(i);
				//	File file = new File("C:\\Users\\1016y\\Documents\\astah_plugin\\AutoSouce\\output\\"+cb.getClass(i).getName()+".java");
				//File file =new File("D:\\program\\workspace\\astah_plugin\\AutoSouce\\output\\"+cb.getClass(i).getName()+".java");


				File file = new File(path+"\\"+cb.getClass(i).getName()+".java");

				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

				pw.println(cb.getClass(i).toString());

				pw.close();


			}
			return true;
		}catch(IOException e){

			return false;
		} catch (ProjectNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}
	}

}
