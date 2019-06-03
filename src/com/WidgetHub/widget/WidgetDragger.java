package com.WidgetHub.widget;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

public class WidgetDragger extends MouseAdapter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	private Point dragPoint;
	private Component component;
	private boolean enabled;
	
	
	public WidgetDragger(Component component) {
		this.component = component;
		enabled = true;
	}


	@Override
	public void mousePressed(MouseEvent e) {
		dragPoint = e.getPoint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point end = e.getPoint();
		if (enabled) {
			component.setLocation(new Point(component.getLocationOnScreen().x + end.x - dragPoint.x, component.getLocationOnScreen().y + end.y - dragPoint.y));
		}
	}
	
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
