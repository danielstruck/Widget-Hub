package com.WidgetHub.widget;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class Toolbox {
	
	private Toolbox() {}

	public static void drawCenteredString(Graphics g, String s, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		
		x -= fm.stringWidth(s) / 2;
		y += getFontHeight(fm) / 2;
		
		g.drawString(s, x, y);
	}
	
	public static int getFontHeight(FontMetrics fm) {
		return (fm.getAscent() - fm.getDescent() - fm.getLeading());
	}
}
