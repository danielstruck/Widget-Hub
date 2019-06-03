package com.WidgetHub.widget.clipboardViewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;

public class ClipboardImageData extends ClipboardData<Image> {

	protected ClipboardImageData() {
		super(DataFlavor.imageFlavor);
	}
	
	@Override
	public void drawFlavor(Image img, Graphics2D g) {
		if (img == null)
			return;
		
		g.setFont(flavorTypeFont);
		g.setColor(flavorTypeColor);
		g.drawString("IMAGE", xOffset, fontSize);
		
		g.setColor(Color.black);
		g.drawRect(xOffset, fontSize + 2, img.getWidth(null) + 1, img.getHeight(null) + 1);
		g.drawImage(img, xOffset + 1, fontSize + 3, null);
	}

	@Override
	public int getHeight() {
		try {
			return getHeight(getData());
		} catch (Exception e) {
			return -1;
		}
	}
	public static int getHeight(Image img) {
		return fontSize + img.getHeight(null) + 4;
	}
}
