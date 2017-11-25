package com.classcheck.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ImageTab extends JPanel {
	private File picFile;
	private ImageView imageView;
	private JPanel zoomLabel_north;
	private JButton zoomInButton;
	private JButton zoomOutButton;
	private JLabel zoomPacent;
	private JScrollPane scrollPane_center;

	public ImageTab(File file) {
		this.picFile = file;
		initComponent();
		initEvent();
	}

	private void initComponent() {
		double scale_width  = 1;
		double scale_height = 1;
		zoomLabel_north = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		zoomInButton = new JButton("ZoomIn",new ImageIcon(getClass().getResource("/icons/zoomin.png")));
		zoomOutButton = new JButton("ZoomOut",new ImageIcon(getClass().getResource("/icons/zoomout.png")));
		zoomPacent = new JLabel("100%");
		imageView = new ImageView(picFile,scale_width,scale_height);
		scrollPane_center = new JScrollPane(imageView);
		zoomLabel_north.add(zoomInButton);
		zoomLabel_north.add(zoomOutButton);
		zoomLabel_north.add(zoomPacent);

		this.setLayout(new BorderLayout());
		add(zoomLabel_north,BorderLayout.NORTH);
		add(scrollPane_center,BorderLayout.CENTER);
	}

	private void initEvent() {
		zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double ten_rate = 0.1;
				double scale_width  = imageView.getScale_width();
				double scale_height = imageView.getScale_height();
				imageView.setScale_width(scale_width+ten_rate);
				imageView.setScale_height(scale_height+ten_rate);
				imageView.repaint();
				double rate_double = scale_height+ten_rate;
				double parcent_double = rate_double * 100;
				int parcent_int = Double.valueOf(parcent_double).intValue();
				zoomPacent.setText(parcent_int + "%");
			}
		});

		zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double ten_rate = 0.1;
				double scale_width  = imageView.getScale_width();
				double scale_height = imageView.getScale_height();
				imageView.setScale_width(scale_width-ten_rate);
				imageView.setScale_height(scale_height-ten_rate);
				imageView.repaint();
				double rate_double = scale_height-ten_rate;
				double parcent_double = rate_double * 100;
				int parcent_int = Double.valueOf(parcent_double).intValue();
				zoomPacent.setText(parcent_int + "%");
			}
		});
	}

	public File getPicFile() {
		return picFile;
	}

}
