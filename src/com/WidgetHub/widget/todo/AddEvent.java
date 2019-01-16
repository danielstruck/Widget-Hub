package com.WidgetHub.widget.todo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

import com.WidgetHub.widget.ContextMenu;

/**
 * TODO remove parent class TodoEvent from AddEvent
 */
public class AddEvent extends TodoEvent {
	public AddEvent(TodoWidget widget) {
		super(widget);
		
		setDateTime(LocalDateTime.MIN);
		this.widget = widget;
	}
	
	
	@Override
	public void onMouseClick(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			widget.addElement(new TodoEvent(widget));
	}
	
	
	@Override
	public void setClosed(boolean closed) {
		/* This element cannot be closed */
	}
	@Override
	public void alert(LocalDateTime now) {
		/* This element cannot be alerted */
	}
	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Add event", (action) -> {
						widget.addElement(new TodoEvent(widget));
					}).addItem("Add repeatable", (action) -> {
						widget.addElement(new RepeatableTodoEvent(widget));
					});
	}
	
	@Override
	public void render(Graphics g, int y, int width) {
		int height = getHeight(width);
		
		drawBackground(g, y, width, height);
		drawAddEvent(g, y, width, height);
	}
	protected void drawAddEvent(Graphics g, int y, int width, int height) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height * 9 / 10));
		g.setColor(Color.darkGray);
		drawCenteredString(g, "+", width / 2, y + height / 2);
	}
}
