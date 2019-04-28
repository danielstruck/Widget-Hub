package com.WidgetHub.widget.todo;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.WidgetHub.widget.ContextMenu;

public abstract class TodoElement implements Comparable<TodoElement> {
	public static final DateTimeFormatter saveDateFormat = DateTimeFormatter.ofPattern("EEE MMM dd"),
			  							  saveTimeFormat = DateTimeFormatter.ofPattern("h mm a"),
			  							  saveFormat     = DateTimeFormatter.ISO_DATE_TIME,
			  							  defaultDateTimeFormat = DateTimeFormatter.ofPattern("EEE, MMM dd | h:mm a");
	
	protected TodoWidget widget;
	private LocalDateTime dateTime;
	private boolean closed;
	
	protected TodoElement(TodoWidget widget) {
		this.widget = widget;
		
		setDateTime(LocalDateTime.now());
	}
	protected TodoElement(TodoWidget widget, BufferedReader reader) {
		this(widget);
		
		try {
			String line;
			// skip to start start
			while ((line = reader.readLine()) != null && !line.equals("start"));
			// next line is datetime
			line = reader.readLine();
			setDateTime(LocalDateTime.parse(line, saveFormat));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public abstract void render(Graphics g, int y, int width);
	public abstract int getHeight(int width);
	public abstract void applyCustomContextMenu(ContextMenu contextMenu);
	public abstract void alert();
	
	
	@Override
	public int compareTo(TodoElement o) {
		return dateTime.compareTo(o.dateTime);
	}
	
	
	public void onMouseClick(MouseEvent e) {}
	public void onMousePress(MouseEvent e) {
		if (e.isPopupTrigger())
			TodoTools.showContextMenu(e, this, widget);
	}
	public void onMouseRelease(MouseEvent e) {
		if (e.isPopupTrigger())
			TodoTools.showContextMenu(e, this, widget);
	}
	
	
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime.withSecond(0);
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
	
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	public boolean hasClosed() {
		return closed;
	}

	
	public String data() {
		String tor = "start\n";

		tor += saveFormat.format(dateTime) + "\n";
		
		return tor;
	}
	
	@Override
	public String toString() {
		return "TodoElement [dateTime=" + dateTime.toString() + ", closed=" + closed + "]";
	}
}
