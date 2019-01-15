package com.WidgetHub.widget;

public class Toolbox {
	
	private Toolbox() {}

	public static void note(Class<?> c, String s) {
		System.out.println("[" + c.getSimpleName() + "] " + s);
	}
}
