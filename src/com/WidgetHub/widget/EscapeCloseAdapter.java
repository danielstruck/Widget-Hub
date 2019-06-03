package com.WidgetHub.widget;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

public class EscapeCloseAdapter extends KeyAdapter {
	private AbstractWidget widget;
	
	public EscapeCloseAdapter(AbstractWidget widget) {
		this.widget = widget;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				if (JOptionPane.showConfirmDialog(widget, "Confirm close?") == JOptionPane.OK_OPTION)
					widget.destroy();
			break;
		}
	}
}
