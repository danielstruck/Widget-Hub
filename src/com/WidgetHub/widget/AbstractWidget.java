package com.WidgetHub.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class AbstractWidget extends JFrame {
	private static final long serialVersionUID = 1L;
	protected JPanel panel;
	protected WidgetDragger dragAdapter;
	private boolean windowInFocus;
	
	
	/**
	 * Empty constructor for ease of importing external, self-contained applications (widgets)
	 */
	protected AbstractWidget() {
		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				windowInFocus = true;
			}
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				windowInFocus = false;
			}
		});
	}
	protected AbstractWidget(boolean isTransparent, int updateDelay, String iconPath) {
		this();
		
		if (iconPath != null) {
			try {
				setIconImage(ImageIO.read(getClass().getResource(iconPath)));
			} catch (IOException e) {
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
			
			dragAdapter = new WidgetDragger(this);
			panel.addMouseMotionListener(dragAdapter);
			panel.addMouseListener(dragAdapter);
		}
		
		
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
	
	
	public abstract void update();
	public abstract void render(Graphics g);
	
	
	public WidgetDragger getDragAdapter() {
		return dragAdapter;
	}
	
	
	public Dimension getPanelSize() {
		return panel.getSize();
	}
	
	
	public boolean isWindowInFocus() {
		return windowInFocus;
	}
	
	public void destroy() {
		super.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
