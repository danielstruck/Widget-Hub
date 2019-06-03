package com.WidgetHub.widget.clipboardViewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;

public class ClipboardStringData extends ClipboardData<String> {

	protected ClipboardStringData() {
		super(DataFlavor.stringFlavor);
	}

	@Override
	public void drawFlavor(String text, Graphics2D g) {
		if (text == null)
			return;
		
		int y = fontSize;
		g.setFont(flavorTypeFont);
		g.setColor(flavorTypeColor);
		g.drawString("TEXT", xOffset, y);
		
		g.setColor(Color.black);
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			
			line = line.replace("\t", " → ");
			if (i < lines.length - 1)
				line += '↵';
			
			y += fontSize;
			g.drawString(line, xOffset + 2, y);
		}
	}

	@Override
	public int getHeight() {
		try {
			return getHeight(getData());
		} catch (Exception e) {
			return -1;
		}
	}
	public static int getHeight(String text) {
		return (text.split("\n").length + 1) * (fontSize + 1) + 3;
	}
}
