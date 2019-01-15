package com.WidgetHub.widget;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WidgetResizer extends MouseAdapter {
	public static final int DRAG_AREA_SZ = 15;
	
	protected AbstractWidget widget;
	private boolean resizing;
	
	
	public WidgetResizer(AbstractWidget widget) {
		this.widget = widget;
		resizing = false;
	}
	

	@Override
	public void mousePressed(MouseEvent e) {
		double dist = e.getPoint().distance(widget.getPanelSize().width, widget.getPanelSize().height);
		if (dist < DRAG_AREA_SZ) {
			widget.getDragAdapter().setEnabled(false);
			resizing = true;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (resizing) {
			Point mouse = e.getLocationOnScreen();
			widget.setSize(mouse.x - widget.getLocation().x, mouse.y - widget.getLocation().y);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		widget.getDragAdapter().setEnabled(true);
		resizing = false;
	}
}