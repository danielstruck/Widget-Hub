package com.WidgetHub.tone;

import java.util.function.Consumer;

public enum Note {
	REST, A4, A4$, B4, C5, C5$, D5, D5$, E5, F5, F5$, G5, G5$, A5;
	
	public static final int WAVE_DURATION = Waveform.SECONDS_MAX * Waveform.SAMPLE_RATE;
	
	private Waveform sin;
	private Waveform sawtooth;
	private Waveform square;
	private Waveform triangle;
	public final double period;
	
	
	private Note() {
		int n = this.ordinal();
		
		double exp, frequency;
		
		if (n > 0) {
			exp = (double) (n - 1) / 12;
			frequency = 440 * Math.pow(2, exp);
			period = (double) Waveform.SAMPLE_RATE / frequency;
		}
		else {
			exp = 0;
			frequency = 0;
			period = 0;
		}
		
		// setup sin waveform
		sin = new Waveform(WAVE_DURATION, (x) -> {
			double angle = 2 * x * Math.PI / period;
			return Math.sin(angle);
		});
		
		// setup sawtooth waveform
		sawtooth = new Waveform(WAVE_DURATION, (x) -> {
			double tooth = (double) (x % period) / period;
			return tooth;
		});
		
		// setup square waveform
		square = new Waveform(WAVE_DURATION, (x) -> {
			double square = (int) (x / period) % 2;
			return square;
		});
		
		// setup triangle waveform
		triangle = new Waveform(WAVE_DURATION, (x) -> {
			double tooth = (double) (x % (period)) / (period/2);
			if (tooth > 1)
				tooth = 2 - tooth;
			return tooth;
		});
	}
	
	
	public Waveform getSin() {
		return sin;
	}
	public Waveform getSawtooth() {
		return sawtooth;
	}
	public Waveform getSquare() {
		return square;
	}
	public Waveform getTriangle() {
		return triangle;
	}
	
	
	public void playSin(int millis) {
		sin.play(millis);
	}
	public void playSawtooth(int millis) {
		sawtooth.play(millis);
	}
	public void playSquare(int millis) {
		square.play(millis);
	}
	public void playTriangle(int millis) {
		triangle.play(millis);
	}
	
	
	private static void playNotes(String notes, Consumer<Note> code) {
		for (String note: notes.split(" ")) {
			code.accept(Note.valueOf(note));
		}
	}
	public static void playSinNotes(int millis, String notes) throws IllegalArgumentException {
		playNotes(notes, (note) -> {
			note.playSin(millis);
		});
	}
	public static void playSawtoothNotes(int millis, String notes) throws IllegalArgumentException {
		playNotes(notes, (note) -> {
			note.playSawtooth(millis);
		});
	}
	public static void playSquareNotes(int millis, String notes) throws IllegalArgumentException {
		playNotes(notes, (note) -> {
			note.playSquare(millis);
		});
	}
	public static void playTriangleNotes(int millis, String notes) throws IllegalArgumentException {
		playNotes(notes, (note) -> {
			note.playTriangle(millis);
		});
	}
}
