package com.WidgetHub.widget.fractal.view;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScaledImageDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private BufferedImage image;
	public double scale;
	
	public ScaledImageDisplay(BufferedImage img) {
		setImage(img);
		
		
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				super.paint(g);
				int imgWidth  = image.getWidth(),
					imgHeight = image.getHeight();
				
				scale = getScaleFactorToFit(imgWidth, imgHeight, getWidth(), getHeight());
				
				int scaledWidth  = (int) (imgWidth * scale),
					scaledHeight = (int) (imgHeight * scale);
				
				int x = getWidth() / 2 - scaledWidth / 2,
				    y = getHeight() / 2 - scaledHeight / 2;
				
				g.drawImage(image.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_SMOOTH), x, y, null);
			}
		};
		getContentPane().add(panel);
		
		
		int width  = Math.max(image.getWidth() + 50, 250),
			height = Math.max(image.getHeight() + 50, 250);
		setSize(width, height);
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setImage(BufferedImage img) {
		this.image = img;
	}
	
	@Override
	public void repaint() {
		super.repaint();
		panel.repaint();
	}
	
	public static double getScaleFactorToFit(int origWidth, int origHeight, int targWidth, int targHeight) {
		double scaledWidth  = (double) targWidth / origWidth;
		double scaledHeight = (double) targHeight / origHeight;
		
		return Math.min(scaledWidth, scaledHeight);
	}
}