package com.classcheck.autosource;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IInteraction;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.classcheck.gen.DisassemblyMethodSignature;

public class SequenceSearcher {

	private List<ISequenceDiagram> diagramList;
	private String color;
	private ArrayList<IPresentation> changedMessageList;
	private static String defaultColor = "#000000";

	public SequenceSearcher(List<ISequenceDiagram> allISequenceDiagram, String color) {
		this.diagramList = allISequenceDiagram;
		this.color = color;
		this.changedMessageList = new ArrayList<IPresentation>();
	}

	public static String getDefaultColor() {
		return defaultColor;
	}

	public List<ISequenceDiagram> findFieldFromISequenceDiagram(String targetClassName,
			JLabel fieldLabel) {
		List<ISequenceDiagram> foundDiagramList = new ArrayList<ISequenceDiagram>(); 
		ISequenceDiagram sequenceDiagram;
		String fieldLabel_str = fieldLabel.getText();
		String[] split = fieldLabel_str.split(" ");
		String fieldClassName = split[split.length-2];
		String fieldVarName = split[split.length-1];

		for(int i_diagramList=0;i_diagramList<diagramList.size();i_diagramList++){
			//ライフライン＝＞　変数名(何でも):型（targetClassName)　に当てはまれば変数名を一時的に記録する
			sequenceDiagram = diagramList.get(i_diagramList);

			System.out.println("@@"+sequenceDiagram.getName()+"@@");

			IInteraction interaction = sequenceDiagram.getInteraction();
			ILifeline[] lifeLines = interaction.getLifelines();
			for(int i_lifeLines=0;i_lifeLines<lifeLines.length;i_lifeLines++){
				ILifeline lifeLine = lifeLines[i_lifeLines];
				String lifeLineName = lifeLine.getName();
				String baseName = lifeLine.getBase().getName();

				//ターゲットのクラス名を発見する
				if (baseName.equals(fieldClassName)) {

					//クリックした変数名と同じ名前
					if (lifeLineName.equals(fieldVarName)) {
						//ターゲットのクラス名の変数名を記録する(複数可能)
						try {
							IPresentation[] presentations = lifeLine.getPresentations();

							for (IPresentation iPresentation : presentations) {
								System.out.println("presentation:"+iPresentation);
								iPresentation.setProperty("font.color", color);
								changedMessageList.add(iPresentation);
							}

							if (foundDiagramList.contains(sequenceDiagram) == false) {
								foundDiagramList.add(sequenceDiagram);
							}
						} catch (InvalidUsingException e) {
							e.printStackTrace();
						} catch (InvalidEditingException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}

		return foundDiagramList;
	}

	public List<ISequenceDiagram> findMethodFromISequenceDiagram(String targetClassName,
			JLabel methodLabel) {
		String labelMethodName = from_Label_methodName(methodLabel);
		List<ISequenceDiagram> foundDiagramList = new ArrayList<ISequenceDiagram>(); 
		ISequenceDiagram sequenceDiagram;

		for(int i_diagramList=0;i_diagramList<diagramList.size();i_diagramList++){
			//ライフライン＝＞　変数名(何でも):型（targetClassName)　に当てはまれば変数名を一時的に記録する
			List<String> instanceVarNameList = new ArrayList<String>();
			boolean classFound = false;
			sequenceDiagram = diagramList.get(i_diagramList);

			System.out.println("@@"+sequenceDiagram.getName()+"@@");

			IInteraction interaction = sequenceDiagram.getInteraction();
			ILifeline[] lifeLines = interaction.getLifelines();
			for(int i_lifeLines=0;i_lifeLines<lifeLines.length;i_lifeLines++){
				ILifeline lifeLine = lifeLines[i_lifeLines];

				//ターゲットのクラス名を発見する
				if (lifeLine.getBase().getName().equals(targetClassName)) {
					classFound = true;
					//ターゲットのクラス名の変数名を記録する(複数可能)
					instanceVarNameList.add(lifeLine.getName());
				}
			}

			if (classFound == false) {
				continue ;
			}

			IMessage[] messages = interaction.getMessages();
			for(int i_messages=0;i_messages<messages.length;i_messages++){
				IMessage message = messages[i_messages];
				System.out.println("message:"+message);
				String messageName = message.getName();
				INamedElement element = message.getTarget();
				String varName = element.getName();

				if (instanceVarNameList.contains(varName) == false) {
					continue;
				}

				if (labelMethodName.equals(messageName) == false) {
					continue;
				}

				System.out.println("target:"+element);
				try {
					IPresentation[] presentations = message.getPresentations();

					for (IPresentation iPresentation : presentations) {
						System.out.println("presentation:"+iPresentation);
						iPresentation.setProperty("line.color", color);
						iPresentation.setProperty("font.color", color);
						changedMessageList.add(iPresentation);
					}
					if (messageName.equals(labelMethodName)) {

						if (foundDiagramList.contains(sequenceDiagram) == false) {
							foundDiagramList.add(sequenceDiagram);
						}
					}

				} catch (InvalidUsingException e) {
					e.printStackTrace();
				} catch (InvalidEditingException e) {
					e.printStackTrace();
				}	
			}
		}
		return foundDiagramList;
	}

	public void refleshChangedMessages(){
		try {
			for(int i_changedMessageList = 0;i_changedMessageList<changedMessageList.size();i_changedMessageList++){
				IPresentation presentation = changedMessageList.get(i_changedMessageList);
				presentation.setProperty("line.color", defaultColor);
				presentation.setProperty("font.color", defaultColor);
			}
		} catch (InvalidEditingException e) {
			e.printStackTrace();
		}

		this.changedMessageList.clear();
	}

	private String from_Label_methodName(JLabel methodLabel){
		String methodName = null;
		DisassemblyMethodSignature dms = new DisassemblyMethodSignature(methodLabel.getText());
		methodName = dms.getMethodName();

		return methodName;
	}
}
