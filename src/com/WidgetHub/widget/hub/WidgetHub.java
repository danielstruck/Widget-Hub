package com.WidgetHub.widget.hub;

import java.awt.Graphics;
import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.WidgetHub.widget.AbstractWidget;

/**
 * One widget to rule them all. This hub keeps track of active widgets.
 * 
 * @author Daniel Struck
 *
 */
public class WidgetHub extends AbstractWidget {
	private static final long serialVersionUID = 1L;

	// constructor info
	private static final boolean isTransparent = false;
	private static final int updateDelay = 50;
	private static final String iconPath = null;
	
	// instance variables
	private JCheckBox clipboardCheckBox;
	private JCheckBox clockCheckBox;
	private JCheckBox fractalCheckBox;
	private JCheckBox memoryCheckBox;
	private JCheckBox timeCheckBox;
	private JCheckBox todoCheckBox;
	

	public WidgetHub() {
		super(isTransparent, updateDelay, iconPath);
		

		clipboardCheckBox = new JCheckBox();
		clockCheckBox	  = new JCheckBox();
		fractalCheckBox	  = new JCheckBox();
		memoryCheckBox	  = new JCheckBox();
		timeCheckBox	  = new JCheckBox();
		todoCheckBox	  = new JCheckBox();
	}
	public static void addBagLine(GridBagConstraints constraints, int colStart, int row, JComponent parent, String label, JComponent ...components) {
		colStart = colStart * 2;
		
		if (constraints == null) {
			constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.WEST;
			constraints.ipadx = 10;
			constraints.ipady = 4;
			constraints.fill = GridBagConstraints.HORIZONTAL;
		}
		
		addBagToPanel(constraints, colStart, row, parent, new JLabel(label));
		addBagToPanel(constraints, colStart + 1, row, parent, components);
	}
	public static void addBagToPanel(GridBagConstraints constraints, int col, int row, JComponent parent, JComponent ...components) {
		constraints.gridy = row;
		constraints.gridx = col;
		for (JComponent component: components) {
			parent.add(component, constraints);
			constraints.gridx++;
		}
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		
	}
}
