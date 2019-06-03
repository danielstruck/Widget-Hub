package com.WidgetHub.widget.todo;

import com.WidgetHub.tone.Note;

public class TodoTools {
	private static volatile boolean alertFlag;
	
	private TodoTools() {}

	
	public static void alertBeep(int duration, String pattern) {
		if (!alertFlag) {
			alertFlag = true;
			new Thread(() -> {
				Note.playSinNotes(duration, pattern);
				alertFlag = false;
			}).start();
		}
	}
}
