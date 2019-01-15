package com.WidgetHub.main;

import com.WidgetHub.widget.clipboardViewer.ClipboardWidget;
import com.WidgetHub.widget.clock.ClockWidget;
import com.WidgetHub.widget.fractal.FractalWidget;
import com.WidgetHub.widget.hub.WidgetHub;
import com.WidgetHub.widget.memory.MemoryGameWidget;
import com.WidgetHub.widget.timer.TimerWidget;
import com.WidgetHub.widget.todo.TodoWidget;

public class WidgetHubRunner {
	// Note: right click menu implementation here - https://stackoverflow.com/questions/766956/how-do-i-create-a-right-click-context-menu-in-java-swing
	public static void main(String[] args) {
		System.out.println("HUB START");
		
		new ClockWidget();

		new TodoWidget();//.test();
		
		new TimerWidget();
		
		new FractalWidget();
		
		new ClipboardWidget();
		
		new MemoryGameWidget();
		
		new WidgetHub();
		
		System.out.println("HUB END");
	}
}
