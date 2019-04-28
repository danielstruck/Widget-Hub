package com.WidgetHub.widget.todo;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.ContextMenu;
import com.WidgetHub.widget.Tone.Note;

public class TodoTools {
	private static volatile boolean alertFlag;
	
	private TodoTools() {}
	
	public static void showContextMenu(MouseEvent event, TodoElement element, TodoWidget widget) {
		ContextMenu contextMenu = new ContextMenu("Menu");
		
		
		element.applyCustomContextMenu(contextMenu);
		
		contextMenu
				.addItem((!widget.isMinimized())? "Minimize": "Maximize", (action) -> {
					widget.setMinimized(!widget.isMinimized());
				})
				.addItem("Change Size", (action) -> {
					String input = null;
					try {
						input = JOptionPane.showInputDialog("Current width: " + widget.getPanelSize().width + " px. Input frame width in pixels:");
						int width = Integer.parseInt(input);
						widget.setSize(width, widget.getHeight());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(widget, "Input is not an integer: " + input);
					}
				})
				.addItem("Change Spacing", (action) -> {
					String input = null;
					try {
						input = JOptionPane.showInputDialog("Input spacing in pixels:");
						int spacing = Integer.parseInt(input);
						widget.setSpacing(spacing);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(widget, "Input is not an integer: " + input);
					}
				})
				.addItem("Exit", (action) -> {
					if (JOptionPane.showConfirmDialog(widget, "Confirm exit?") == JOptionPane.OK_OPTION)
						widget.close();
				});
		
		contextMenu.show(event.getComponent(), event.getX(), event.getY());
	}

	
	public static void alertBeep() {
		if (!alertFlag) {
			alertFlag = true;
			new Thread(() -> {
				Note.play(100, "A4 B4");
				alertFlag = false;
			}).start();
		}
	}
}