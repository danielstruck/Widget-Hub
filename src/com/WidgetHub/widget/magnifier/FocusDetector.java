package com.WidgetHub.widget.magnifier;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusDetector implements FocusListener {
	public boolean hasFocus = true;

	@Override
	public void focusGained(FocusEvent e) {
		hasFocus = true;
	}

	@Override
	public void focusLost(FocusEvent e) {
		hasFocus = false;
	}
	
}