package com.WidgetHub.widget.clipboardViewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;

public class ClipboardFileData extends ClipboardData<List<File>> {

	protected ClipboardFileData() {
		super(DataFlavor.javaFileListFlavor);
	}

	@Override
	public void drawFlavor(List<File> files, Graphics2D g) {
		if (files == null)
			return;
		
		int y = fontSize;
		g.setFont(flavorTypeFont);
		g.setColor(flavorTypeColor);
		g.drawString("FILE", xOffset, y);
		
		for (File f: files) {
			y += fontSize;
			g.setColor((f.isDirectory()? Color.green: Color.blue));
			g.drawString(f.getName(), xOffset + 2, y);
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
	public static int getHeight(List<File> files) {
		return (files.size() + 1) * fontSize + 2;
	}
}
