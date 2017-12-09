package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Class_Sequence_ImageTab extends JPanel {
	private String classDiagramName;
	private File picFile;
	private ImageView imageView;
	private JPanel zoomPanel_north;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JLabel zoomPacent;
	private JScrollPane scrollPane_center;

	public Class_Sequence_ImageTab(String classDiagramName, File file) {
		this.classDiagramName = classDiagramName;
		this.picFile = file;
		initComponent();
		initEvent();
	}
	
	public String getClassDiagramName() {
		return classDiagramName;
	}

	private void initComponent() {
		double scale_width  = 1;
		double scale_height = 1;
		zoomPanel_north = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		zoomInButton = new JButton("ZoomIn",new ImageIcon(getClass().getResource("/icons/zoomin.png")));
		zoomOutButton = new JButton("ZoomOut",new ImageIcon(getClass().getResource("/icons/zoomout.png")));
		zoomPacent = new JLabel("100%");
		scrollPane_center = new JScrollPane();
		imageView = new ImageView(picFile,scale_width,scale_height,scrollPane_center);
		scrollPane_center.setViewportView(imageView);
		zoomPanel_north.add(zoomInButton);
		zoomPanel_north.add(zoomOutButton);
		zoomPanel_north.add(zoomPacent);

		this.setLayout(new BorderLayout());
		add(zoomPanel_north,BorderLayout.NORTH);
		add(scrollPane_center,BorderLayout.CENTER);
		
		//FIXME
		//ズームの実装はなしにする
		zoomPanel_north.setVisible(false);
	}

	private void initEvent() {
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double ten_rate = 0.1;
				double width_rate  = imageView.getScale_width() + ten_rate;
				double height_rate = imageView.getScale_height() + ten_rate;
				Dimension preferredSize = imageView.getPreferredSize();
				double ch_width,ch_height;
				imageView.setScale_width(width_rate);
				imageView.setScale_height(height_rate);
				ch_width = preferredSize.getWidth() * (width_rate);
				ch_height = preferredSize.getHeight() * (height_rate);
				preferredSize.setSize(ch_width, ch_width);
				imageView.repaint();
				double parcent_double = height_rate * 100;
				int parcent_int = Double.valueOf(parcent_double).intValue();
				zoomPacent.setText(parcent_int + "%");
			}
		});

		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double ten_rate = 0.1;
				double width_rate  = imageView.getScale_width() - ten_rate;
				double height_rate = imageView.getScale_height() - ten_rate;
				Dimension preferredSize = imageView.getPreferredSize();
				double ch_width,ch_height;
				imageView.setScale_width(width_rate);
				imageView.setScale_height(height_rate);
				ch_width = preferredSize.getWidth() * (width_rate);
				ch_height = preferredSize.getHeight() * (height_rate);
				preferredSize.setSize(ch_width, ch_width);
				imageView.repaint();
				double parcent_double = height_rate * 100;
				int parcent_int = Double.valueOf(parcent_double).intValue();
				zoomPacent.setText(parcent_int + "%");
			}
		});
	}

	public File getPicFile() {
		return picFile;
	}

}
