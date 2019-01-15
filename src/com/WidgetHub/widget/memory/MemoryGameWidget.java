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
	
	private final String charsLower = "abcdefghijklmnopqrstuvwxyz", charsUpper = charsLower.toUpperCase(),
			charsNum = "1234567890";
	
	// constructor info
	private static final boolean isTransparent = true;
	private static final int updateDelay = 10;
	private static final String iconPath = null;
	
	// instance valiables
	private double codeTime = 60;// in seconds
	private double codeLength = 1;
	private double streak = 0;
	private String code;
	private long startTime;
	private String input;
	private String infoLabel;
	private boolean gameStarted;
	
	
	public MemoryGameWidget() {
		super(isTransparent, updateDelay, iconPath);
		gameStarted = false;
		
		setName("Memory Game Widget");
		setBackground(Color.lightGray);
		setSize(200, 20);
		setResizable(false);
		
		
		generateCode();
		gameStarted = true;
	}
	
	
	@Override
	public void update() {
		if (gameStarted && System.currentTimeMillis() - startTime >= codeTime * 1000) {
			CodePromptResults results = promptForCode();
			
			double percentCorrect = (double) results.highestCorrect / code.length();
			
			streak += percentCorrect;
			if (results.tries <= 3) {
				codeTime += 10 * percentCorrect / results.tries + streak;
				codeLength += 1.0 / (results.tries * results.tries);
			}
			else {
				codeTime = Math.max(30, codeTime - 10 * 3/percentCorrect);
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
		infoLabel = ((int) codeLength) + " chars every " + ((int) codeTime) + "s | streak: " + ((int) streak);
		
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

class CodePromptResults {
	int tries;
	int highestCorrect;
	
	public CodePromptResults() {
		tries = 0;
		highestCorrect = 0;
	}
}