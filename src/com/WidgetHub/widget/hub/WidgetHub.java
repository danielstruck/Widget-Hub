package com.WidgetHub.widget.hub;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import com.WidgetHub.widget.AbstractWidget;
import com.WidgetHub.widget.BagGridPane;
import com.WidgetHub.widget.clipboardViewer.ClipboardWidget;
import com.WidgetHub.widget.clock.ClockWidget;
import com.WidgetHub.widget.fractal.FractalWidget;
import com.WidgetHub.widget.memory.MemoryGameWidget;
import com.WidgetHub.widget.timer.TimerWidget;
import com.WidgetHub.widget.todo.TodoWidget;

// TODO reduce CPU impact

/**
 * One widget to rule them all. This hub keeps track of active widgets.
 * 
 * @author Daniel Struck
 *
 */
public class WidgetHub extends AbstractWidget {
	private static final long serialVersionUID = 1L;

	// constructor info
	private static final boolean isTransparent = false;
	private static final int updateDelay = 250;
	private static final String iconPath = null; // TODO make widget hub icon
	
	// TODO concatenate widget JFrames together to make 1 JFrame
	// instance variables
	private WidgetCheckBox<ClipboardWidget> clipboardCheckBox;
	private WidgetCheckBox<ClockWidget> clockCheckBox;
	private WidgetCheckBox<FractalWidget> fractalCheckBox;
	private WidgetCheckBox<MemoryGameWidget> memoryCheckBox;
	private WidgetCheckBox<TimerWidget> timeCheckBox;
	private WidgetCheckBox<TodoWidget> todoCheckBox;
	

	public WidgetHub() {
		super(isTransparent, updateDelay, iconPath);
		
		setTitle("Widget Hub");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		 // TODO Widgets sometimes open twice
		clipboardCheckBox = new WidgetCheckBox<ClipboardWidget>(ClipboardWidget.class);
		clockCheckBox	  = new WidgetCheckBox<ClockWidget>(ClockWidget.class); // TODO ClockWidget sometimes fails to open properly when another widget is open first
		fractalCheckBox	  = new WidgetCheckBox<FractalWidget>(FractalWidget.class);
		memoryCheckBox	  = new WidgetCheckBox<MemoryGameWidget>(MemoryGameWidget.class);
		timeCheckBox	  = new WidgetCheckBox<TimerWidget>(TimerWidget.class);
		todoCheckBox	  = new WidgetCheckBox<TodoWidget>(TodoWidget.class);
		
		BagGridPane gridPane = new BagGridPane();
		gridPane.addRow(clipboardCheckBox);
		gridPane.addRow(clockCheckBox);
		gridPane.addRow(fractalCheckBox);
		gridPane.addRow(memoryCheckBox);
		gridPane.addRow(timeCheckBox);
		gridPane.addRow(todoCheckBox);
		
		panel.add(gridPane, BorderLayout.NORTH);
		pack();
		setSize(Math.max(250, getWidth()), getHeight());
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		
	}
	
	
	// TODO add functionality for saving the state of the hub; ie: which widget is open, etc
	
	
	// TODO some widgets fail to have their size set properly, resulting in widgets not appearing on the screen
}

class WidgetCheckBox<Widget_T extends AbstractWidget> extends JCheckBox implements ItemListener {
	private static final long serialVersionUID = 1L;
	private Class<Widget_T> widgetClass;
	private Widget_T widget;
	private boolean widgetOpen;
	
	
	public WidgetCheckBox(Class<Widget_T> widgetClass) {
		super(widgetClass.getSimpleName());
		
		this.widgetClass = widgetClass;
		widgetOpen = false;
		
		addItemListener(this);
	}
	
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		
		if (source == this) {
			setWidgetOpen(!widgetOpen);
		}
	}
	
	
	public void setWidgetOpen(boolean widgetOpen) {
		this.widgetOpen = widgetOpen;
		
		if (widget != null) {
			widget.setVisible(widgetOpen);
		}
		else {
			try {
				widget = widgetClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	public boolean getWidgetOpen() {
		return widgetOpen;
	}
	
	public Widget_T getWidget() {
		return widget;
	}
}
