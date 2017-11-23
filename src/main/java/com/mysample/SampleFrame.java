package com.mysample;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;

public class SampleFrame extends JFrame implements ProjectEventListener{

	ProjectAccessor projectAccessor;
	AstahAPI api;
	
	public SampleFrame() {
		setSize(new Dimension(400,200));
		setVisible(true);
	}

	private void addProjectEventListener(File projectFile) {
		/*
		try {
			api = AstahAPI.getAstahAPI();
			projectAccessor = api.getProjectAccessor();
            projectAccessor.open(projectFile.getPath(), true, false, true);
		} catch (ClassNotFoundException | ProjectNotFoundException | LicenseNotFoundException | NonCompatibleException | IOException | ProjectLockedException e) {
			e.getMessage();
		}
		*/
	}

	public static void main(String[] args) {
		SampleFrame frame = new SampleFrame();
		JFileChooser chooser = new JFileChooser();
		File projectFile;
		int rtnVal;

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.showOpenDialog(frame);
		rtnVal = chooser.showOpenDialog(frame);

		if(rtnVal == JFileChooser.APPROVE_OPTION) {
			projectFile = chooser.getSelectedFile();
			frame.addProjectEventListener(projectFile);
		}
	}

	@Override
	public void projectChanged(ProjectEvent e) {
	}
	@Override
	public void projectClosed(ProjectEvent e) {
	}
	@Override
	public void projectOpened(ProjectEvent e) {
	}
}
