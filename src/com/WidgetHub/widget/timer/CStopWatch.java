package com.WidgetHub.widget.timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class CStopWatch {
	public static final int SIZE = 50;
	public static int CUSHION = 5;
	
	
	private String name;
	private long time;
	private long lastTimeUpdated;
	private boolean paused;
	private Color bg;
	
	
	public CStopWatch(String name){
		this.name = name;
		reset();
	}
	
	
	public void reset(){
		time = 0;
		lastTimeUpdated = -1;
		paused = true;
		bg = Color.white;
	}
	
	
	public String getName(){
		return name;
	}
	public void setName(String n){
		name = n;
	}
	
	
	public boolean isPaused(){
		return paused;
	}
	public void setPaused (boolean paused) {
		this.paused = paused;
		if(paused)	bg = Color.white;
		else		bg = Color.cyan;
//		System.out.println(bg.getRed() + "," + bg.getGreen() + "," + bg.getBlue());
	}
	public void togglePaused () {
		setPaused(!isPaused());
	}
	
	
	public void setBG(Color col){
		bg = col;
	}
	
	public String getTimeFormat(){
		return getTimeFormat(getCurrentTimeValue());
	}
	public String getTimeFormat(long time) {
		return formatMillis(time);
	}
	public static String formatMillis(long time){
		String tor = "";
		//format milliseconds to h:m:s
		long hours = time/3600000;
		long minutes = (time%3600000)/60000;
		long seconds = ((time%3600000)%60000)/1000;
		tor += hours + ":" + minutes + ":" + seconds;
//		System.out.println(hours + "," + minutes + "," + seconds + "/" + time);
		return tor;
	}
	public long getTime(){
		return time;
	}
	public long getCurrentTimeValue(){
		return time + (System.currentTimeMillis()-lastTimeUpdated);
	}
	
	public void renderBG(int renderY, Graphics g){
		final Rectangle r = new Rectangle(CUSHION, renderY, SIZE*2, SIZE);
		final int shadow = 1, bgRoundness = 7;
		
		//draw the stop watch at [renderPos]
		g.setColor(Color.lightGray);
		g.fillRoundRect(r.x+shadow, r.y+shadow, r.width+shadow, r.height+shadow, bgRoundness, bgRoundness);
		String pauseState;
		if(paused)	pauseState = "PAUSED";
		else		pauseState = "RUNNING";
		g.setColor(bg);
		g.fillRoundRect(r.x, r.y, r.width, r.height, bgRoundness, bgRoundness);
		g.setColor(Color.lightGray);
		g.drawString(pauseState, CUSHION*2, renderY+g.getFontMetrics().getHeight()*2);
		//draw name
		g.setColor(Color.black);
		g.drawString(name, CUSHION*2, renderY+g.getFontMetrics().getHeight());
		//draw the state (paused? unpaused?)
//		if(isStared()){
//			g.setColor(Color.orange);
//			g.fillRect(SIZE*2-10, renderY+g.getFontMetrics().getHeight()*2-10, 10, 10);
//		}
		
		if(!paused)
			time += System.currentTimeMillis() - lastTimeUpdated;
		lastTimeUpdated = System.currentTimeMillis();
	}
	
	public void render (int renderY, Graphics g) {
		renderBG(renderY, g);
		
		g.setColor(Color.red);
		if(paused)
			g.drawString(getTimeFormat(time), CUSHION*2, renderY+g.getFontMetrics().getHeight()*3);
		else
			g.drawString(getTimeFormat(getCurrentTimeValue()), CUSHION*2, renderY+g.getFontMetrics().getHeight()*3);
	}
}
