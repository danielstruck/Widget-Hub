package com.WidgetHub.main;

import com.WidgetHub.widget.clipboardViewer.ClipboardWidget;

public class WidgetHubRunner {
	// Note: right click menu implementation here - https://stackoverflow.com/questions/766956/how-do-i-create-a-right-click-context-menu-in-java-swing
	public static void main(String[] args) {
		System.out.println("HUB START");
//		new ClockWidget();

//		new TodoWidget();//.test();
		
//		LocalDateTime now = LocalDateTime.now();
//		System.out.println(TodoEvent.defaultDateFormat.format(now));
//		System.out.println(TodoEvent.defaultTimeFormat.format(now));
		
//		new TimerWidget();
		
//		new FractalWidget();
		
		new ClipboardWidget();
		
//		new MemoryGame();
		
		System.out.println("HUB END");
	}
}
