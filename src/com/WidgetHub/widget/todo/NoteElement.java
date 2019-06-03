package com.WidgetHub.widget.todo;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.ContextMenu;

public class NoteElement extends TodoElement {
	private static final long serialVersionUID = 1L;
	

	protected String details;
	protected int lastLineCount;
	

	public NoteElement(TodoWidget widget) {
		super(widget);
		
		lastLineCount = 0;
		details = "";
		
		edit();
	}
	protected NoteElement(String details, TodoWidget widget) {
		super(widget);
		
		this.details = details;
	}
	
	
	public void edit() {
		String input = JOptionPane.showInputDialog("Details:", details);
		if (input != null)
			details = input;
	}
	
	
	@Override
	public LocalDateTime getDateTime() {
		return LocalDateTime.now().withNano(0);
	}
	
	
	@Override
	public void renderElement(BufferedImage canvas) {
		render(canvas, 210, 180, 140);
	}
	protected void render(BufferedImage canvas, int red, int green, int blue) {
		final int xOffset = 5;
		Graphics2D g = canvas.createGraphics();
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, getUnitHeight(canvas.getWidth()) - 1));
		FontMetrics fm = g.getFontMetrics();
		
		g.setColor(colorOf(red, green, blue));
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setColor(colorOf(10, 10, 10));
		g.drawRect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
		
		int y = 0;
		int lineHeight = getUnitHeight(canvas.getWidth()) - 2;
		g.setColor(colorOf(0, 0, 0));
		String[] wrapped = wrapText(details, fm, canvas.getWidth() - xOffset);
		for (String line: wrapped) {
			y += lineHeight;
			g.drawString(line, xOffset, y);
		}
		
		
		lastLineCount = wrapped.length;
	}
	private String[] wrapText(String text, FontMetrics fm, int width) {
		if (text == null) {
			System.err.println("TEXT IS NULL: " + text);
			return new String[0];
		}
		ArrayList<String> lines = new ArrayList<String>();
		
		String line = "";
		for (char c: text.toCharArray()) {
			if (fm.stringWidth(line + c) >= width) {
				lines.add(line);
				line = "";
			}
			
			line += c;
		}
		
		if (line.length() > 0)
			lines.add(line);
		
		return lines.toArray(new String[0]);
	}

	@Override
	public int getHeight(int width) {
		return Math.max(1, lastLineCount) * getUnitHeight(width);
	}
	protected int getUnitHeight(int width) {
		return width / 15;
	}

	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Edit", (action) -> {
						edit();
				}).addItem((!hasClosed())? "Mark": "Restore", (action) -> {
						setClosed(!hasClosed());
				});
	}

	@Override
	public void alert() {
		// Does not receive alerts
	}
}
