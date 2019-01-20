package com.WidgetHub.widget.todo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.AbstractWidget;

/**
 * Simple virtual todo list with transparent background.
 * 
 * @author Daniel Struck
 *
 */
public class TodoWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	public static final DateTimeFormatter TIME_DATE_FORMAT = DateTimeFormatter.ofPattern("mm/dd/yyyy h:mm a"),
										  TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a"),
										  DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d");
	
	// settings
	private static final int START_SIZE = 200;
	private static final String FILE_PATH = System.getProperty("user.dir") + "/Todo Elements.txt";
	
	// constructor info
	private static final boolean isTransparent = true;
	private static final int updateDelay = 50;
	private static final String iconPath = "/todo icon.png";
	
	// instance-specific
	private final ArrayList<TodoElement> elements;
	private int spacing;
	private boolean minimized;
	private int yOffset;
	
	
	public TodoWidget() {
		super(isTransparent, updateDelay, iconPath);
		
		setTitle("Todo Widget");
		resizeTo(START_SIZE);
		
		spacing = 3;
		minimized = false;
		yOffset = 0;
		
		elements = new ArrayList<TodoElement>();
		elements.add(new AddEvent(this));
		readInFile();
		
		MouseAdapter mouseControl = new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				yOffset += e.getWheelRotation() * (panel.getWidth() + spacing) / 6;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				TodoElement elem = elementAt(e.getY());
				if (elem != null)
					elem.onMousePress(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				TodoElement elem = elementAt(e.getY());
				if (elem != null)
					elem.onMouseRelease(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				TodoElement elem = elementAt(e.getY());
				if (elem != null)
					elem.onMouseClick(e);
			}
		
			private TodoElement elementAt(int y) {
				y += yOffset;
				
				for (TodoElement event: elements) {
					y -= event.getHeight(panel.getWidth());
					y -= spacing;
					
					if (y < 0) {
						return event;
					}
				}
				
				return null;
			}
		};
		panel.addMouseListener(mouseControl);
		panel.addMouseWheelListener(mouseControl);
	}
	private void readInFile() {
		ArrayList<Class<? extends TodoElement>> elementTypes = new ArrayList<Class<? extends TodoElement>>();
		elementTypes.add(TodoEvent.class);
		elementTypes.add(RepeatableTodoEvent.class);

		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
			String line = reader.readLine();
			try {
				String[] settings = line.split(",");
				spacing = Integer.parseInt(settings[0]);
				minimized = Boolean.parseBoolean(settings[1]);
				resizeTo(Integer.parseInt(settings[2]));
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Failed to read Todo List settings: " + line);
			}
			
			while ((line = reader.readLine()) != null) {
				for (Class<? extends TodoElement> type: elementTypes) {
					if (line.equals(type.getSimpleName())) {
						addElement(type.getConstructor(TodoWidget.class, BufferedReader.class).newInstance(this, reader));
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void saveToFile() {
		StringBuilder b = new StringBuilder();
		
		b.append(spacing + "," + minimized + "," + getWidth() + "\n");
		
		for (int i = 1; i < elements.size(); i++)
			b.append(elements.get(i).data());
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
			writer.write(b.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TodoWidget test() {
		for (int i = 0; i < 3; i++) {
			TodoEvent e = new TodoEvent(this);
			int minOffset = new Random().nextInt(20) - 10;
			System.out.println("minOffset " + i + ": " + minOffset);
			e.setDateTime(LocalDateTime.now().plusMinutes(minOffset));
			e.setDetails("Test " + (i + 1));
			e.setLocation("Road " + (i + 1) + " West");
			elements.add(e);
		}
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_1:
						minimized = true;
					break;
					case KeyEvent.VK_2:
						minimized = false;
					break;
				}
				
				System.out.println("pressed " + e.getKeyChar());
			}
		});
		
		return this;
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
	public void resizeTo(int size) {
		if (size < 100)
			size = 100;
		setSize(size, size * 3);
	}
	
	
	/**
	 * Removes closed elements
	 */
	public void flush() {
		for (int i = elements.size() - 1; i >= 0; i--)
			if (elements.get(i).hasClosed())
				elements.remove(i);
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
		
		for (int i = 0; i < elements.size() - 1; i++) {
			scrollMax += elements.get(i).getHeight(this.getWidth());
			scrollMax += spacing;
		}
		
		if (yOffset < 0)
			yOffset = 0;
		else if (yOffset > scrollMax)
			yOffset = scrollMax;
		
		if (elements.size() > 0) {
			int yMax = scrollMax + elements.get(elements.size() - 1).getHeight(this.getWidth());
			setSize(getWidth(), Math.min(getWidth() * 3, yMax - yOffset + 1));
		}
		
		
		saveToFile();
	}
	
	@Override
	public void render(Graphics g) {
		int y = -yOffset;
		
		render:
		for (int i = 0; i < elements.size(); i++) {
			TodoElement element = elements.get(i);
			int elementHeight = element.getHeight(getWidth());
			
			if (y + elementHeight > 0) {
				if (y > getHeight())
					break render;
				
				element.render(g, y, panel.getWidth());
			}
			
			y += elementHeight;
			y += spacing;
		}
		
		
		g.setColor(new Color(127, 127, 127, 10));
		g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
	}
}
