package com.WidgetHub.widget.hub;

import java.awt.Graphics;

import com.WidgetHub.widget.AbstractWidget;

/**
 * One widget to rule them all. This hub keeps track of active widgets.
 * 
 * @author Daniel
 *
 */
public class WidgetHub extends AbstractWidget {
	private static final long serialVersionUID = 1L;

	// constructor info
	private static final boolean isTransparent = false;
	private static final int updateDelay = 50;
	private static final String iconPath = null;

	public WidgetHub() {
		super(isTransparent, updateDelay, iconPath);
		// add (buttons?) for each type of widget or something
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		// TODO Auto-generated method stub
		
	}
	
}
