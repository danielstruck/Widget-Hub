package com.WidgetHub.widget.magnifier;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import com.WidgetHub.widget.AbstractWidget;

public class MagnifierWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	private static final boolean isTransparent = true;
	private static final int updateDelay = 10;
	private static final String iconPath = null;

	private long lastRender;

	public FocusDetector focusDetector;
	public BufferedImage img;
	public Robot robot;
	public Point mouse;
	
	public ColorFilter colorFilter;
	public Contraster contraster;
	public Crosshair crosshair;
	
	public int filterRGB;
	public int contrastValue;
	public Color crosshairColor;
	private double scale;
	private double zoomStep;
	
	
	public MagnifierWidget() {
		super(isTransparent, updateDelay, iconPath);
		
		setTitle("Magnifier");
		addKeyListener(new KeyCommands(this));
		setSize(500, 500);
		
		try {
			robot = new Robot();
		} catch (AWTException e) { e.printStackTrace(); }

		focusDetector = new FocusDetector();
		addFocusListener(focusDetector);
		mouse = new Point();
		
		reset();
	}
	
	
	public void reset() {
		colorFilter = ColorFilter.None;
		filterRGB = 0xFFFFFF;
		contraster = Contraster.None;
		contrastValue = 0;
		crosshair = Crosshair.None;
		crosshairColor = Color.red;
		setScale(1);
		zoomStep = 0.1;
	}
	
	
	@Override
	public void update() {
		collectScreenshot();
		applyProcessing();
	}
	
	
	@Override
	public void render(Graphics g) {
		if (img != null) {
			if (scale > 1) {// image too small
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						g.setColor(new Color(img.getRGB(x, y)));
						g.fillRect((int) (x * scale), (int) (y * scale), (int) (scale + 1), (int) (scale + 1));
					}
				}
			}
			else { // image too large
				for (int y = 0; y < getHeight(); y++) {
					for (int x = 0; x < getWidth(); x++) {
						g.setColor(new Color(img.getRGB((int) (x / scale), (int) (y / scale))));
						g.fillRect(x, y, 1, 1);
					}
				}
//				g.drawImage(img.getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_SMOOTH), 0, 0, null);
			}
//			g.drawImage(img.getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_REPLICATE), 0, 0, null);
			
			applyFocusBox(g);
			drawInfo(g);
			
			String str = "" + (System.currentTimeMillis() - lastRender);
			lastRender = System.currentTimeMillis();
			g.drawString(str, getWidth() - g.getFontMetrics().stringWidth(str) - 2, 15);
		}
	}
	private void applyFocusBox(Graphics g) {
		if (focusDetector.hasFocus) {
			g.setColor(Color.cyan);
			double w = getWidth()-1,
				   h = getHeight()-1;
			g.drawRect(0, 0, (int) w, (int) h);
		}
	}
	private void drawInfo(Graphics g) {
		int y = 0, x = 2, fontSZ = 12;
		g.setColor(Color.magenta);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSZ));
		
		g.drawString(String.format("%.3fx", scale), x, y += fontSZ);
		
		if (colorFilter != ColorFilter.None) {
			String filterName = String.format("Filter [0x%06X]", filterRGB & 0xFFFFFF);
			g.drawString(filterName, x, y += fontSZ);
		}
		
		if (contraster != Contraster.None) {
			String contrasterName = "Contraster: " + contraster.name().replace('_', ' ') + " @ " + contrastValue;
			g.drawString(contrasterName, x, y += fontSZ);
		}
		
		if (crosshair != Crosshair.None) {
			String crosshairName = "Crosshair: " + crosshair.name().replace('_', ' ');
			g.drawString(crosshairName, x, y += fontSZ);
		}
	}
	
	public void collectScreenshot() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		
		if (pointerInfo != null) {
			mouse = pointerInfo.getLocation();
			int w = Math.max(1, (int) (getWidth() / scale)),
				h = Math.max(1, (int) (getHeight() / scale));
			
			Rectangle capture = new Rectangle(mouse.x - w/2, mouse.y - h/2, w, h);
			img = robot.createScreenCapture(capture);
		}
	}
	
	public void applyProcessing() {
		colorFilter.apply(img, filterRGB);
		contraster.apply(img, contrastValue);
		crosshair.apply(img, crosshairColor);
	}
	
	
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		if (scale < zoomStep) {
			this.scale = zoomStep;
		}
		else {
			this.scale = scale;
		}
	}


	public double getZoomStep() {
		return zoomStep;
	}
	
}
