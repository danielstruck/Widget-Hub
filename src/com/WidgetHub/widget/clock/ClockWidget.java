package com.WidgetHub.widget.clock;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.WidgetHub.widget.AbstractWidget;
import com.WidgetHub.widget.EscapeCloseAdapter;

/**
 * Simple clock widget with transparent background. Includes a date counter!
 * 
 * @author Daniel Struck
 *
 */
public class ClockWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	// settings
	protected final static DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm:ss a");
	protected final static DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEE MMMM d, yyyy");
	protected final static int SIZE = 75;
	
	// Constructor info
	private static final int updateDelay = 100;
	private static final boolean isTransparent = true;
	private static final String iconPath = "/clock icon.png";
	

	public ClockWidget() {
		super(isTransparent, updateDelay, iconPath);
		
		setTitle("Clock Widget");
		setSize(7 * SIZE, SIZE);
		panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		
		addKeyListener(new EscapeCloseAdapter(this));
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		final LocalDateTime now = LocalDateTime.now();
		
		// time
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, panel.getHeight()));
		String time = TIME.format(now).toLowerCase();
		Point timePos = new Point((panel.getWidth() / 2 - g.getFontMetrics().stringWidth(time) / 2), g.getFontMetrics().getHeight() / 2);
		drawWithShadow(g, time, timePos, 1);
		
		// date
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, panel.getHeight() / 5));
		String date = DATE.format(now).toUpperCase();
		Point datePos = new Point((panel.getWidth() / 2 - g.getFontMetrics().stringWidth(date) / 2), (int) (timePos.y + g.getFontMetrics().getHeight() * 1.2));
		drawWithShadow(g, date, datePos, 1);
	}

	public void drawWithShadow(Graphics g, String text, Point pos, int shadow) {
		g.setColor(Color.black);
		g.drawString(text, pos.x - shadow, pos.y + shadow);
		g.drawString(text, pos.x - shadow, pos.y - shadow);
		g.drawString(text, pos.x + shadow, pos.y + shadow);
		g.drawString(text, pos.x + shadow, pos.y - shadow);
		
		g.setColor(new Color(180, 180, 180));
		g.drawString(text, pos.x, pos.y);
	}
}
