package com.WidgetHub.widget;

import java.awt.FontMetrics;
import java.io.Serializable;

public class StringScroller implements Serializable {
	private static final long serialVersionUID = 1L;


	public static final double DEFAULT_SCROLL_STALL_STEP = 50;
	public static final double DEFAULT_SCROLL_TICK_STEP = .15;
	
	
	private final double stallStep;
	private final double tickStep;
	private double indexStart;
	public String text;
	
	
	public StringScroller(String text, double stallStep, double tickStep) {
		this.text = text;
		this.stallStep = stallStep;
		this.tickStep = tickStep;
		indexStart = 0;
	}
	
	public boolean willOverflow(FontMetrics fm, int maxWidth) {
		return fm.stringWidth(text) > maxWidth;
	}
	
	public String next(FontMetrics fm, int maxWidth) {
		if (willOverflow(fm, maxWidth)) {
			if (indexStart < 1) {
				indexStart += (double) 1 / stallStep;
			}
			else {
				indexStart += tickStep;
				indexStart %= text.length();
			}
			
			String scrolling = new String(text);
			int indexEnd;
			for (indexEnd = text.length(); fm.stringWidth(scrolling.substring((int) indexStart, indexEnd)) > maxWidth; indexEnd--)
				/* do nothing */;
			
			return scrolling.substring((int) indexStart, indexEnd);
		}
		else {
			return text;
		}
	}
}
