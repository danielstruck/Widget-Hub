package com.WidgetHub.widget.memory;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.AbstractWidget;

/**
 * A simple memory game. Improve your memory!
 * 
 * @author Daniel Struck
 *
 */
public class MemoryGameWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	private final String charsLower = "abcdefghijklmnopqrstuvwxyz",
						 charsUpper = charsLower.toUpperCase(),
						 charsNum   = "1234567890";
	
	// constructor info
	private static final boolean isTransparent = true;
	private static final int updateDelay = 10;
	private static final String iconPath = null;
	
	// instance variables
	private double codeTime_Sec;
	private double codeLength;
	private double streak;
	private String code;
	private long startTime;
	private String input;
	private String infoLabel;
	private boolean gameStarted;
	
	private class CodePromptResults {
		int tries;
		int highestCorrect;
		
		public CodePromptResults() {
			tries = 0;
			highestCorrect = 0;
		}
	}
	
	
	public MemoryGameWidget() {
		super(isTransparent, updateDelay, iconPath);
		gameStarted = false;
		
		codeTime_Sec = 60;
		codeLength = 1;
		streak = 0;
		
		setName("Memory Game Widget");
		setBackground(Color.lightGray);
		setSize(200, 20);
		setResizable(false);
		
		
		generateCode();
		gameStarted = true;
	}
	
	
	@Override
	public void update() {
		if (gameStarted && System.currentTimeMillis() - startTime >= codeTime_Sec * 1000) {
			CodePromptResults results = promptForCode();
			
			double percentCorrect = (double) results.highestCorrect / code.length();
			
			streak += percentCorrect;
			if (results.tries <= 3) {
				codeTime_Sec += 10 * percentCorrect / results.tries + streak;
				codeLength += 1.0 / (results.tries * results.tries);
			}
			else {
				codeTime_Sec = Math.max(30, codeTime_Sec - 10 * 3/percentCorrect);
				codeLength = Math.max(1, codeLength - 1);
				
				streak = 0;
				
				JOptionPane.showMessageDialog(null, "Incorrect: " + code);
			}
			
			generateCode();
		}
	}
	private CodePromptResults promptForCode() {
		CodePromptResults results = new CodePromptResults();
		
		do {
			requestFocus();
			input = JOptionPane.showInputDialog("Enter the code (" + ((int) codeLength) + " chars)");
			
			if (input != null) {
				results.tries++;
				results.highestCorrect = Math.max(compareInput(input, code), results.highestCorrect);
			}
		} while (results.highestCorrect < code.length() && results.tries < 3);
		
		return results;
	}
	private void generateCode() {
		infoLabel = ((int) codeLength) + " chars every " + ((int) codeTime_Sec) + "s | streak: " + ((int) streak);
		
		String chars = charsUpper;
		if (codeLength > 7)
			chars += charsLower;
		else if (codeLength > 2)
			chars += charsNum;
		
		code = "";
		for (int i = 0; i < (int) codeLength; i++)
			code += chars.charAt((int) (chars.length() * Math.random()));
		
		JOptionPane.showMessageDialog(null, code);
		
		startTime = System.currentTimeMillis();
	}
	private int compareInput(String input, String code) {
		int maxIndex = Math.min(input.length(), code.length());
		int numMatching = 0;
		
		for (int i = 0; i < maxIndex; i++)
			if (input.charAt(i) == code.charAt(i)) numMatching++;
		
		return numMatching;
	}
	
	
	@Override
	public void render(Graphics g) {
		if (infoLabel != null)
			g.drawString(infoLabel, 2, 15);
	}
}
