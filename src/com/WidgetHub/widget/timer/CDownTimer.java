package com.WidgetHub.widget.timer;

import java.awt.Color;
import java.awt.Graphics;

import com.WidgetHub.tone.Note;

public class CDownTimer extends CStopWatch {
	private long startTime;
	private boolean expired;
	

	public CDownTimer(String name, long startTimeSeconds) {
		super(name);
		this.startTime = startTimeSeconds*1000;
		expired = false;
	}
	
	public String getTimerFormat(long time){
		return super.formatMillis(startTime - time);
	}
	
	public void setExpired(boolean expired){
		this.expired = expired;
		if(expired)
			Note.playSinNotes(50, "A4 A4 A4");
	}
	
	public void render (int renderY, Graphics g) {
		super.renderBG(renderY, g);
		
		if(expired) {
			if (System.currentTimeMillis() % 1000 < 500)
				super.setBG(Color.red);
			else
				super.setBG(super.isPaused()? Color.white: Color.cyan);
		}
		
		g.setColor(new Color(0, 195, 0));
		if(super.isPaused()){
			if(!expired && startTime - super.getTime() <= 0)
				setExpired(true);
			g.drawString(getTimerFormat(super.getTime()), CUSHION*2, renderY+g.getFontMetrics().getHeight()*3);
		}
		else{
			if(!expired && startTime - super.getCurrentTimeValue() <= 0)
				setExpired(true);
			g.drawString(getTimerFormat(super.getCurrentTimeValue()), CUSHION*2, renderY+g.getFontMetrics().getHeight()*3);
		}
	}
}
