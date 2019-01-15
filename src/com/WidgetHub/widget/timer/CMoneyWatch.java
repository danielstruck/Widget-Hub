package com.WidgetHub.widget.timer;
import java.awt.Color;
import java.awt.Graphics;

public class CMoneyWatch extends CStopWatch {
	private float hourlyRate;
	private String prefix;
	

	public CMoneyWatch(String name, float hourlyRate, String prefix) {
		super(name);
		this.hourlyRate = hourlyRate;
		this.prefix = prefix + " ";
	}
	
	public String getMoneyFormat(long time){
		String tor = "";

		double hours = (double)time/3600000;
//		System.out.println("Hours: " + hours);
//		System.out.println("Earned: " + hours);
		tor += "" + ((double)((int)(100*(hours * hourlyRate)))/100);
//		System.out.println();
		
		return "$" + tor;
	}
	
	public void render (int renderY, Graphics g) {
		super.renderBG(renderY, g);
		
		g.setColor(new Color(0, 195, 0));
		if(super.isPaused())
			g.drawString(prefix + getMoneyFormat(super.getTime()), CUSHION*2, renderY+g.getFontMetrics().getHeight()*3);
		else
			g.drawString(prefix + getMoneyFormat(super.getCurrentTimeValue()), CUSHION*2, renderY+g.getFontMetrics().getHeight()*3);
	}
}
