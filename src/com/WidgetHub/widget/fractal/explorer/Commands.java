package com.WidgetHub.widget.fractal.explorer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Commands extends HashMap<String, Command> {
	private static final long serialVersionUID = 1L;
	public static Commands fractalCommands;
	private static FractalInfo info;
	
	
	public String execute(String command) {
		if (command == null)
			return null;
		if (command.length() == 0)
			return Errors.noCommand(this);
		
		String pre;
		String[] options;
		
		int firstSpaceIndex = command.indexOf(' ');
		if (firstSpaceIndex != -1) {
			pre = command.substring(0, firstSpaceIndex);
			options = command.substring(firstSpaceIndex + 1).split(" ");
		}
		else {
			pre = command;
			options = new String[]{};
		}
		
		if (options != null && this.containsKey(pre))
			return get(pre).execute(options);
		else
			return Errors.badCommand(pre);
	}
	
	
	public static Commands setFractalInfo(FractalInfo info_) {
		Commands.info = info_;
		
		if (Commands.fractalCommands != null)
			return null;
		
		Commands.fractalCommands = new Commands();
		
		
		
		fractalCommands.put("help", new Command("help (command name)",
				"Gets helpful hints about use of the command prompt.\n ") {
			public String execute(String options[]) {
				if (options.length == 0) {
					String hints = "HELP:\n";
					
					hints += "-Execute blank command ('') to see list of commands  \n";
					hints += "-Perfered input syntax:  \n"
							 + "> [desc]: Option type - 'desc' describes what goes here.  \n"
							 + "> (desc): The input 'val' is optional.  \n"
							 + "> {desc1, desc2, ...}: One of 'desc1', 'desc2', etc. must be chosen.  \n"
							 + "> ...: Variadic command. Any number of commands can go here.  \n"
							 + "> 'A': The literal. In this case the character 'A'  \n";
					
					return hints;
				}
				else {
					Command command = fractalCommands.get(options[0]);
					if (command == null)
						return Errors.badCommand(options[0]);
					
					return " ***** SYNTAX *****\n"
						   + command.perferedSyntax
						   + "\n\n"
						   + " ***** DESCRIPTION *****\n"
						   + command.description;
				}
			}
		});
		
		fractalCommands.put("n", new Command("n [new Fractal::n value]",
				"Sets the 'n' value of the currant fractal.\n "
				+ "'n' is generally used for powers (x^n) in the fractals.\n ") {
			
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					info.fractal.n = Double.parseDouble(options[0]);
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});

		fractalCommands.put("x", new Command("x [new x]",
				"Sets the 'x' value of the cursor.\n ") {
			
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					info.xOffset = Double.parseDouble(options[0]);
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("y", new Command("y [new y]",
				"Sets the 'y' value of the cursor.\n ") {
			
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					info.yOffset = Double.parseDouble(options[0]);
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("zoom", new Command("zoom [new zoom]",
				"Sets the 'zoom' (z-axis) value of the cursor.\n ") {
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					info.zoom = Long.parseLong(options[0]);
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("xyz", new Command("xyz [new x] [new y] [new zoom]",
				"Sets the 'x', 'y', and 'zoom' (z-axis) values of the cursor, respectively.\n ") {
			public String execute(String options[]) {
				if (options.length < 3)
					return Errors.badInput(perferedSyntax);
				
				String result = "";
				
				result += fractalCommands.execute("x" + options[0]) + "\n";
				result += fractalCommands.execute("y" + options[1]) + "\n";
				result += fractalCommands.execute("zoom" + options[2]) + "\n";
				
				if (result.length() > 3) // min size of 3 due to 3 new lines (\n) after each execute
					return result;
				else
					return null;
			}
		});
		
		fractalCommands.put("res", new Command("res [new res].",
				"Sets the resolution of the image.\n "
				+ "Resolution generally controls the \"depth\" of the fractal.\n ") {
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					info.resolution = Integer.parseInt(options[0]);
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("center", new Command("center ({'x', 'y'})",
				"Sets the value of the 'x' and/ or 'y' location of the cursor to zero (0), or both if no axis is specified.\n ") {
			public String execute(String options[]) {
				if (options.length == 0 || options[0].contains("x"))
					info.xOffset = 0;
				
				if (options.length == 0 || options[0].contains("y"))
					info.yOffset = 0;
				
				return null;
			}
		});
		
		fractalCommands.put("set", new Command("set [{{" + Fractal.nameList(", ") + "}, 'list'}]",
				"Changes the type of fractal to display or lists available fractals.\n ") {
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					if (options[0].equals("list")) {
						String list = "";
						
						for (Fractal frac: Fractal.values())
							list += frac.name() + "\n";
						
						return list;
					}
					else
						info.fractal = Fractal.valueOf(options[0]);
				} catch (IllegalArgumentException e) {
					return Errors.notFound(options);
				}
				return null;
			}
		});
		
		fractalCommands.put("size", new Command("size [{'x' size, 'y' size}] ({'x' size, 'y' size})",
				"Changes the size of the image, in pixels.\n "
				+ "Can change one or both dimensions with this command\n ") {
			public String execute(String options[]) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					if (options[0].startsWith("x")) {
						info.imgWidth = Integer.parseInt(options[0].substring(1));
						
						if (options.length >= 2 && options[1].startsWith("y"))
							info.imgHeight = Integer.parseInt(options[1].substring(1));
					}
					else if (options[0].startsWith("y")) {
						info.imgHeight = Integer.parseInt(options[0].substring(1));
						
						if (options.length >= 2 && options[1].startsWith("x"))
							info.imgWidth = Integer.parseInt(options[1].substring(1));
					}
					else
						return null;
					
					try {
						info.setImage(new BufferedImage(info.imgWidth, info.imgHeight, BufferedImage.TYPE_INT_RGB));
					} catch (OutOfMemoryError e) {
						JOptionPane.showMessageDialog(null, "Not enough memory for desired image size.");
					} catch (NegativeArraySizeException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Image dimensions must be positive: " + options[0] + "," + ((options.length > 1)? options[1]: "~"));
					}
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("c", new Command("c [{'x' constant, 'y' constant}] ({'x' constant, 'y' constant})",
				"Changes the constant value of the fractal.\n "
				+ "Each fractal has constants for the real and imaginary axes, but only some use them.\n ") {
			@Override
			public String execute(String[] options) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					if (options[0].startsWith("x")) {
						info.fractal.cx = Double.parseDouble(options[0].substring(1));
						
						if (options.length >= 2 && options[1].startsWith("y"))
							info.fractal.cy = Double.parseDouble(options[1].substring(1));
					}
					else if (options[0].startsWith("y")) {
						info.fractal.cy = Double.parseDouble(options[0].substring(1));
						
						if (options.length >= 2 && options[1].startsWith("x"))
							info.fractal.cx = Double.parseDouble(options[1].substring(1));
					}
					else
						return Errors.badInput(perferedSyntax);
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("save", new Command("save",
				"Saves the current fractal image to a file selected with the file chooser popup.\n ") {
			@Override
			public String execute(String[] options) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
				fileChooser.showSaveDialog(null);
				
				try {
					File f = fileChooser.getSelectedFile();
					
					if (f != null) {
						ImageIO.write(info.img, "PNG", f);
						JOptionPane.showMessageDialog(null, "Save successful");
					}
					else
						return "Image saving canceled";
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return null;
			}
		});
		
		fractalCommands.put("co", new Command("co [output]",
				"A custom output command that returns the string \"co \" followed by the options, delimited by a space.\n "
				+ "This command extends the functionality of the command prompt to allow for more advances and custom commands.\n "
				+ "\n"
				+ "To make custom commands, check the command.execute() line for a return value that starts with 'co '.") {
			@Override
			public String execute(String[] options) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				String customOutput = "co ";
				
				for (String option: options)
					customOutput += option + " ";
				
				return customOutput;
			}
		});
		
		fractalCommands.put("vars", new Command("vars",
				"Displays all variables in the FractalInfo object that these commands were initiated with.\n ") {
			@Override
			public String execute(String[] options) {
				JOptionPane.showMessageDialog(null, info.toString());
				
				return null;
			}
		});
		
		fractalCommands.put("disp", new Command("disp {" + FractalInfo.DisplayMode.nameList(", ") + "}",
				"Changes the display mode. The display mode decides how the image should be rendered.\n "
				+ "In particular, the display mode calculates the color of each pixel based on the membership of that pixel to the set of the fractal.\n ") {
			@Override
			public String execute(String[] options) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				
				try {
					info.displayMode = FractalInfo.DisplayMode.valueOf(options[0]);
				} catch (IllegalArgumentException e) {
					return Errors.notFound(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("exit", new Command("exit",
				"Shuts down the program.\n "
				+ "Information about the programs' operation will not be saved, nor will there be a prompt, so use with caution.\n ") {
			@Override
			public String execute(String[] options) {
				System.exit(0);
				return null;
			}
		});
		
		fractalCommands.put("timebomb", new Command("timebomb [milliseconds until detonation] ",
				"Initiates a shutdown procedure that closes the program after the specified number of miliseconds has passed.\n ") {
			@Override
			public String execute(String[] options) {
				if (options.length < 1)
					return Errors.badInput(perferedSyntax);
				try {
					int millis = Integer.parseInt(options[0]);
					
					new Thread(() ->  {
						try { Thread.sleep(millis); } catch (InterruptedException e) {}
						System.out.println("BOOM!!");
						System.exit(0);
					}).start();
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				return null;
			}
		});
		
		fractalCommands.put("animate", new Command("animate ({'ms' frame time milliseconds, 'cx' step, 'cy' step, 'n' step, 'cxm' cx min, 'cxM' cx max, 'cym' cy min, 'cyM' cy max, 'nm' n min, 'nM' n max})...",
				"Animates the fractal's dimentions.\n "
				+ "The animation steps though the 'cx', 'cy', and 'n' variables of the current fractal.\n "
				+ "The animation frame refreshes the specified number of milliseconds.\n "
				+ "All animations take place in a singleton thread that will terminate and re-start when new values are presented.\n "
				+ "The thread will take up to the number of milliseconds specified for the last animation, plus 100 milliseconds, to terminate.\n "
				+ "\n"
				+ "The 'stop' command will only prompt the thread to terminate and will not wait for the thread's termination.\n "
				+ "Normally, the thread will stop after the next generated pattern.") {
			private Thread thread;
			private volatile boolean shouldRun;
			private long millis;
			private double cxStep, cyStep, nStep;
			private double cxMin, cyMin, nMin;
			private double cxMax, cyMax, nMax;
			
			@Override
			public String execute(String[] options) {
				if (options.length > 0 && options[0].equals("stop")) {
					shouldRun = false;
					return null;
				}

				String errors = "";
				try {
					if (thread != null) {
						shouldRun = false;
						thread.join(millis + 100);
						
						if (thread.isAlive())
							return "Thread failed to terminate. Try again later.";
					}
					
					millis = 100;
					 
					cxStep = 0.002;
					cyStep = 0.002;
					nStep  = 0.002;
					
					cxMin  = -1;
					cyMin  = -1;
					nMin   = -1;
					
					cxMax  = 1;
					cyMax  = 1;
					nMax   = 1;
					
					for (int i = 0; i < options.length; i++) {
						String selector = options[i].split(" ")[0];
						
						if (selector.startsWith("ms")) {
							millis = Long.parseLong(selector.substring(2));
							if (millis < 0)
								return Errors.negNum(options);
						}
						else if (selector.startsWith("cx")) {
							cxStep = Double.parseDouble(selector.substring(2));
						}
						else if (selector.startsWith("cy")) {
							cyStep = Double.parseDouble(selector.substring(2));
						}
						else if (selector.startsWith("n")) {
							nStep  = Double.parseDouble(selector.substring(1));
						}
						else if (selector.startsWith("cxm")) {
							cxMin  = Double.parseDouble(selector.substring(3));
						}
						else if (selector.startsWith("cxM")) {
							cxMax  = Double.parseDouble(selector.substring(3));
						}
						else if (selector.startsWith("cym")) {
							cyMin  = Double.parseDouble(selector.substring(3));
						}
						else if (selector.startsWith("cyM")) {
							cyMax  = Double.parseDouble(selector.substring(3));
						}
						else if (selector.startsWith("nm")) {
							nMin   = Double.parseDouble(selector.substring(2));
						}
						else if (selector.startsWith("nM")) {
							nMax   = Double.parseDouble(selector.substring(2));
						}
						else {
							errors += ("Unrecognized input: " + selector + ". Continuing command parsing -->\n");
						}
					}
					
					shouldRun = true;
					thread = new Thread(() ->  {
						while (shouldRun) {
							info.fractal.cx = boundDouble(cxMin, info.fractal.cx + cxStep, cxMax);
							info.fractal.cy = boundDouble(cyMin, info.fractal.cy + cyStep, cyMax);
							info.fractal.n  = boundDouble(nMin,  info.fractal.n + nStep,   nMax);
							
							try { Thread.sleep(millis); } catch (InterruptedException e) {}
						}
					});
					
					thread.start();
					
				} catch (InterruptedException e) {
					return "Animation thread was interupted:\n\n "
							+ "***** MAIN MESSAGE *****\n"
							+ e.getMessage()
							+ "\n\n "
							+ "***** LOCAL MESSAGE *****\n"
							+ e.getLocalizedMessage();
				} catch (NumberFormatException e) {
					return Errors.nan(options);
				}
				
				if (errors.length() > 0)
					return errors;
				else
					return null;
			}
			
			private double boundDouble(double min, double val, double max) {
				if (val > max)
					return max;
				else if (val < min)
					return max;
				else
					return val;
			}
		});
		
		fractalCommands.put("test", new Command("test",
				"Setup for a test render. Values that are set are: xOffset=yoffset=0, width=height=1500, res=1500, zoom=500") {
			
			@Override
			public String execute(String[] options) {
				info.xOffset    = 0;
				info.yOffset    = 0;
				info.imgWidth   = 1500;
				info.imgHeight  = 1500;
				info.resolution = 1500;
				info.zoom       = 500;
				
				info.setImage(new BufferedImage(info.imgWidth, info.imgHeight, BufferedImage.TYPE_INT_RGB));
				return null;
			}
		});
		
		fractalCommands.put("softsize", new Command("softsize [{'x' size, 'y' size}] ({'x' size, 'y' size})",
				"Changes the size of the image and attempts to maintain the current window of the fractal by zooming in or out.") {
			
			@Override
			public String execute(String[] options) {
				int oldWidth  = info.imgWidth,
					oldHeight = info.imgHeight;
				
				String errors = fractalCommands.get("size").execute(options);
				
				if (errors == null) {
					double widthScale  = (double) info.imgWidth / oldWidth,
						   heightScale = (double) info.imgHeight / oldHeight;
					info.zoom = (long) (info.zoom * (widthScale + heightScale) / 2);
				}
				
				return errors;
			}
		});
		
		fractalCommands.put("ssize", new Command("ssize [{'x' size, 'y' size}] ({'x' size, 'y' size})",
				"calls 'softsize' command") {
			
			@Override
			public String execute(String[] options) {
				return fractalCommands.get("softsize").execute(options);
			}
		});
		
		return fractalCommands;
	}
}
class Errors {
	public static String catStrings(String delim, String ...array) {
		String concat = "";
		
		for (String str: array)
			concat += str + delim;
		
		return concat;
	}
	
	public static String negNum(String[] options) {
		return "Parsed negative number when positive number expected: '" + catStrings(" ", options) + "'.";
	}
	
	public static String nan(String[] options) {
		return "Not a valid number: '" + catStrings(" ", options) + "'.";
	}
	
	public static String badCommand(String pre) {
		return "No such command: '" + pre + "'.";
	}
	
	public static String badInput(String perferedSyntax) {
		return "Invalid input. Use: " + perferedSyntax + ".";
	}
	
	public static String noCommand(Commands commands) {
		String info = "List of commands:\n";
		
		for (String s: commands.keySet())
			info += s + "\n";
		
		return info;
	}
	
	public static String notFound(String options[]) {
		return "Not found: '" + catStrings(" ", options) + "'.";
	}
}

abstract class Command {
	String perferedSyntax;
	String description;
	
	public Command(String perferedSyntax, String description) {
		this.perferedSyntax = perferedSyntax;
		this.description = description;
	}
	
	public abstract String execute(String options[]);
}