package com.WidgetHub.widget.clipboardViewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class ClipboardData<T extends Object> {
	public static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	// settings
	public static final int fontSize = 15;
	public static final int xOffset = 2;
	public static final Font flavorTypeFont = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
	public static final Color flavorTypeColor = Color.magenta;
	
	
	protected DataFlavor flavor;
	
	
	protected ClipboardData(DataFlavor flavor) {
		this.flavor = flavor;
	}
	
	
	protected T getData() throws UnsupportedFlavorException, IOException {
		return (T) clipboard.getData(flavor);
	}
	public boolean tryFlavor(BufferedImage canvas) {
		try {
			T data = getData();
			
			Graphics2D g = canvas.createGraphics();
			
			drawFlavor(data, g);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public abstract void drawFlavor(T data, Graphics2D g);
	public abstract int getHeight();
}
