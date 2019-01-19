package com.WidgetHub.widget;

import java.awt.event.ActionListener;
import java.util.function.Predicate;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
/**
 * Implements a right click context menu. Methods allow chaining.
 * 
 * @author Daniel
 * 
 * NOTE: right click menu implementation here - https://stackoverflow.com/questions/766956/how-do-i-create-a-right-click-context-menu-in-java-swing
 */
public class ContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;


	public ContextMenu() {
		
	}
	public ContextMenu(String label) {
		super(label);
	}
	
	
	public ContextMenu addItem(String name, ActionListener action) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(action);
		super.add(item);
		
		return this;
	}
	public <T> ContextMenu addItemIf(Predicate<T> condition, T input, String name, ActionListener action) {
		return addItemIf(condition.test(input), name, action);
	}
	public ContextMenu addItemIf(boolean condition, String name, ActionListener action) {
		if (condition)
			addItem(name, action);
		
		return this;
	}
}
