package com.WidgetHub.widget.todo;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.ContextMenu;

public class JobElement extends NoteElement {
	private static final long serialVersionUID = 1L;
	
	
	private ArrayList<NoteElement> tasks;
	private boolean collapsed;
	
	
	public JobElement(TodoWidget widget) {
		super(widget);
		
		tasks = new ArrayList<NoteElement>();
		collapsed = false;
	}
	public JobElement(String details, TodoWidget widget) {
		super(details, widget);
		
		tasks = new ArrayList<NoteElement>();
		collapsed = false;
	}
	
	
	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Add Note", (action) -> {
						String input = JOptionPane.showInputDialog("Details:");
						if (input != null)
							tasks.add(new NoteElement(input, widget));
					})
					.addItem("Add Sub-Job", (action) -> {
						String input = JOptionPane.showInputDialog("Details:");
						if (input != null)
							tasks.add(new JobElement(input, widget));
					})
					.addItem(collapsed? "Expand": "Collapse", (action) -> {
						collapsed = !collapsed;
					});
		
		super.applyCustomContextMenu(contextMenu);
	}
	
	
	@Override
	public void setWidget(TodoWidget widget) {
		super.setWidget(widget);
		
		for (NoteElement task: tasks) {
			task.setWidget(widget);
		}
	}
	
	
	@Override
	public void onMousePress(MouseEvent e, Point adjustedClick) {
		if (e.isPopupTrigger()) {
			boolean foundElement = false;
			
			if (!collapsed) {
				int width = (int) widget.getPanelSize().getWidth();
				int currentY = this.getHeight(width);
				
				for (int i = tasks.size() - 1; i >= 0 && !foundElement; i--) {
					NoteElement task = tasks.get(i);
					int taskHeight = task.getHeight(width);
					
					currentY -= taskHeight;
					
					if (currentY <= adjustedClick.y) {
						foundElement = true;
						
						if (task instanceof JobElement) {
							adjustedClick.y -= currentY;
							task.onMousePress(e, adjustedClick);
						}
						else {
							task.showContextMenu(e);
						}
					}
				}
			}
			
			if (!foundElement) {
				showContextMenu(e);
			}
		}
	}
	@Override
	public void onMouseRelease(MouseEvent e, Point adjustedClick) {
		onMousePress(e, adjustedClick);
	}
	
	
	
	@Override
	public void flush() {
		for (int i = tasks.size() - 1; i >= 0; i--) {
			tasks.get(i).flush();
			
			if (tasks.get(i).hasClosed()) {
				tasks.remove(i);
			}
		}
	}
	
	
	
	@Override
	public void renderElement(BufferedImage canvas) {
		render(canvas, 200, 200, 150);
		
		if (!collapsed)
			renderTasks(canvas);
	}
	private void renderTasks(BufferedImage canvas) {
		Graphics2D g = canvas.createGraphics();
		
		int xOffset = this.getUnitHeight(canvas.getWidth()) / 3;
		int y = this.getHeight(canvas.getWidth());
		for (NoteElement task: tasks) {
			BufferedImage subCanvas = new BufferedImage(canvas.getWidth() - xOffset, task.getHeight(canvas.getWidth()), BufferedImage.TYPE_INT_ARGB);
			task.renderElement(subCanvas);
			g.drawImage(subCanvas, xOffset, y, null);
			
			y += task.getHeight(canvas.getWidth());
			lastLineCount += task.lastLineCount;
		}
	}
}
