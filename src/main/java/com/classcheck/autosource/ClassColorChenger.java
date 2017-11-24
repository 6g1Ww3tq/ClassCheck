package com.classcheck.autosource;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.presentation.IPresentation;

public class ClassColorChenger {

	private IClass targetClass;
	private static String defaultColor = "#FFFFCC";

	public ClassColorChenger(IClass iClass) {
		this.targetClass = iClass;
	}

	public void changeColor(String color) {
		try {
			IPresentation[] presentations = targetClass.getPresentations();

			for(IPresentation presentation : presentations){
				presentation.setProperty("fill.color", color);
				System.out.println(presentation);
			}
		} catch (InvalidUsingException e) {
			e.printStackTrace();
		} catch (InvalidEditingException e) {
			e.printStackTrace();
		}
	}

}
