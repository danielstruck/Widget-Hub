package com.WidgetHub.widget.fractal.view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MonitorFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private String[] data;
	/*
	 * FOUND PROBLEM: monitor dimensions are 0:
	 * 
	 * monitor info: true
	 * monitor info: java.awt.Point[x=1005,y=610] | java.awt.Dimension[width=0,height=0] | true
	 */
	
	public MonitorFrame(final Window parent, final int width) {
		JFrame frame = this;
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			int largestDataset = 0;

			public void paint(Graphics g) {
				super.paint(g);
				if (data != null) {
					if (data.length > largestDataset) {
						int newHeight = data.length * 14 + 5;
						Dimension newSize = new Dimension(width, newHeight);
						panel.setPreferredSize(newSize);
						frame.setMinimumSize(newSize);
						frame.pack();
					}
					
					g.setColor(Color.magenta);
					for (int i = 0; i < data.length; i++)
						g.drawString(data[i], 2, 14 * (i + 1));
				}
			}
		};
		panel.setBackground(Color.darkGray);
		getContentPane().add(panel);
		
		
		new Thread(() ->  {
			while (true) {
				int x = parent.getX() - width;
				if (x < 0)
					x = 0;
				setLocation(x, parent.getY() + 40);
			}
		}).start();
		
		setUndecorated(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		

		Dimension newSize = new Dimension(width, 2);
		panel.setPreferredSize(newSize);
		setMinimumSize(newSize);
		pack();
	}
	
	public void monitor(String ...data) {
		this.data = data;
		repaint();
	}
	
	@Override
	public void repaint() {
		super.repaint();
		panel.repaint();
	}
}