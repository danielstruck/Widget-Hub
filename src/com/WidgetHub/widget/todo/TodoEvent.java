package com.WidgetHub.widget.todo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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
	private static final long serialVersionUID = 1L;
	
	
	protected StringScroller location;
	protected StringScroller details;
	protected boolean alertFlag;
	protected boolean forceMaximize;
	
	
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
						event.forceMaximize = true;
						event.setDateTime(dateTime);
						event.setDetails(eventDetails);
						event.setLocation(eventLocation);
//						event.render(g, 0, 0, previewDim.width);
						BufferedImage img = new BufferedImage(previewDim.width, event.getHeight(previewDim.width), BufferedImage.TYPE_INT_ARGB);
						event.renderElement(img);
						g.drawImage(img, 0, 0, null);
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
		forceMaximize = false;
	}
	
	
	public void edit() {
		new TodoEventEditPane(this).showConfirmDialog(widget);
	}
	
	
	@Override
	public void alert() {
		if (alertFlag == false) {
			alertFlag = true;
			TodoTools.alertBeep(100, "A4 B4");
		}
	}
	/** @return true if alert was reset, false otherwise. */
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
		TodoEvent event = this;
		contextMenu.addItem("Edit", (action) -> {
						event.edit();
					})
					.addItemIf(alertFlag, "Snooze +5", (action) -> {
						event.setDateTime(LocalDateTime.now().plusMinutes(5));
						event.alertFlag = false;
					})
					.addItemIf(alertFlag, "Snooze +30", (action) -> {
						event.setDateTime(LocalDateTime.now().plusMinutes(30));
						event.alertFlag = false;
					})
					.addItem((!hasClosed())? "Mark": "Restore", (action) -> {
						event.setClosed(!hasClosed());
					});
	}
	
	
	public void setLocation(String location) {
		this.location = new StringScroller(location, StringScroller.DEFAULT_SCROLL_STALL_STEP, StringScroller.DEFAULT_SCROLL_TICK_STEP);
	}
	
	public void setDetails(String details) {
		this.details = new StringScroller(details, StringScroller.DEFAULT_SCROLL_STALL_STEP, StringScroller.DEFAULT_SCROLL_TICK_STEP);
	}
	
	
	@Override
	public int getHeight(int width) {
		if (isMinimized())
			return width / 15;
		else
			return width / 3;
	}
	
	
	public boolean isMinimized() {
		return !forceMaximize && widget.isMinimized();
	}
	
	
	@Override
	public void renderElement(BufferedImage canvas) {
		Graphics2D g = canvas.createGraphics();
		
		drawBackground(g, canvas);

		if (!isMinimized()) {
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, canvas.getHeight() * 3 / 11));
			drawDateTime(g, canvas);
			drawLocation(g, canvas);
		}
		else
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, canvas.getHeight()));
		
		drawDetails(g, canvas);
		
		g.dispose();
	}
	protected void drawBackground(Graphics2D g, BufferedImage canvas) {
		final int arcSize = getArcSize(canvas.getWidth());
		
		g.setColor(colorOf(0, 0, 0));
		g.fillRoundRect(0, 1, canvas.getWidth(), canvas.getHeight(), arcSize, arcSize);
		
		Color c;
		LocalDateTime now = LocalDateTime.now();
		if (alertFlag && (System.currentTimeMillis() % 500 > 250))
			c = colorOf(255, 0, 0);
		else if (getDateTime().isBefore(now.plusDays(7)))
			c = colorOf(192 - 50, 192, 192); // light gray w/ cyan tint
		else
			c = colorOf(192, 192, 192);
		
		g.setColor(c);
		g.fillRoundRect(1, 0, canvas.getWidth() - 1, canvas.getHeight() - 1, arcSize, arcSize);
	}
	protected void drawDateTime(Graphics2D g, BufferedImage canvas) {
		int dateTimeY = canvas.getHeight() * 2 / 10;
		
		String formattedDateTime = defaultDateTimeFormat.format(getDateTime());
		
		g.setColor(colorOf(0, 0, 255));
		Toolbox.drawCenteredString(g, formattedDateTime, canvas.getWidth() / 2, dateTimeY);
	}
	protected void drawLocation(Graphics2D g, BufferedImage canvas) {
		final int xOffset = 7;
		FontMetrics fm = g.getFontMetrics();
		
		int yPos = canvas.getHeight() * 5 / 10;
		yPos += Toolbox.getFontHeight(fm) / 2;

		g.setColor(colorOf(125, 60, 0));
		g.drawString(location.next(fm, canvas.getWidth() - xOffset), xOffset, yPos);
	}
	protected void drawDetails(Graphics2D g, BufferedImage canvas) {
		final int xOffset = 7;
		FontMetrics fm = g.getFontMetrics();
		
		int yPos= Toolbox.getFontHeight(fm) / 2;
		
		if (!isMinimized())
			yPos += canvas.getHeight() * 8 / 10;
		else
			yPos += canvas.getHeight() * 5 / 10;

		g.setColor(colorOf(0, 0, 0));
		g.drawString(details.next(fm, canvas.getWidth() - xOffset), xOffset, yPos);
	}
}
