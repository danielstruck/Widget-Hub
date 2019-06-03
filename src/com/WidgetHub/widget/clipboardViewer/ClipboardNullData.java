package com.WidgetHub.widget.clipboardViewer;

import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.image.BufferedImage;

public class ClipboardNullData extends ClipboardData<Object> {

	protected ClipboardNullData() {
		super(null);
	}
	
	@Override
	protected Object getData()  {
		return null;
	}
	@Override
	public boolean tryFlavor(BufferedImage canvas) {
		Graphics2D g = canvas.createGraphics();
		
		g.setFont(flavorTypeFont);
		g.setColor(flavorTypeColor);
		drawFlavor(null, g);
		
		return true;
	}

	@Override
	public void drawFlavor(Object data, Graphics2D g) {
		g.setFont(flavorTypeFont);
		g.setColor(flavorTypeColor);
		for (DataFlavor f: clipboard.getAvailableDataFlavors()) {
			try {
				clipboard.getData(f);
				
				g.drawString("unhandled DataFlavor: " + f.getClass().getCanonicalName(), xOffset, fontSize);
				
				return;
			} catch (Exception e) {}
		}
		
		g.drawString("unhandled DataFlavor of unknown type", xOffset, fontSize);
	}

	@Override
	public int getHeight() {
		return fontSize;
	}
}
