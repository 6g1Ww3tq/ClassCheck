package com.classcheck.autosouce;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IGeneralization;
import com.change_vision.jude.api.inf.model.IInteraction;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

public class DiagramChecker {
	List<Inadequate> inadequateList;

	DiagramChecker(){
		inadequateList = new ArrayList<Inadequate>();
	}

	public void sequenceCheck(ISequenceDiagram sd){
		IInteraction interaction=sd.getInteraction();
		ILifeline l[] = interaction.getLifelines();
		IMessage m[] = interaction.getMessages();
		lifelineCheck(l,sd);
		messageCheck(m,sd);
	}

	private void lifelineCheck(ILifeline[] l,ISequenceDiagram sd){
		for(int i=0;i<l.length;i++){
			if(l[i].getBase()==null){
				Inadequate inadequate = new Inadequate(sd,l[i],"ライフラインにクラスが設定されていません");
				inadequateList.add(inadequate);
			}
		}
	}

	private void messageCheck(IMessage[] m,ISequenceDiagram sd){
		IMessage first = seachMessageIndex(m,"1");
		if(!first.getTarget().equals(first.getSource())){
			Inadequate inadequate = new Inadequate(sd,null,"シーケンス図の起点となるメッセージがありません");
			inadequateList.add(inadequate);
		}
		for(int i=0;i<m.length;i++){
			if(m[i].getOperation()==null && !m[i].isReturnMessage() && !m[i].isCreateMessage() && !m[i].isDestroyMessage() && !(m[i].getIndex()==null)){
				Inadequate inadequate = new Inadequate(sd,m[i],"メッセージに操作が設定されていません");
				inadequateList.add(inadequate);
			}
			if(!(m[i].getTarget() instanceof ILifeline)){
				Inadequate inadequate = new Inadequate(sd,m[i],"メッセージの送信先が不正です");
				inadequateList.add(inadequate);
			}
			if(!m[i].equals(first) && m[i].getSource()==null){
				Inadequate inadequate = new Inadequate(sd,m[i],"メッセージの送信元が不正です");
				inadequateList.add(inadequate);
			}
		}
	}

	private IMessage seachMessageIndex(IMessage[] m,String index){
		for(int i=0;i<m.length;i++){
			if(m[i].getIndex()==null){

			}else if(m[i].getIndex().equals(index))
				return m[i];
		}
		return null;
	}

	public void classCheck(IClass c){
		IGeneralization g[] = c.getGeneralizations();
		if(g.length>0){
			pluralGeneralization(g,c);
			rightFormGeneralization(g,c);
		}
		IAttribute a[] = c.getAttributes();
		interfaceInitial(a,c);
	}
	private void pluralGeneralization(IGeneralization g[],IClass c){
		if(g.length>1){
			Inadequate inadequate = new Inadequate(c,null,"一つのクラスに複数の汎化を利用することはできません");
			inadequateList.add(inadequate);
		}
	}

	private void rightFormGeneralization(IGeneralization g[], IClass c){
		if(!isInterface(c)){
			if(isInterface(g[0].getSuperType())){
				Inadequate inadequate = new Inadequate(c,g[0].getSuperType(),"インターフェイスの実装には実現を使ってください");
				inadequateList.add(inadequate);
			}
		}
	}

	private void interfaceInitial(IAttribute a[],IClass c){
		if(isInterface(c)){
			 for(int i=0;i<a.length;i++){
				 if(a[i].getInitialValue().equals("")){
					 Inadequate inadequate = new Inadequate(c,a[i],"インターフェイスの属性に初期値を設定してください");
					 inadequateList.add(inadequate);
				 }
			 }
		}
	}

	private boolean isInterface(IClass c){
		String type[] = c.getStereotypes();
		for(int i=0;i<type.length;i++){
			if(type[i].equals("interface")){
				return true;
			}
		}
		return false;
	}

	public int getInadequateSize(){
		return inadequateList.size();
	}

	public String toString(){
		String s = "以下のモデルを修正してください\n";
		for(int i=0;i<inadequateList.size();i++){
			s += inadequateList.get(i).toString() + "\n";
		}
		return s;
	}

	public void showIndequateDialog(){
		JFrame frame = new JFrame();
		frame.setSize(480, 270);
		frame.setTitle("モデルを修正してください");
		frame.setLayout(new BorderLayout());
		String tableName[] = {"SequenceDiagram","Element","Problem"};

		DefaultTableModel tableModel = new DefaultTableModel(tableName,0);

		JTable table = new JTable(tableModel);

		for(int i=0;getInadequateSize()>i;i++){
			tableModel.addRow(inadequateList.get(i).toArray());
		}
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);

		frame.add(scrollPane,BorderLayout.CENTER);
		frame.setVisible(true);

	}
}
