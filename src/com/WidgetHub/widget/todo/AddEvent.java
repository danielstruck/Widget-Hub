package com.WidgetHub.widget.todo;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import com.WidgetHub.widget.ContextMenu;
import com.WidgetHub.widget.Toolbox;

public class AddEvent extends TodoElement {
	private static final long serialVersionUID = 1L;
	
	
	public AddEvent(TodoWidget widget) {
		super(widget);
		
		setDateTime(LocalDateTime.MIN);
		this.widget = widget;
	}
	
	
	@Override
	public void onMouseClick(MouseEvent e, Point adjustedClick) {
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
	
	
	@Override
	public void setClosed(boolean closed) {
		/* This element cannot be closed */
	}
	
	
	@Override
	public void alert() {
		/* This element cannot be alerted */
	}
	
	
	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Add event", (action) -> {
							widget.addElement(new TodoEvent(widget));
					}).addItem("Add repeatable", (action) -> {
							widget.addElement(new RepeatableTodoEvent(widget));
					}).addItem("Add Job", (action) -> {
							widget.addElement(new JobElement(widget));
					}).addItem("Add Note", (action) -> {
							widget.addElement(new NoteElement(widget));
					});
	}
	
	
	@Override
	public void renderElement(BufferedImage canvas) {
		Graphics2D g = canvas.createGraphics();
		
		drawBackground(g, canvas);
		drawAddEvent(g, canvas);
	}
	protected void drawBackground(Graphics2D g, BufferedImage canvas) {
		final int arcSize = getArcSize(canvas.getWidth());
		
		g.setColor(colorOf(0, 0, 0));
		g.fillRoundRect(0, 1, canvas.getWidth(), canvas.getHeight(), arcSize, arcSize);
		
		g.setColor(colorOf(192, 192, 192));
		g.fillRoundRect(1, 0, canvas.getWidth() - 1, canvas.getHeight() - 1, arcSize, arcSize);
	}
	protected void drawAddEvent(Graphics2D g, BufferedImage canvas) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, canvas.getHeight()));
		g.setColor(colorOf(64, 64, 64));
		Toolbox.drawCenteredString(g, "+", canvas.getWidth() / 2, canvas.getHeight() / 2);
	}
}
