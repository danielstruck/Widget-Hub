package com.WidgetHub.widget;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {
	public enum Note {
		// Useful tool: http://www.szynalski.com/tone-generator/
		REST, A4, A4$, B4, C5, C5$, D5, D5$, E5, F5, F5$, G5, G5$, A5;
		
		public static final int SAMPLE_RATE = 16 * 1024; // ~16 KHz
		public static final int SECONDS_MAX = 5;
		public static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		
		private static final SourceDataLine line = generateSourceDataLine();
		
		private final byte[] sin = new byte[SECONDS_MAX * SAMPLE_RATE];
		
		
		Note() {
			int n = this.ordinal();
			
			if (n > 0) {
				double exp = (double) (n - 1) / 12;
				double frequency = 440 * Math.pow(2, exp);
				
				for (int i = 0; i < sin.length; i++) {
					double period = (double) SAMPLE_RATE / frequency;
					double angle = 2 * i * Math.PI / period;
					sin[i] = (byte) (Math.sin(angle) * 127);
				}
			}
		}
		
		
		public void play(int millis) {
			try {
				line.open(af, SAMPLE_RATE);
				line.start();
				
				millis = Math.min(millis, SECONDS_MAX * 1000);
				int length = SAMPLE_RATE * millis / 1000;
				
				line.write(sin, 0, length);
				line.drain();
				line.close();
			}
			catch (LineUnavailableException e) {
				e.printStackTrace();
				return;
			}
		}
		
		
		/**
		 * Parses <i>notes</i> and plays them for <i>millis</i> milliseconds.
		 * 
		 * @param millis
		 *            The number of milliseconds to play each note for.
		 * @param notes
		 *            A {@link String} of notes to parse.
		 * @return The number of notes successfully parsed.
		 */
		public static int play(int millis, String notes) {
			int successes = 0;
			
			for (String note: notes.split(" ")) {
				Note parse = valueOf(note);
				
				if (parse != null) {
					parse.play(millis);
					successes++;
				}
			}
			
			return successes;
		}
		
		
		private static SourceDataLine generateSourceDataLine() {
			try {
				return AudioSystem.getSourceDataLine(af);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
	public static void main(String[] args) {
		Note.play(100, "A4 B4");
		
//		for (Note note: Note.values()) {
//			System.out.println(note.name());
//			note.play(100);
//		}
	}
}
