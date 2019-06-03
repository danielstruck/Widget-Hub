package com.WidgetHub.tone;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Function;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.WidgetHub.widget.AbstractWidget;

public class Waveform {
	// Useful tool: http://www.szynalski.com/tone-generator/
	public static final int SAMPLE_RATE = 16 * 1024; // ~16 KHz
	public static final int SECONDS_MAX = 5;
	public static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
	
	private static final SourceDataLine line = generateSourceDataLine();
	
	private byte volume;
	
	public double[] data;
	
	
	public Waveform(int duration, Function<Integer, Double> function) {
		this.volume = 127;
		data = new double[duration];
		
		for (int x = 0; x < duration; x++) {
			data[x] = function.apply(x);
		}
	}
	
	
	public void play(int millis) throws IllegalArgumentException {
		if (millis > SECONDS_MAX * 1000 || millis < 0)
			throw new IllegalArgumentException("Play duration too long. Duration must be a positive integer that does not exceed " + 
												(SECONDS_MAX * 1000) + " milliseconds. Recieved duration: " + millis + " milliseconds");
		
		int length = SAMPLE_RATE * millis / 1000;
		try {
			line.open(af, SAMPLE_RATE);
			line.start();
			
			line.write(amplify(data), 0, length);
			
			line.drain();
			line.close();
		}
		catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	
	private byte[] amplify(double[] values) {
		byte[] amplified = new byte[values.length];
		
		for (int i = 0; i < values.length; i++)
			amplified[i] = (byte) (values[i] * volume);
		
		return amplified;
	}
	
	public void setVolume(double volume) {
		if (volume >= 1)
			volume = 127;
		else if (volume <= 0)
			volume = 0;
		else
			volume = (byte) (127 * volume);
	}
	
	
	private static SourceDataLine generateSourceDataLine() {
		try {
			return AudioSystem.getSourceDataLine(af);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Waveform add(Waveform waveform) {
		if (this.data.length != waveform.data.length) {
			System.err.println("Waveform data must be same length.");
		}
		
		return new Waveform(data.length, (x) -> {
			return data[x] + waveform.data[x];
		});
	}
	
	public Waveform multiply(Waveform waveform) {
		if (this.data.length != waveform.data.length) {
			System.err.println("Waveform data must be same length.");
		}
		
		return new Waveform(data.length, (x) -> {
			return data[x] * waveform.data[x];
		});
	}
	
	
	public void display() {
		new AbstractWidget(false, 1000, null) {
			private static final long serialVersionUID = 1L;

			@Override
			public void update() {}

			@Override
			public void render(Graphics g) {
				int xOffset = 20;
				int yOffset = this.getHeight()/2;
				int amp = 100;
				
				g.setColor(Color.blue);
				for (int i = 1; i < data.length; i++) {
					g.drawLine(i-1 + xOffset, (int) (amp*data[i-1] + yOffset), i + xOffset, (int) (amp*data[i] + yOffset));
				}
			}
			
		};
	}
}
