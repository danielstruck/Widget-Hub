package com.WidgetHub.widget.magnifier;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public enum Crosshair {
	None {
		@Override
		public void apply(BufferedImage img, Color col) {
			// placeholder for doing nothing
		}
	},
	Cross_X {
		@Override
		public void apply(BufferedImage img, Color col) {
			final int w = img.getWidth(),
					  h = img.getHeight();
			final Graphics g = img.getGraphics();
			
			g.setColor(col);
			g.drawLine(w/2, 0, w/2, h);
			g.drawLine(0, h/2, w, h/2);
		}
	},
 	Boxes {
		@Override
		public void apply(BufferedImage img, Color col) {
			final int w = img.getWidth(),
					  h = img.getHeight();
			final Graphics g = img.getGraphics();
			
			g.setColor(col);
			for (int x = 0; x < w; x += w/10) {
				for (int y = 0; y < h; y += h/10) {
					g.drawRect(x, y, w/10, h/10);
				}
			}
		}
	};
	
	public abstract void apply(BufferedImage img, Color col);
}
