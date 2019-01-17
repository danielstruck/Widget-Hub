package com.WidgetHub.widget;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BagGridPane extends JPanel {
	private static final long serialVersionUID = 1L;
	GridBagConstraints constraints;
	
	private int currentRow;
	
	public BagGridPane() {
		constraints = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		
		constraints.anchor = GridBagConstraints.WEST;
		constraints.ipadx = 10;
		constraints.ipady = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		currentRow = 0;
	}
	
	public void addRow(Component ...components) {
		for (int col = 0; col < components.length; col++)
			setCell(col, currentRow, components[col]);
		
		currentRow++;
	}
	public void setCell(int x, int y, Component component) {
		constraints.gridy = y;
		constraints.gridx = x;
		this.add(component, constraints);
	}
	
	public int showConfirmDialog(Component parent) {
		return JOptionPane.showConfirmDialog(parent, this, "Event Editor", JOptionPane.OK_CANCEL_OPTION);
	}
}