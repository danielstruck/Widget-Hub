package com.WidgetHub.widget;

import java.awt.event.ActionListener;
import java.util.function.Predicate;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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
		if (condition.test(input))
			addItem(name, action);
		
		return this;
	}
}
