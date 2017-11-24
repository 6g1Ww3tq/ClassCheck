package com.classcheck.panel;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageTab extends JPanel {
	private File picFile;
	private BufferedImage bufImage;
	private boolean pressed = false;
	private int px,py,rx,ry;

	public ImageTab(File file) {
		this.picFile = file;
		initComponent();
		initEvent();
	}

	private void initComponent() {
		px = py = rx = ry = 0;

		try {
			bufImage = ImageIO.read(picFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	private void initEvent() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				pressed = true;
				px = e.getX();
				py = e.getY();
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				pressed = false;
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);

				rx = e.getX();
				ry = e.getY();
				repaint();
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		});
	}

	public File getPicFile() {
		return picFile;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Backup original transform
		AffineTransform originalTransform = g2d.getTransform();

		g2d.translate(rx, ry);
		//		g2d.scale(zoom, zoom);

		// paint the image here with no scaling
		g2d.drawImage(bufImage, 0,0,null);

		// Restore original transform
		g2d.setTransform(originalTransform);
	}
}
