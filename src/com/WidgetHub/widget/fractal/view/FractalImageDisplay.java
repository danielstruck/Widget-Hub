package com.WidgetHub.widget.fractal.view;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.WidgetHub.widget.fractal.explorer.FractalInfo;

public class FractalImageDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	public double scale;
	
	public FractalImageDisplay(FractalInfo info) {
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				super.paint(g);
				int imgWidth  = info.img.getWidth(),
					imgHeight = info.img.getHeight();
				
				scale = getScaleFactorToFit(imgWidth, imgHeight, getWidth(), getHeight());
				
				int scaledWidth  = (int) Math.max(1, imgWidth * scale),
					scaledHeight = (int) Math.max(1, imgHeight * scale);
				
				int x = getWidth() / 2 - scaledWidth / 2,
				    y = getHeight() / 2 - scaledHeight / 2;
				
				g.drawImage(info.img.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_SMOOTH), x, y, null);
			}
		};
		getContentPane().add(panel);
		
		
		int width  = Math.max(info.img.getWidth() + 50, 250),
			height = Math.max(info.img.getHeight() + 50, 250);
		setSize(width, height);
		
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
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