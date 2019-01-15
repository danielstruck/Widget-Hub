package com.WidgetHub.widget;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class EscapeCloseAdapter extends KeyAdapter {
	private Component component;
	
	public EscapeCloseAdapter(Component component) {
		this.component = component;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			if (JOptionPane.showConfirmDialog(component, "Confirm close?") == JOptionPane.OK_OPTION)
				System.exit(0);
		break;
		}
	}
}
