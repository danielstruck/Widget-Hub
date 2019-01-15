package com.WidgetHub.widget.timer;

public class Help {
	public static final String version = "1.5";
	
	public static final String[] text = new String[]{
			"Under File:\n" +
			"    >New: add a timer \n" + 
			"Under Tools:\n"+
			"    >Pause All: pauses all paused timers\n" +
			"    >Unpause All: starts/resumes all timers\n" + 
			"    >Delete All: deletes all timers - irreversable\n" +
			"    >Reset All: sets all timers' time to zero\n" + 
			"    >Center Window: centers window on screen" +
			"Under Other:\n" + 
			"    >Help: Displays a popup help screen\n" + 
			"    >" + 
			"    Under Options:\n" + 
			"        >Set Cushion: change the spacing between stopwatches\n" +
			"        >Change Window Height: change the height of the window to fit\n" +
			"          some number of timers (default is 3)\n" + 
			"        >Toggle Concurrent Timers: toggle whether or not multiple timers\n" +
			"          can be running at the same time\n" +
			"    >Exit: exits the widget - does not save\n" +
			"\nOk to continue..."
			,
			"[TIMER CONTROL]\n" + 
			"-Left clicking a timer starts/pauses it.\n" + 
			"-The scroll wheel cyles though the timers\n" +
			"[TIMER MODIFICATION]\n" + 
			"-Reset a timer by right clicking it\n" + 
			"-Delete a timer by middle clicking (scroll wheel)\n" +
			"-Control + left click to rename a timer\n" +
			"\nOk to continue..."
		};
}