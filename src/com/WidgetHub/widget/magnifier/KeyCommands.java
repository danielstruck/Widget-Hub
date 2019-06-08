package com.WidgetHub.widget.magnifier;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class KeyCommands implements KeyListener {
	private final int[] filterColors;
	private int filterColorsIndex;
	private int filterIndex;
	private int contrasterIndex;
	private int crosshairIndex;
	private MagnifierWidget viewer;
	
	public KeyCommands(MagnifierWidget viewer) {
		this.viewer = viewer;
		filterColorsIndex = 0;
		filterIndex = 0;
		contrasterIndex = 0;
		crosshairIndex = 0;
		
		filterColors = new int[] {
				0xFFFFFF,
				0xFF0000,
				0x00FF00,
				0x0000FF,
				0x00FFFF,
				0xFF00FF,
				0xFFFF00
		};
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ADD:
				if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
					viewer.setScale(viewer.getScale() * 1.1);
				}
				else {
					viewer.setScale(viewer.getScale() + viewer.getZoomStep());
				}
			break;
			case KeyEvent.VK_SUBTRACT:
				if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
					viewer.setScale(viewer.getScale() * 0.9);
				}
				else {
					viewer.setScale(viewer.getScale() - viewer.getZoomStep());
				}
			break;
			case KeyEvent.VK_F:
				if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
					filterColorsIndex = (++filterColorsIndex) % (filterColors.length);
					viewer.filterRGB = (filterColors[filterColorsIndex]);
				}
				else {
					if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0) {
						filterIndex = (--filterIndex + ColorFilter.values().length) % ColorFilter.values().length;
					}
					else {
						filterIndex = (++filterIndex) % ColorFilter.values().length;
					}
					
					viewer.colorFilter = (ColorFilter.values()[filterIndex]);
				}
			break;
			case KeyEvent.VK_X:
				crosshairIndex = (++crosshairIndex) % Crosshair.values().length;
				viewer.crosshair = (Crosshair.values()[crosshairIndex]);
			break;
			case KeyEvent.VK_C:
				if ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
					int newVal = viewer.contrastValue;
					int mult = 1;
					
					if ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
						mult = 10;
					}
					
					if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0) {
						newVal -= mult;
					}
					else {
						newVal += mult;
					}
					
					viewer.contrastValue = newVal;
				}
				else {
					if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0) {
						contrasterIndex = (--contrasterIndex + Contraster.values().length) % Contraster.values().length;
					}
					else {
						contrasterIndex = (++contrasterIndex) % Contraster.values().length;
					}
					
					viewer.contraster = Contraster.values()[contrasterIndex];
				}
			break;
			case KeyEvent.VK_S:
				if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
					BufferedImage imgToSave = viewer.img;
					JFileChooser fc = new JFileChooser();
					fc.showOpenDialog(null);
					String path = fc.getSelectedFile().getAbsolutePath();
					if (!path.endsWith(".png"))
						path += ".png";
					File file = new File(path);
					try {
						ImageIO.write(imgToSave, "PNG", file);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
					}
				}
			break;
			case KeyEvent.VK_DOWN:
				viewer.setSize(viewer.getWidth(), viewer.getHeight() - 5);
			break;
			case KeyEvent.VK_UP:
				viewer.setSize(viewer.getWidth(), viewer.getHeight() + 5);
			break;
			case KeyEvent.VK_LEFT:
				viewer.setSize(viewer.getWidth() - 5, viewer.getHeight());
			break;
			case KeyEvent.VK_RIGHT:
				viewer.setSize(viewer.getWidth() + 5, viewer.getHeight());
			break;
			case KeyEvent.VK_ESCAPE:
				if (JOptionPane.showConfirmDialog(viewer, "Confirm close?") == JOptionPane.YES_OPTION)
					System.exit(0);
			break;
			case KeyEvent.VK_HOME:
				if ((e.getModifiersEx() & (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK)) != 0) {
					viewer.reset();
				}
				else {
					viewer.setScale(1);
				}
			break;
		}
	}
}