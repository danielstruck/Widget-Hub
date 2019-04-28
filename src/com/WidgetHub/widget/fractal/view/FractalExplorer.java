package com.WidgetHub.widget.fractal.view;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import com.WidgetHub.widget.fractal.explorer.Commands;
import com.WidgetHub.widget.fractal.explorer.FractalInfo;

public class FractalExplorer extends FractalInfo {
	// TODO combine display frame and monitor into 1 JFrame (maybe make monitor into a JPanel?)
	// TODO make combined display frame icon (maybe a generated fractal?)
	public FractalImageDisplay fractalFrame;
	public MonitorFrame monitor;
	
	private volatile int autoRenderTime;
	private volatile boolean renderRequested;
	
	public FractalExplorer() {
		Commands.setFractalInfo(this);
	}
	
	public void setup() {
		super.setup();
		
		fractalFrame = new FractalImageDisplay(this);
		fractalFrame.setTitle("Fractal Explorer");
		monitor = new MonitorFrame(fractalFrame, 150);
		
		setupControls();
		setupImageRenderer();
		setupAutoRenderer();
	}
	
	private void setupControls() {
		MouseAdapter mouseInteraction = new MouseAdapter() {
			Point mouse = null;
			
			@Override
			public void mousePressed(MouseEvent e) {
				mouse = e.getLocationOnScreen();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (mouse != null) {
					Point current = e.getLocationOnScreen();
					
					double mult = 0.5;
					if (e.isShiftDown())
						mult *= 0.1;
					if (e.isControlDown())
						mult *= 0.01;
					if (e.isAltDown())
						mult = 1 / mult;
					
					xOffset += mult * (mouse.x - current.x) / zoom;
					if (Math.abs(xOffset) > 2)
						xOffset = Math.signum(xOffset) * 2;
					
					yOffset += mult * (mouse.y - current.y) / zoom;
					if (Math.abs(yOffset) > 2)
						yOffset = Math.signum(yOffset) * 2;
					
					mouse = current;
					
					requestRender();
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mouse = null;
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				double mult = 10;
				if (e.isShiftDown())
					mult *= 0.1;
				if (e.isControlDown())
					mult *= 0.01;
				if (e.isAltDown())
					mult = 10 / mult;
				
				zoom -= mult * e.getWheelRotation() * Math.max(0.1, Math.abs((double) zoom / 100));
				
				if (zoom < 1)
					zoom = 1;
				else
					requestRender();
			}
		};
		fractalFrame.addMouseListener(mouseInteraction);
		fractalFrame.addMouseMotionListener(mouseInteraction);
		fractalFrame.addMouseWheelListener(mouseInteraction);
		
		
		KeyAdapter keyCommands = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_SLASH:
						String command = JOptionPane.showInputDialog(fractalFrame, "Command:");
						
						if (command != null && command.startsWith("/")) // remove leading slash
							command = command.substring(1);
						
						String result = Commands.fractalCommands.execute(command);
						
						if (result != null) {
							String[] options = result.split(" ");
							
							switch ((result + " ").split(" ")[1]) {
								case "rend": // Re-renders the image.
									requestRender();
								break;
								case "wins": // Closes and reopens the display windows.
									fractalFrame.setVisible(false);
									monitor.setVisible(false);
									try { Thread.sleep(500); } catch (InterruptedException exc) {}
									fractalFrame.setVisible(true);
									monitor.setVisible(true);
								break;
								case "auto": // Performs automatic updates. Frame times are set to the specified amount.
									try {
										autoRenderTime = Integer.parseInt(options[2]);
									} catch (NumberFormatException exc) {
										JOptionPane.showMessageDialog(fractalFrame, "Not a number: " + options[2]);
									} catch (ArrayIndexOutOfBoundsException exc) {
										JOptionPane.showMessageDialog(fractalFrame, "Must specify an integer. Non-positive integers stop the automatic rendering.");
									}
								break;
								case "refresh": // redraw the fractal and info panel
									fractalFrame.repaint();
									monitor.repaint();
								break;
								default:
									JOptionPane.showMessageDialog(fractalFrame, result);
							}
						}
					break;
					case KeyEvent.VK_UP:
						fractal.cy += .01;
					break;
					case KeyEvent.VK_DOWN:
						fractal.cy -= .01;
					break;
					case KeyEvent.VK_RIGHT:
						fractal.cx += .01;
					break;
					case KeyEvent.VK_LEFT:
						fractal.cx -= .01;
					break;
					case KeyEvent.VK_COMMA:
						if (fractal.n > 2)
							fractal.n--;
					break;
					case KeyEvent.VK_PERIOD:
						fractal.n++;
					break;
				}
			}
		};
		fractalFrame.addKeyListener(keyCommands);
		monitor.addKeyListener(keyCommands);
	}
	
	private void setupImageRenderer() {
		new Thread(() -> {
			while (true) {
				try { Thread.sleep(1); } catch (Exception e) {}
				while (!renderRequested)
					/* do nothing */;
				
				
				final long start = System.currentTimeMillis();
				
				monitor.monitor(
						"Calculating..."
				);
				
				
				ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				
				for (int x = 0; x < imgWidth; x++) {
					final int x_ = x;
					
					service.submit(() -> {
						for (int y = 0; y < imgHeight; y++) {
							double mappedX = ((double) (x_ - imgWidth/2) / zoom + xOffset);
							double mappedY = ((double) (y - imgHeight/2) / zoom + yOffset);
							
							if (Math.hypot(mappedX, mappedY) > 2) {
								img.setRGB(x_, y, 0x003FFF);
							}
							else {
								int membership = fractal.findMembership(mappedX, mappedY, resolution, this);
								int col = 0xFF * membership / resolution;
								img.setRGB(x_, y, displayMode.apply(col));
							}
						}
					});
				}
				
				service.shutdown();
				try {
					if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
						service.shutdownNow();
						
						if (!service.awaitTermination(10, TimeUnit.SECONDS))
							System.err.println("Failed to terminate thread pool");
					}
				} catch (InterruptedException e) {
					service.shutdownNow();
					Thread.currentThread().interrupt();
				}
				
				img.setRGB(imgWidth/2, imgHeight/2, 0xFF00FF);
				
				fractalFrame.repaint();
				
				
				// must calculate frame time before monitor is updated
				final long end = System.currentTimeMillis();
				frameTime = end - start;
				
				
				monitor.monitor(
						"x: " + xOffset,
						"y: " + yOffset,
						"zoom: " + zoom,
						"res: " + resolution,
						"size: " + imgWidth + ", " + imgHeight,
						String.format("scale: %.3f", fractalFrame.scale),
						"frame: " + frameTime + "mS",
						"n: " + fractal.n,
						"c: (" + fractal.cx + ", " + fractal.cy + ")",
						"Set: " + fractal.name(),
						"disp: " + displayMode.name()
				);
				
				
				renderRequested = false;
			}
		}).start();
	}
	private void requestRender() {
		renderRequested = true;
	}
	
	private void setupAutoRenderer() {
		autoRenderTime = 0;
		
		new Thread(() -> {
			while (true) {
				if (autoRenderTime > 0 && fractalFrame != null) {
					requestRender();
					try { Thread.sleep(autoRenderTime-1); } catch (Exception e) {}
				}
				try { Thread.sleep(1); } catch (Exception e) {}
			}
		}).start();
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("scale: %.3f", fractalFrame.scale) + "\n";
	}
}
