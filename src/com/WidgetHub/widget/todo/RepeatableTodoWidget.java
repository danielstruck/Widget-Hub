package com.WidgetHub.widget.todo;

import java.io.BufferedReader;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.ContextMenu;

public class RepeatableTodoWidget extends TodoEvent {
	private boolean[] repeats;
	
	public RepeatableTodoWidget(TodoWidget widget) {
		super(widget);
		
		repeats = new boolean[7];
		// TODO Auto-generated constructor stub
	}
	
	
	public RepeatableTodoWidget(TodoWidget widget, BufferedReader reader) {
		super(widget, reader);
		
		repeats = new boolean[7];
		
		try {
			String repeatData = reader.readLine();
			for (int i = 0; i < repeats.length; i++)
				repeats[i] = repeatData.charAt(i) == '+';
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Dismiss", (action) -> {
						// TODO code for repeatable dismiss; sets dateTime to next alarm
					});
	}
	
	
	private boolean repeatsOnDay(DayOfWeek day) {
		return repeats[day.getValue() - 1];
	}
	
	
	@Override
	public String data() {
		String tor = getClass().getSimpleName() + "\n";
		
		tor += super.data();
		
		for (boolean day: repeats)
			tor += day? "+": "_";
		tor += "\n";
		
		return tor;
	}
}
