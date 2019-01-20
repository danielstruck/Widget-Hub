package com.WidgetHub.widget.todo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.WidgetHub.widget.BagGridPane;
import com.WidgetHub.widget.ContextMenu;
import com.WidgetHub.widget.StringScroller;
import com.WidgetHub.widget.Toolbox;

public class TodoEvent extends TodoElement {
	protected StringScroller location;
	protected StringScroller details;
	protected boolean alertFlag;
	
	protected class TodoEventEditPane extends BagGridPane {
		private static final long serialVersionUID = 1L;
		protected static final int textFieldWidth = 15;
		
		protected JSpinner yearSpinner;
		protected JComboBox<Month> monthBox;
		protected JComboBox<Integer> dayOfMonthBox;
		protected JTextField locationText;
		protected JTextField detailsText;
		protected JTextField hourText, minuteText;
		protected LocalDateTime currentTime;
		
		protected TodoEvent todoEvent;
		protected boolean promptOpenFlag;
		
		protected TodoEventEditPane(TodoEvent todoEvent) {
			this.todoEvent = todoEvent;
			promptOpenFlag = false;
			
			yearSpinner = new JSpinner();
			monthBox = new JComboBox<Month>();
			dayOfMonthBox = new JComboBox<Integer>();
			locationText = new JTextField(textFieldWidth);
			detailsText = new JTextField(textFieldWidth);
			hourText = new JTextField(textFieldWidth);
			minuteText = new JTextField(textFieldWidth);
			
			Dimension previewDim = new Dimension(150, 50);
			JPanel previewPanel = new JPanel() {
				private static final long serialVersionUID = 1L;
				
				public void paint(Graphics g) {
					super.paint(g);
					try {
						String eventDetails = detailsText.getText();
						String eventLocation = locationText.getText();
						
						Month month = (Month) monthBox.getSelectedItem();
						int year = (int) yearSpinner.getValue();
						int dayOfMonth = (int) dayOfMonthBox.getSelectedItem();
						int hour = proccessHour(hourText.getText());
						int minute = Integer.parseInt(minuteText.getText());
						
						LocalDateTime dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
						TodoEvent event = new TodoEvent(TodoEventEditPane.this.todoEvent.widget);
						event.setDateTime(dateTime);
						event.setDetails(eventDetails);
						event.setLocation(eventLocation);
						event.render(g, 0, previewDim.width);
					}
					catch (Exception e) {
						g.setColor(Color.red);
						int x = (previewDim.width - g.getFontMetrics().stringWidth("ERROR")) / 2;
						int y = (previewDim.height + g.getFontMetrics().getHeight()) / 2;
						g.drawString("ERROR", x, y);
						g.drawString("ERROR", x + 1, y + 1);
					}
				}
			};
			previewPanel.setPreferredSize(previewDim);
			previewPanel.setMinimumSize(previewDim);
			
			
			// date time
			currentTime = this.todoEvent.getDateTime() == null? LocalDateTime.now(): this.todoEvent.getDateTime();
			
			// year
			yearSpinner.setValue(currentTime.getYear());
			
			// month
			for (Month m: Month.values())
				monthBox.addItem(m);
			monthBox.addItemListener((e1) -> {
				dayOfMonthBox.removeAllItems();
				
				for (int i = 1; i <= ((Month) monthBox.getSelectedItem()).maxLength(); i++)
					dayOfMonthBox.addItem(i);
			});
			monthBox.setSelectedItem(currentTime.getMonth());
			
			// day of month
			for (int i = 1; i <= currentTime.getMonth().maxLength(); i++)
				dayOfMonthBox.addItem(i);
			dayOfMonthBox.setSelectedItem(currentTime.getDayOfMonth());
			
			// hour
			hourText.setText("" + currentTime.getHour());
			
			// minute
			minuteText.setText("" + currentTime.getMinute());
			
			// details
			if (this.todoEvent.details != null)
				detailsText.setText(this.todoEvent.details.text);
			
			// location
			if (this.todoEvent.location != null)
				locationText.setText(this.todoEvent.location.text);
			
			
			addRow(new JLabel("Preview"),	previewPanel);
			addRow(new JLabel("Year"),		yearSpinner);
			addRow(new JLabel("Month"),		monthBox);
			addRow(new JLabel("Day"),		dayOfMonthBox);
			addRow(new JLabel("Hour"),		hourText);
			addRow(new JLabel("Minute"),	minuteText);
			addRow(new JLabel("Location"),	locationText);
			addRow(new JLabel("Details"),	detailsText);
		}
		
		@Override
		public int showConfirmDialog(Component parent) {
			int dialogConfirm = -1;
			
			if (!promptOpenFlag) {
				promptOpenFlag = true;
				new Thread(() -> {
					while (promptOpenFlag) {
						repaint();
						try {
							Thread.sleep(10);
						} catch (Exception e) {}
					}
				}).start();
				
				dialogConfirm = super.showConfirmDialog(todoEvent.widget);
				
				if (dialogConfirm == JOptionPane.OK_OPTION) {
					String eventDetails = detailsText.getText();
					String eventLocation = locationText.getText();
					
					Month month = (Month) monthBox.getSelectedItem();
					int year = (int) yearSpinner.getValue();
					int dayOfMonth = (int) dayOfMonthBox.getSelectedItem();
					int hour = proccessHour(hourText.getText());
					int minute = Integer.parseInt(minuteText.getText());
					
					LocalDateTime dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
					
					todoEvent.setLocation(eventLocation);
					todoEvent.setDetails(eventDetails);
					todoEvent.setDateTime(dateTime);
					
					todoEvent.resetAlerts();
				}
				
				promptOpenFlag = false;
			}
			
			return dialogConfirm;
		}
		
		private int proccessHour(String hour) {
			if (hour.startsWith("+")) {
				if (hour.length() > 1) {
					return Integer.parseInt(hour.substring(1)) + 12;
				}
				else {
					return -1;
				}
			}
			else {
				return Integer.parseInt(hour);
			}
		}
	}

	
	public TodoEvent(TodoWidget widget) {
		super(widget);
		
		setLocation("");
		setDetails("");
		
		alertFlag = false;
	}
	public TodoEvent(TodoWidget widget, BufferedReader reader) {
		super(widget, reader);

		try {
			setLocation(reader.readLine());
			setDetails(reader.readLine());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void edit() {
		TodoEventEditPane gridPane = new TodoEventEditPane(this);
		gridPane.showConfirmDialog(widget);
	}
	
	
	@Override
	public void alert() {
		if (alertFlag == false) {
			alertFlag = true;
			TodoTools.alertBeep();
		}
	}
	/**
	 * @return true if alert was reset, false otherwise.
	 */
	protected boolean resetAlerts() {
		if (alertFlag) {
			alertFlag = false;
			return true;
		}
		else {
			return false;
		}
	}
	
	
	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Edit", (action) -> {
						edit();
					})
					.addItemIf(alertFlag, "Snooze +5", (action) -> {
						setDateTime(LocalDateTime.now().plusMinutes(5));
						alertFlag = false;
					})
					.addItemIf(alertFlag, "Snooze +30", (action) -> {
						setDateTime(LocalDateTime.now().plusMinutes(30));
						alertFlag = false;
					})
					.addItem((!hasClosed())? "Delete": "Restore", (action) -> {
						setClosed(!hasClosed());
					})
					.addItem("Flush", (action) -> {
						if (JOptionPane.showConfirmDialog(widget, "Confirm flush? This action is irreversable.") == JOptionPane.OK_OPTION)
							widget.flush();
					});
	}
	
	
	public void setLocation(String location) {
		this.location = new StringScroller(location, 30, .5);
	}
	
	public void setDetails(String details) {
		this.details = new StringScroller(details, 30, .5);
	}
	
	@Override
	public int getHeight(int width) {
		if (widget.isMinimized())
			return width / 15;
		else
			return width / 3;
	}
	
	private int getArcSize(int width) {
		return width / 10;
	}
	
	
	@Override
	public void render(Graphics g, int y, int width) {
		int height = getHeight(width);
		
		drawBackground(g, y, width, height);
		
		if (!widget.isMinimized()) {
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height * 3 / 11));
			
			drawDateTime(g, y, width, height);
			drawLocation(g, y, width, height);
		}
		else
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height));
		
		drawDetails(g, y, width, height);
	}
	protected void drawBackground(Graphics g, int y, int width, int height) {
		final int arcSize = getArcSize(width);
		g.setColor(Color.black);
		g.fillRoundRect(0, y + 1, width, height, arcSize, arcSize);
		
		Color c;
		if (alertFlag && (System.currentTimeMillis() % 500 > 250))
			c = Color.red;
		else
			c = Color.lightGray;
		
		if (hasClosed())
			c = c.darker().darker();
		
		g.setColor(c);
		g.fillRoundRect(1, y, width - 1, height - 1, arcSize, arcSize);
	}
	protected void drawDateTime(Graphics g, int y, int width, int height) {
		int dateTimeY = y + height * 2 / 10;
		
		String formattedDateTime = defaultDateTimeFormat.format(getDateTime());
		
		g.setColor(Color.blue);
		Toolbox.drawCenteredString(g, formattedDateTime, width / 2, dateTimeY);
	}
	protected void drawLocation(Graphics g, int y, int width, int height) {
		final int xOffset = 7;
		FontMetrics fm = g.getFontMetrics();
		
		int yPos = y + height * 5 / 10;
		yPos += Toolbox.getFontHeight(fm) / 2;

		g.setColor(new Color(125, 60, 0));
		g.drawString(location.next(fm, width - xOffset), xOffset, yPos);
	}
	protected void drawDetails(Graphics g, int y, int width, int height) {
		final int xOffset = 7;
		FontMetrics fm = g.getFontMetrics();
		
		int yPos;
		if (!widget.isMinimized())
			yPos = y + height * 8 / 10;
		else
			yPos = y + height * 5 / 10;
		yPos += Toolbox.getFontHeight(fm) / 2;

		g.setColor(Color.black);
		g.drawString(details.next(fm, width - xOffset), xOffset, yPos);
	}
	
	
	@Override
	public String data() {
		String tor = getClass().getSimpleName() + "\n";
		
		tor += super.data();
		tor += location.text + "\n";
		tor += details.text + "\n";
		
		return tor;
	}
}
