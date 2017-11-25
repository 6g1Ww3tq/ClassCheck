package com.classcheck.panel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class ImageView extends JPanel {
	private File picFile;
	private BufferedImage bufImage;
	private Dimension preferredSize;
	private double scale_width,scale_height;
	
	public ImageView(File picFile, double scale_width, double scale_height) {
		this.picFile = picFile;
		this.scale_width = scale_width;
		this.scale_height = scale_height;
		initComponent();
//		initEvent();
	}
	
	public double getScale_width() {
		return scale_width;
	}

	public void setScale_width(double scale_width) {
		this.scale_width = scale_width;
	}

	public double getScale_height() {
		return scale_height;
	}

	public void setScale_height(double scale_height) {
		this.scale_height = scale_height;
	}

	public File getPicFile() {
		return picFile;
	}

	private void initComponent() {
		try {
			bufImage = ImageIO.read(picFile);
			this.preferredSize = new Dimension(bufImage.getWidth(), bufImage.getHeight());
			setPreferredSize(this.preferredSize);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		//zoom
		g2d.scale(scale_width,scale_height);

		// paint the image here with no scaling
		g2d.drawImage(bufImage, 0, 0, preferredSize.width, preferredSize.height, this);
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		this.preferredSize = preferredSize;
	}
}
