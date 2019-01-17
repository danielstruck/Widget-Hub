package com.WidgetHub.widget.todo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

import com.WidgetHub.widget.ContextMenu;
import com.WidgetHub.widget.Toolbox;

public class AddEvent extends TodoElement {
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
	public int getHeight(int width) {
		if (widget.isMinimized())
			return width / 15;
		else
			return width / 3;
	}
	
	private int getArcSize(int width) {
		return width / 10;
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
	protected void drawBackground(Graphics g, int y, int width, int height) {
		final int arcSize = getArcSize(width);
		
		g.setColor(Color.black);
		g.fillRoundRect(0, y + 1, width, height, arcSize, arcSize);
		
		g.setColor(Color.lightGray);
		g.fillRoundRect(1, y, width - 1, height - 1, arcSize, arcSize);
	}
	protected void drawAddEvent(Graphics g, int y, int width, int height) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height * 9 / 10));
		g.setColor(Color.darkGray);
		Toolbox.drawCenteredString(g, "+", width / 2, y + height / 2);
	}
}
