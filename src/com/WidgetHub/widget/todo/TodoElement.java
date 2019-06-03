package com.WidgetHub.widget.todo;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.ContextMenu;

public abstract class TodoElement implements Comparable<TodoElement>, Serializable {
	private static final long serialVersionUID = 1L;
	
	
	public static final DateTimeFormatter saveDateFormat 		= DateTimeFormatter.ofPattern("EEE MMM dd");
	public static final DateTimeFormatter saveTimeFormat 		= DateTimeFormatter.ofPattern("h mm a");
	public static final DateTimeFormatter saveFormat     		= DateTimeFormatter.ISO_DATE_TIME;
	public static final DateTimeFormatter defaultDateTimeFormat = DateTimeFormatter.ofPattern("EEE, MMM dd | h:mm a");
	
	protected TodoWidget widget;
	private LocalDateTime dateTime;
	private boolean closed;
	
	
	protected TodoElement(TodoWidget widget) {
		this.widget = widget;
		
		setDateTime(LocalDateTime.now());
	}
	
	
	public abstract void renderElement(BufferedImage canvas);
	public abstract void applyCustomContextMenu(ContextMenu contextMenu);
	public abstract int getHeight(int width);
	public abstract void alert();
	
	
	public void flush() {}
	

	protected void showContextMenu(MouseEvent event) {
		ContextMenu contextMenu = new ContextMenu("Menu");
		
		
		applyCustomContextMenu(contextMenu);
		
		
		contextMenu.addItem("Flush", (action) -> {
						if (JOptionPane.showConfirmDialog(widget, "Confirm flush? This action is irreversable.") == JOptionPane.OK_OPTION)
							widget.flush();
				})
				.addItem((!widget.isMinimized())? "Minimize": "Maximize", (action) -> {
						widget.setMinimized(!widget.isMinimized());
				})
				.addItem("Change Size", (action) -> {
						String input = null;
						try {
							input = JOptionPane.showInputDialog("Current width: " + widget.getPanelSize().width + " px.\nInput frame width in pixels:");
							if (input == null)
								return;
							
							int width = Integer.parseInt(input);
							if (width < 0)
								throw new NumberFormatException();
							
							widget.setSize(width, widget.getHeight());
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(widget, "Input must be a positive integer: " + input);
						}
				})
				.addItem("Change Spacing", (action) -> {
						String input = null;
						try {
							input = JOptionPane.showInputDialog("Current spacing: " + widget.getSpacing() + " px. \nInput spacing in pixels:");
							if (input == null)
								return;
							
							int spacing = Integer.parseInt(input);
							if (spacing < 0)
								throw new NumberFormatException();
							
							widget.setSpacing(spacing);
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(widget, "Input must be a positive integer: " + input);
						}
				})
				.addItem("Close", (action) -> {
						if (JOptionPane.showConfirmDialog(widget, "Confirm exit?") == JOptionPane.OK_OPTION)
							widget.destroy();
				});
		
		contextMenu.show(event.getComponent(), event.getX(), event.getY());
	}
	
	
	@Override
	public int compareTo(TodoElement other) {
		return getDateTime().compareTo(other.getDateTime());
	}
	
	
	public void setWidget(TodoWidget widget) {
		this.widget = widget;
	}
	
	
	public void onMouseClick(MouseEvent e, Point adjustedClick) {}
	public void onMousePress(MouseEvent e, Point adjustedClick) {
		if (e.isPopupTrigger())
			showContextMenu(e);
	}
	public void onMouseRelease(MouseEvent e, Point adjustedClick) {
		onMousePress(e, adjustedClick);
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
	
	
	protected static int getArcSize(int width) {
		return width / 10;
	}
	
	
	protected int getRenderAlpha() {
		if (widget.isWindowInFocus()) {
			return 255;
		}
		else {
			return 90;
		}
	}
	protected Color colorOf(int r, int g, int b) {
		Color c = new Color(r, g, b, getRenderAlpha());
		
		if (hasClosed())
			c = c.darker().darker();
		
		return c;
	}
}
