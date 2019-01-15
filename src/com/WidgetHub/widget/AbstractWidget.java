package com.WidgetHub.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class AbstractWidget extends JFrame {
	private static final long serialVersionUID = 1L;
	protected JPanel panel;
	protected WidgetDragger dragAdapter;
	private boolean closed;
	
	/**
	 * Empty constructor for ease of importing external, self-contained applications (widgets)
	 */
	protected AbstractWidget() {
		
	}
	protected AbstractWidget(boolean isTransparent, int updateDelay, String iconPath) {
		closed = false;
		
		if (iconPath != null) {
			try {
				setIconImage(ImageIO.read(getClass().getResource(iconPath)));
			}
			catch (IOException e) {
				System.err.println("Could not read icon image");
			}
		}
		
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			
			public void paint(Graphics g) {
				super.paint(g);
				render(g);
			}
		};
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel);
		
		if (isTransparent) {
			setUndecorated(true);
			setBackground(new Color(0, 0, 0, 0));
			panel.setOpaque(false);
		}
		
		dragAdapter = new WidgetDragger(this);
		panel.addMouseMotionListener(dragAdapter);
		panel.addMouseListener(dragAdapter);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setVisible(true);
		revalidate();
		
		new Timer(updateDelay, (action) -> {
			update();
			panel.repaint();
		}).start();
	}
	
	
	public WidgetDragger getDragAdapter() {
		return dragAdapter;
	}
	
	
	public Dimension getPanelSize() {
		return panel.getSize();
	}
	
	
	public abstract void update();
	public abstract void render(Graphics g);
	
	
	public void close() {
		closed = true;
	}
	public boolean hasClosed() {
		return closed;
	}
}
