package com.WidgetHub.widget.todo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import com.WidgetHub.widget.AbstractWidget;

/**
 * Simple virtual todo list with transparent background.
 * 
 * @author Daniel Struck
 *
 */
public class TodoWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	// settings
	private static final int START_SIZE = 200;
	private static final String FILE_PATH = System.getProperty("user.dir") + "/Todo Elements.txt";
	
	// constructor info
	private static final boolean isTransparent = true;
	private static final int updateDelay = 50;
	private static final String iconPath = "/todo icon.png";
	
	// instance-specific
	private ArrayList<TodoElement> elements;
	private int spacing;
	private boolean minimized;
	private int yScroll;

	private enum ClickType { PRESS, CLICK, RELEASE }
	private class MouseControl extends MouseAdapter {
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			yScroll += e.getWheelRotation() * (panel.getWidth() + spacing) / 6;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			notifyElement(e, ClickType.PRESS);
		}
	
		@Override
		public void mouseReleased(MouseEvent e) {
			notifyElement(e, ClickType.RELEASE);
		}
	
		@Override
		public void mouseClicked(MouseEvent e) {
			notifyElement(e, ClickType.CLICK);
		}
		
		private void notifyElement(MouseEvent e, ClickType clickType) {
			TodoElement elem = elementAt(e.getY());
			Point adjustedClick = new Point(e.getX(), e.getY() - elementYPosition(elem));
			
			if (elem != null) {
				switch (clickType) {
					case PRESS:
						elem.onMousePress(e, adjustedClick);
					break;
					case CLICK:
						elem.onMouseClick(e, adjustedClick);
					break;
					case RELEASE:
						elem.onMouseRelease(e, adjustedClick);
					break;
				}
				saveToFile();
			}
		}
		private TodoElement elementAt(int y) {
			y += yScroll;
			
			for (TodoElement event: elements) {
				y -= event.getHeight(panel.getWidth());
				y -= spacing;
				
				if (y < 0) {
					return event;
				}
			}
			
			return null;
		}
		private int elementYPosition(TodoElement elem) {
			int y = -yScroll;
			
			for (TodoElement event: elements) {
				if (event == elem)
					return y;
				
				y += event.getHeight(panel.getWidth()) + spacing;
			}
			
			return -1;
		}
	}
	private class KeyControl extends KeyAdapter {
		private Window window;
		
		public KeyControl(Window window) {
			this.window = window;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_HOME: // returns the widget to the center of the screen
					window.setLocationRelativeTo(null);
				break;
			}
		}
	}
	
	
	public TodoWidget() {
		super(isTransparent, updateDelay, iconPath);
		
		setTitle("Todo Widget");
		resizeTo(START_SIZE);
		
		yScroll = 0;
		
		elements = new ArrayList<TodoElement>();
		readInFile();
		
		MouseControl mouseControl = new MouseControl();
		panel.addMouseListener(mouseControl);
		panel.addMouseWheelListener(mouseControl);
		
		KeyControl keyControl = new KeyControl(this);
		addKeyListener(keyControl);
	}
	private void readInFile() {
		File file = new File(FILE_PATH);
		if (file.exists()) {
			try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file))) {
				spacing = stream.readInt();
				minimized = stream.readBoolean();
				resizeTo(stream.readInt());
				elements = ((ArrayList<TodoElement>) stream.readObject());
				for (TodoElement e: elements)
					e.setWidget(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			spacing = 3;
			minimized = false;
			elements.add(new AddEvent(this));
		}
	}
	private void saveToFile() {
		File file = new File(FILE_PATH);
		if (file.exists()) {
			try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file))) {
				stream.writeInt(spacing);
				stream.writeBoolean(minimized);
				stream.writeInt(getWidth());
				stream.writeObject(elements);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
	}
	public boolean isMinimized() {
		return minimized;
	}
	
	
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}
	public int getSpacing() {
		return spacing;
	}
	
	public void resizeTo(int size) {
		if (size < 100)
			setSize(100, 300);
		else
			setSize(size, size * 3);
	}
	
	
	/**
	 * Removes closed elements
	 */
	public void flush() {
		for (int i = elements.size() - 1; i >= 0; i--) {
			elements.get(i).flush();
			
			if (elements.get(i).hasClosed()) {
				elements.remove(i);
			}
		}
	}
	
	
	public void addElement(TodoElement element) {
		elements.add(element);
	}
	
	
	@Override
	public void update() {
		Collections.sort(elements);
		
		LocalDateTime now = LocalDateTime.now();
		for (TodoElement elem: elements) {
			if (elem.getDateTime().compareTo(now) < 0)
				elem.alert();
			else
				break;
		}
		
	
		int scrollMax = 0;
		
		for (int i = 0; i < elements.size() - 1; i++)
			scrollMax += elements.get(i).getHeight(this.getWidth()) + spacing;
		
		if (yScroll < 0)
			yScroll = 0;
		else if (yScroll > scrollMax)
			yScroll = scrollMax;
		
		if (elements.size() > 0) {
			int yMax = scrollMax + elements.get(elements.size() - 1).getHeight(this.getWidth());
			setSize(getWidth(), Math.min(getWidth() * 3, yMax - yScroll + 1));
		}
	}
	
	@Override
	public void render(Graphics g) {
		int y = -yScroll;
		
		for (int i = 0; i < elements.size(); i++) {
			TodoElement element = elements.get(i);
			int elementHeight = element.getHeight(panel.getWidth());
			
			if (y + elementHeight > 0) {
				if (y > getHeight())
					break;
				
				BufferedImage img = new BufferedImage(panel.getWidth(), elementHeight, BufferedImage.TYPE_INT_ARGB);
				element.renderElement(img);
				g.drawImage(img, 0, y, null);
			}
			
			y += elementHeight;
			y += spacing;
		}
		
		
		g.setColor(new Color(127, 127, 127, 10));
		g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
	}
}
