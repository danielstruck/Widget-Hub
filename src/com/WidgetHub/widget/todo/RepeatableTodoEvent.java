package com.WidgetHub.widget.todo;

import java.awt.Component;
import java.time.DayOfWeek;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.WidgetHub.widget.ContextMenu;

public class RepeatableTodoEvent extends TodoEvent {
	private static final long serialVersionUID = 1L;
	
	
	private boolean[] repeats = new boolean[7];
	
	
	protected class RepeatableTodoEventEditPane extends TodoEventEditPane {
		private static final long serialVersionUID = 1L;
		
		private Component[] repeatRow = new Component[8];

		RepeatableTodoEventEditPane(RepeatableTodoEvent todoEvent) {
			super(todoEvent);
			
			repeatRow[0] = new JLabel("Repeats");
			
			for (int i = 1; i < repeatRow.length; i++) {
				JCheckBox checkBox = new JCheckBox(DayOfWeek.of(i).name().substring(0, 3), null, todoEvent.repeats[i - 1]);
				repeatRow[i] = checkBox;
			}
			
			addRow(repeatRow);
		}
		

		@Override
		public int showConfirmDialog(Component parent) {
			int dialogConfirm = super.showConfirmDialog(parent);
			
			if (dialogConfirm == JOptionPane.OK_OPTION) {
				for (int i = 1; i < repeatRow.length; i++) {
					((RepeatableTodoEvent) todoEvent).repeats[i - 1] = ((JCheckBox) repeatRow[i]).isSelected();
				}
			}
			
			return dialogConfirm;
		}
	}
	
	public RepeatableTodoEvent(TodoWidget widget) {
		super(widget);
	}
	
	
	public void edit() {
		new RepeatableTodoEventEditPane(this).showConfirmDialog(widget);
	}
	

	@Override
	public void applyCustomContextMenu(ContextMenu contextMenu) {
		RepeatableTodoEvent event = this;
		
		contextMenu.addItemIf(alertFlag, "Dismiss", (action) -> {
						for (int i = 0; i < 7; i++) {
							event.setDateTime(event.getDateTime().plusDays(1));
							if (event.repeatsOnDay(event.getDateTime().getDayOfWeek())) {
								event.resetAlerts();
								break;
							}
						}
					});
		
		super.applyCustomContextMenu(contextMenu);
	}
	
	
	private boolean repeatsOnDay(DayOfWeek day) {
		return repeats[day.getValue() - 1];
	}
}
