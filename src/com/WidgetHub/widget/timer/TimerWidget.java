package com.WidgetHub.widget.timer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import com.WidgetHub.widget.AbstractWidget;

/**
 * Simple timer widget that can keep track of multiple timers. Geared toward accounting.
 * 
 * @author Daniel Struck
 *
 */
public class TimerWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	// constructor info
	private static final boolean isTransparent = true;
	private static final int updateDelay = 50;
	private static final String iconPath = null;
	
	// timer constants
	public static final ArrayList<CStopWatch> stopwatches = new ArrayList<CStopWatch>();
	public static int currentScroll = 0;
	public static boolean onlyOneRunning = true;

	public TimerWidget() {
		super(isTransparent, updateDelay, iconPath);
		setTitle("Timer Widget");
		
		try {
			setup();
		}
		catch (Exception e) {
			File errorFile = new File(System.getProperty("user.home") + "\\Desktop\\this is a bad error - Daniel.txt");
			try (PrintStream writer = new PrintStream(new FileOutputStream(errorFile))) {
				writer.print("This is a bad error. Send this document to my EMail: CowCow98@gmail.com so that I can try to fix it.\n\n");
				e.printStackTrace(writer);
			}
			catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Unable to create error log.", "Error durring operation", JOptionPane.ERROR_MESSAGE);
				close();
			}
			JOptionPane.showMessageDialog(null, "Error data has been logged. \nSee file on desktop for more information.", "Error durring operation", JOptionPane.ERROR_MESSAGE);
			close();
		}
	}
	private void setup() throws Exception {
		setBackground(new Color(0, 0, 0, 100));
		resizeWindow(3);
		TimerWidget widget = this;
		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
// System.out.println(e.getModifiers() + "/" + (MouseEvent.BUTTON3 +
// KeyEvent.VK_CONTROL));
				try {
					Point mouse = new Point(e.getX() - 8, e.getY() - 23);
					if (mouse.x > CStopWatch.CUSHION && mouse.x < CStopWatch.CUSHION + CStopWatch.SIZE * 2 && mouse.y >= 0) {
						int index = (mouse.y + currentScroll) / (CStopWatch.SIZE + CStopWatch.CUSHION);
						if (index < stopwatches.size()) {
							if (e.getButton() == MouseEvent.BUTTON1) {
								// rename timer with ctrl + click
								if (e.getModifiers() == MouseEvent.BUTTON1 + KeyEvent.VK_CONTROL) {
									String newName = JOptionPane.showInputDialog(this, "Rename timer '" + stopwatches.get(index).getName() + "'");
									if (newName != null) stopwatches.get(index).setName(newName);
								}
								// toggle pause of clicked stop watch
								else {
									if (onlyOneRunning && stopwatches.get(index).isPaused()) for (CStopWatch sw: stopwatches)
										sw.setPaused(true);
									stopwatches.get(index).togglePaused();
								}
							}
							else if (e.getButton() == MouseEvent.BUTTON3) {
								if (JOptionPane.showConfirmDialog(widget, "Reset timer?", "Timer reset", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) stopwatches.get(index).reset();
							}
							else if (e.getButton() == MouseEvent.BUTTON2) {
								if (JOptionPane.showConfirmDialog(widget, "Are you sure you want to delete this timer?\nThis action cannot be undone.", "Delete timer", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) stopwatches.remove(index);
							}
						}
					}
				}
				catch (IndexOutOfBoundsException exc) {
					exc.printStackTrace();
				}
			}
		});
		this.addMouseWheelListener((e) -> {
			int scrollAmount = e.getWheelRotation() * (CStopWatch.SIZE + CStopWatch.CUSHION);
			if (currentScroll + scrollAmount < 0) return;
			int largestScroll = (stopwatches.size() - 1) * (CStopWatch.SIZE + CStopWatch.CUSHION);
			if (currentScroll + scrollAmount > largestScroll) return;
			currentScroll += scrollAmount;
		});
		
		setupToolbar();
		
		this.revalidate();
	}
	private void setupToolbar() throws Exception {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		// FILE MENU
		JMenuItem plainTimer = generateMenuOption("Plain", (e) -> {
			String name = JOptionPane.showInputDialog(this, "Name the timer:");
			if (name == null) return;
			stopwatches.add(new CStopWatch(name));
		});
		JMenuItem associateTimer = generateMenuOption("Associate Timer (AST)", (e) -> {
			String name = JOptionPane.showInputDialog(this, "Name the timer:");
			if (name == null) return;
			stopwatches.add(new CMoneyWatch(name, 60, "AST"));
		});
		JMenuItem adminTimer = generateMenuOption("Admin Timer (ADM)", (e) -> {
			String name = JOptionPane.showInputDialog(this, "Name the timer:");
			if (name == null) return;
			stopwatches.add(new CMoneyWatch(name, 90, "ADM"));
		});
		JMenuItem consultantTimer = generateMenuOption("Consultant Timer (CSL)", (e) -> {
			String name = JOptionPane.showInputDialog(this, "Name the timer:");
			if (name == null) return;
			stopwatches.add(new CMoneyWatch(name, 120, "CSL"));
		});
		JMenuItem customTimer = generateMenuOption("Custom Timer (CSM)", (e) -> {
			String name = JOptionPane.showInputDialog(this, "Name the timer:");
			String hourlyRate = JOptionPane.showInputDialog(this, "What is the hourly rate?");
			if (name == null || hourlyRate == null) return;
			try {
				stopwatches.add(new CMoneyWatch(name, Float.parseFloat(hourlyRate.trim()), "CSM"));
			}
			catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(this, "Could not read '" + hourlyRate + "' as a number");
			}
		});
		JMenuItem downTimer = generateMenuOption("Down Timer", (e) -> {
			String name = JOptionPane.showInputDialog(this, "Name the timer:");
			String hourlyRate = JOptionPane.showInputDialog(this, "Start time in h:m:s format:");
			if (name == null || hourlyRate == null) return;
			try {
				String[] startTimeString = hourlyRate.split(":");
				int h = Integer.parseInt(startTimeString[0]), m = Integer.parseInt(startTimeString[1]),
						s = Integer.parseInt(startTimeString[2]);
				long startTime = h * 3600 + m * 60 + s;
				stopwatches.add(new CDownTimer(name, startTime));
			}
			catch (IndexOutOfBoundsException | NumberFormatException exc) {
				JOptionPane.showMessageDialog(this, "Could not read '" + hourlyRate + "' as a number");
			}
		});
		JMenu add = generateMenu("New", plainTimer, associateTimer, adminTimer, consultantTimer, customTimer, downTimer);
		JMenu file = generateMenu("File", add);
		menuBar.add(file);
		menuBar.add(new JSeparator(JSeparator.VERTICAL));
		// TOOLS MENU
		JMenuItem pauseAll = generateMenuOption("Pause All", (e) -> {
			for (CStopWatch sw: stopwatches)
				sw.setPaused(true);
		});
		JMenuItem unpauseAll = generateMenuOption("Unpause All", (e) -> {
			for (CStopWatch sw: stopwatches)
				sw.setPaused(false);
		});
		JMenuItem deleteAll = generateMenuOption("Delete All", (e) -> {
			String s1 = "Are you sure you want to delete all timers?\nDeleting all timers cannot be undone.",
					s2 = "Deleting all timers is final. Are you sure you want to delete all timers?";
			if (JOptionPane.showConfirmDialog(this, s1, "Delete All Timers", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) if (JOptionPane.showConfirmDialog(this, s2, "Delete All Timers", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) stopwatches.clear();
		});
		JMenuItem resetAll = generateMenuOption("Reset All", (e) -> {
			String s1 = "Are you sure you want to reset all timers?\nReseting all timers cannot be undone.",
					s2 = "Reseting all timers is final. Are you sure you want to reset all timers?";
			if (JOptionPane.showConfirmDialog(this, s1, "Reset All Timers", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) if (JOptionPane.showConfirmDialog(this, s2, "Reset All Timers", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) for (CStopWatch sw: stopwatches)
				sw.reset();
		});
		JMenuItem centerWindow = generateMenuOption("Center Window", (e) -> {
			this.setLocationRelativeTo(null);
		});
		JMenu tools = generateMenu("Tools", pauseAll, unpauseAll, deleteAll, resetAll, centerWindow);
		menuBar.add(tools);
		menuBar.add(new JSeparator(JSeparator.VERTICAL));
		// OPTIONS MENU
		JMenuItem help = generateMenuOption("Help", (e) -> {
			for (String s: Help.text)
				JOptionPane.showMessageDialog(this, s);
		});
		JMenuItem exit = generateMenuOption("Exit", (e) -> {
			String s1 = "Are you sure you want to exit Multi Stopwatch?\nData is not saved and will be lost.";
			if (JOptionPane.showConfirmDialog(this, s1, "Exit Multi Stopwatch", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				int closeDisp = Math.max(this.getHeight() / 350, 1);
				
				while (this.getHeight() > 0) {
					try {
						Thread.sleep(1);
					}
					catch (Exception e1) {
						e1.printStackTrace();
					}
					this.setSize(this.getWidth(), this.getHeight() - closeDisp);
				}
				
				close();
			}
		});
		JMenuItem cushion = generateMenuOption("Change Cushion", (e) -> {
			String input = JOptionPane.showInputDialog(this, "Set border cushion distance equal to:", "" + CStopWatch.CUSHION);
			try {
				CStopWatch.CUSHION = (int) (Float.parseFloat(input.trim()));
			}
			catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(this, "Error reading input - not a valid number: '" + input + "'");
			}
		});
		JMenuItem setWindowHeight = generateMenuOption("Change Window Height", (e) -> {
			String newHeight = JOptionPane.showInputDialog(this, "Resize the window to fit how many timers?");
			if (newHeight == null) return;
			try {
				resizeWindow(Float.parseFloat(newHeight.trim()));
			}
			catch (NumberFormatException exc) {
				JOptionPane.showMessageDialog(this, "Error reading input - not a valid number: '" + newHeight + "'");
			}
		});
		JMenuItem toggleOneTimer = generateMenuOption("Toggle Concurrent Timers", (e) -> {
			onlyOneRunning = !onlyOneRunning;
			JOptionPane.showMessageDialog(this, onlyOneRunning? "Only one timer can run at a time.": "Multiple timers may run concurrently.");
		});
		JMenuItem debugger = generateMenuOption("Debug", (e) -> {
			int numRunning = 0;
			for (CStopWatch sw: stopwatches)
				if ( !sw.isPaused()) numRunning++;
			String[] s = {"Current version: " + Help.version, "Number of stop watches: " + stopwatches.size(), "Number of stop watches running: " + numRunning, "Timer size: " + CStopWatch.SIZE, "Window stats: (" + this.getX() + "px," + this.getY() + "px/" + panel.getWidth() + "px," + panel.getHeight() + "px)", "Concurrent timers active: " + !onlyOneRunning, "Timer cushion: " + CStopWatch.CUSHION};
			StringBuilder toDisp = new StringBuilder();
			for (String s1: s)
				toDisp.append(s1 + "\n");
			JOptionPane.showMessageDialog(this, toDisp);
		});
		JMenu options = generateMenu("Options", cushion, setWindowHeight, toggleOneTimer);
		JMenu other = generateMenu("Other", help, debugger, options, exit);
		menuBar.add(other);
	}
	private JMenuItem generateMenuOption(String name, ActionListener onClick) throws Exception {
		JMenuItem generated = new JMenuItem(name);
		generated.addActionListener(onClick);
		return generated;
	}
	private JMenu generateMenu(String name, JMenuItem... items) throws Exception {
		JMenu tor = new JMenu(name);
		
		for (JMenuItem i: items)
			tor.add(i);
		
		return tor;
	}
	public void resizeWindow(float numTimers) {
		numTimers++;
		this.setSize(CStopWatch.CUSHION * 2 + CStopWatch.SIZE * 2 + 2, (int) ( (CStopWatch.SIZE + CStopWatch.CUSHION) * numTimers) - 20);
	}
	
	
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void render(Graphics g) {
		int renderY = 5 - currentScroll;
		for (CStopWatch sw: stopwatches) {
			sw.render(renderY, g);
			renderY += CStopWatch.SIZE + CStopWatch.CUSHION;
		}
		g.setColor(Color.black);
		g.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);
	}
	
}
