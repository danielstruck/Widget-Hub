package com.WidgetHub.widget.todo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.WidgetHub.widget.ContextMenu;
import com.WidgetHub.widget.StringScroller;

/****TODO****
 * -Add functionality for repeating alerts (possible a separate element?)
 */
public class TodoEvent extends TodoElement {
	private StringScroller location, details;
	private boolean promptOpenFlag, alertFlag;
	
	public TodoEvent(TodoWidget widget) {
		super(widget);
		
		setLocation("");
		setDetails("");
		
		alertFlag = false;
		promptOpenFlag = false;
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
		int textFieldLength = 15;
		JSpinner yearSpinner = new JSpinner();
		JComboBox<Month> monthBox = new JComboBox<Month>();
		JComboBox<Integer> dayOfMonthBox = new JComboBox<Integer>();
		JTextField locationText = new JTextField(textFieldLength);
		JTextField detailsText = new JTextField(textFieldLength);
		JTextField hourText = new JTextField(textFieldLength), minuteText = new JTextField(textFieldLength);
		
		LocalDateTime currentTime = getDateTime() == null? LocalDateTime.now(): getDateTime();
		
		// year
		yearSpinner.setValue(currentTime.getYear());
		// Month
		for (Month m: Month.values())
			monthBox.addItem(m);
		monthBox.addItemListener((e1) -> {
			dayOfMonthBox.removeAllItems();
			
			for (int i = 1; i <= ((Month) monthBox.getSelectedItem()).maxLength(); i++)
				dayOfMonthBox.addItem(i);
		});
		monthBox.setSelectedItem(currentTime.getMonth());
		// day of month
		for (int i = 1; i <= 31; i++)
			dayOfMonthBox.addItem(i);
		dayOfMonthBox.setSelectedItem(currentTime.getDayOfMonth());
		// hour
		hourText.setText("" + currentTime.getHour());
		// minute
		minuteText.setText("" + currentTime.getMinute());
		// details
		if (details != null) detailsText.setText(details.text);
		// location
		if (location != null) locationText.setText(location.text);
		
		
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
					TodoEvent event = new TodoEvent(widget);
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
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.ipadx = 10;
		constraints.ipady = 4;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		int row = 0;
		addBagLine(constraints, row++, p, "Preview",  previewPanel);
		addBagLine(constraints, row++, p, "Year",	  yearSpinner);
		addBagLine(constraints, row++, p, "Month",	  monthBox);
		addBagLine(constraints, row++, p, "Day",	  dayOfMonthBox);
		addBagLine(constraints, row++, p, "Hour",	  hourText);
		addBagLine(constraints, row++, p, "Minute",	  minuteText);
		addBagLine(constraints, row++, p, "Location", locationText);
		addBagLine(constraints, row++, p, "Details",  detailsText);
		
		if (!promptOpenFlag) {
			promptOpenFlag = true;
			new Thread(() -> {
				while (promptOpenFlag) {
					previewPanel.repaint();
					try { Thread.sleep(10); } catch (Exception e) {}
				}
			}).start();
			
			if (JOptionPane.showConfirmDialog(widget, p, "Event Editor", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				String eventDetails, eventLocation;
				Month month;
				int year, dayOfMonth, hour, minute;
				
				eventDetails = detailsText.getText();
				eventLocation = locationText.getText();
				
				month = (Month) monthBox.getSelectedItem();
				year = (int) yearSpinner.getValue();
				dayOfMonth = (int) dayOfMonthBox.getSelectedItem();
				hour = proccessHour(hourText.getText());
				minute = Integer.parseInt(minuteText.getText());
				
				LocalDateTime dateTime = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
				
				setLocation(eventLocation);
				setDetails(eventDetails);
				setDateTime(dateTime);
				
				alertFlag = false;
				promptOpenFlag = false;
			}
			else {
				promptOpenFlag = false;
			}
		}
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
	public static void addBagLine(GridBagConstraints constraints, int row, JPanel panel, String label, JComponent component) {
		addBagToPanel(constraints, 0, row, panel, new JLabel(label));
		addBagToPanel(constraints, 1, row, panel, component);
	}
	public static void addBagToPanel(GridBagConstraints constraints, int x, int y, JPanel panel, JComponent component) {
		constraints.gridy = y;
		constraints.gridx = x;
		panel.add(component, constraints);
	}
	
	
	@Override
	public void alert(LocalDateTime now) {
		alertFlag = true;
	}
	
	
	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem("Edit", (action) -> {
						edit();
					})
					.addItemIf((input) -> { return input; }, alertFlag, "Snooze +5", (action) -> {
						setDateTime(LocalDateTime.now().plusMinutes(5));
						alertFlag = false;
					})
					.addItemIf((input) -> { return input; }, alertFlag, "Snooze +30", (action) -> {
						setDateTime(LocalDateTime.now().plusMinutes(30));
						alertFlag = false;
					})
					.addItem((!hasClosed())? "Close": "Un-close", (action) -> {
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
			drawDetails(g, y, width, height);
		}
		else
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height));
		
		drawLocation(g, y, width, height);
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
		drawCenteredString(g, formattedDateTime, width / 2, dateTimeY);
	}
	protected void drawLocation(Graphics g, int y, int width, int height) {
		final int xOffset = 7;
		FontMetrics fm = g.getFontMetrics();
		
		int yPos = y + height * 5 / 10;
		yPos += getFontHeight(fm) / 2;
		
		g.setColor(Color.black);
		g.drawString(location.next(fm, width - xOffset), xOffset, yPos);
	}
	protected void drawDetails(Graphics g, int y, int width, int height) {
		final int xOffset = 7;
		FontMetrics fm = g.getFontMetrics();
		
		int yPos  = y + height * 8 / 10;
		yPos += getFontHeight(fm) / 2;
		
		g.setColor(new Color(125, 60, 0));
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
	
	
	
	public static void drawCenteredString(Graphics g, String s, int x, int y) {
		FontMetrics fm = g.getFontMetrics();
		
		x -= fm.stringWidth(s) / 2;
		y += getFontHeight(fm) / 2;
		
		g.drawString(s, x, y);
	}
	private static int getFontHeight(FontMetrics fm) {
		return (fm.getAscent() - fm.getDescent() - fm.getLeading());
	}
}