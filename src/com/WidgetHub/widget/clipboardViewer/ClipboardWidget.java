package com.WidgetHub.widget.clipboardViewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.WidgetHub.widget.AbstractWidget;
import com.WidgetHub.widget.WidgetResizer;

/**
 * A widget to view and save clipboard data. Useful for multitasking!
 * 
 * @author Daniel Struck
 *
 */
public class ClipboardWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;
	
	// constructor info
	private static final boolean isTransparent = true;
	private static final int updateDelay = 100;
	private static final String iconPath = null;
	
	// settings
	private static final int fontSize = 15;
	private static final int horizBuff = 2;
	private static final Font flavorTypeFont = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
	private static final Color flavorTypeColor = Color.magenta;
	
	// instance variables
	private static final Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
	private static boolean stringFlavor, javaFileListFlavor, imageFlavor;
	private ArrayList<SavableClipboard> clipboards;
	private WidgetResizer resizer;
	private int infoHeight;
	private int scroll;
	private boolean promptOpenFlag;
	
	
	public ClipboardWidget() {
		super(isTransparent, updateDelay, iconPath);
		
		setTitle("Clipboard Widget");

		clipboards = new ArrayList<SavableClipboard>();
		infoHeight = 0;
		scroll = 0;
		stringFlavor = false;
		javaFileListFlavor = false;
		imageFlavor = false;
		
		
		resizer = new WidgetResizer(this);
		panel.addMouseListener(resizer);
		panel.addMouseMotionListener(resizer);
		setBackground(new Color(80, 80, 80));
		
		setJMenuBar(menubar(this));
		
		panel.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scroll += 20 * e.getWheelRotation();
				
				if (scroll < 0)
					scroll = 0;
				else if (scroll > infoHeight - 10)
					scroll = infoHeight - 10;
			}
		});
		
		setSize(450, 500);
		setMinimumSize(new Dimension(50, 50));
		revalidate();
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		g.setColor(new Color(0, 255, 255, 40));
		g.fillOval(panel.getWidth() - WidgetResizer.DRAG_AREA_SZ, panel.getHeight() - WidgetResizer.DRAG_AREA_SZ, 2*WidgetResizer.DRAG_AREA_SZ, 2*WidgetResizer.DRAG_AREA_SZ);
		
		Point pos = new Point(horizBuff, -scroll);
		boolean handledFlavor = true;
		handledFlavor &= tryFileFlavor(g, pos);
		handledFlavor &= tryStringFlavor(g, pos);
		handledFlavor &= tryImageFlavor(g, pos);
		if (handledFlavor)
			unhandledFlavor(g, pos);
		
		infoHeight = pos.y + scroll;
		
		g.setColor(Color.black);
		g.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);
	}
	private boolean tryFileFlavor(Graphics g, Point pos) {
		try {
			pos.y += fontSize;
			g.setFont(flavorTypeFont);
			g.setColor(flavorTypeColor);
			g.drawString("FILE", pos.x, pos.y);
			
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>) c.getData(DataFlavor.javaFileListFlavor);
			
			drawFileFlavor(files, g, pos);
			
			javaFileListFlavor = true;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private void drawFileFlavor(List<File> files, Graphics g, Point pos) {
		int longestX = 0;
		Point borderStart = new Point(pos.x, pos.y + 1);
		for (File f: files) {
			pos.y += fontSize;
			g.setColor((f.isDirectory()? Color.green: Color.blue));
			g.drawString(f.getName(), pos.x + 2, pos.y);
			longestX = Math.max(longestX, g.getFontMetrics().stringWidth(f.getName()) + horizBuff);
		}
		pos.y += 5;
		g.drawRect(borderStart.x, borderStart.y, longestX + 2, pos.y - borderStart.y);
	}
	private boolean tryStringFlavor(Graphics g, Point pos) {
		try {
			pos.y += fontSize;
			g.setFont(flavorTypeFont);
			g.setColor(flavorTypeColor);
			g.drawString("TEXT", pos.x, pos.y);
			
			String text = (String) c.getData(DataFlavor.stringFlavor);
			
			drawStringFlavor(text, g, pos);
			
			stringFlavor = true;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private void drawStringFlavor(String text, Graphics g, Point pos) {
		g.setColor(Color.black);
		int longestX = 0;
		Point borderStart = new Point(pos.x, pos.y + 1);
		for (String s: text.split("\n")) {
			pos.y += fontSize;
			s = s.replace("\t", "    ");
			g.drawString(s, pos.x + 2, pos.y);
			longestX = Math.max(longestX, g.getFontMetrics().stringWidth(s) + horizBuff);
		}
		pos.y += 5;
		g.drawRect(borderStart.x, borderStart.y, longestX + 2, pos.y - borderStart.y);
	}
	private boolean tryImageFlavor(Graphics g, Point pos) {
		try {
			pos.y += fontSize;
			g.setFont(flavorTypeFont);
			g.setColor(flavorTypeColor);
			g.drawString("IMAGE", pos.x, pos.y);
			
			Image img = (Image) c.getData(DataFlavor.imageFlavor);
			
			drawImageFlavor(img, g, pos);
			
			imageFlavor = true;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private void drawImageFlavor(Image img, Graphics g, Point pos) {
		Rectangle border = new Rectangle(pos.x - 1, pos.y + 5 - 1, img.getWidth(null) + 1, img.getHeight(null) + 1);
		g.setColor(Color.black);
		g.drawRect(border.x, border.y, border.width, border.height);
		g.drawImage(img, pos.x, pos.y + 5, null);
		
		pos.y += border.height;
	}
	private void unhandledFlavor(Graphics g, Point pos) {
		pos.y += fontSize;
		g.setFont(flavorTypeFont);
		g.setColor(flavorTypeColor);
		
		for (DataFlavor f: c.getAvailableDataFlavors()) {
			try {
				c.getData(f);
				
				g.drawString("unhandled DataFlavor: " + f.getClass().getCanonicalName(), pos.x, pos.y);
				
				return;
			} catch (Exception e) {}
		}
		
		g.drawString("unhandled DataFlavor of unknown type", pos.x, pos.y);
	}
	
	
	public JMenuBar menubar(JFrame frame) {
		ClipboardWidget widget = this;
		
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(
			menu("File", 
				item("Edit", (action) -> {
					try {
						if (stringFlavor) {
							String input = JOptionPane.showInputDialog(frame, "Change clipboard contents", c.getData(DataFlavor.stringFlavor));
							
							if (input != null) {
								StringSelection str = new StringSelection(input);
								c.setContents(str, str);
							}
							else {
								JOptionPane.showMessageDialog(frame, "Clipboard edit canceled");
							}
						}
						if (imageFlavor) {
							JOptionPane.showMessageDialog(frame, "Image editing unimplemented");
						}
						if (javaFileListFlavor) {
							JOptionPane.showMessageDialog(frame, "File editing unimplemented");
						}
//						else {
//							JOptionPane.showMessageDialog(frame, "Clipboard data type is unrecognized: ");// + lastDataType.getClass().getName());
//						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}), // edit
				item("Save", (action) -> {
					try {
						SavableClipboard.Builder factory = new SavableClipboard.Builder();
						
						if (stringFlavor)
							factory.setText((String) c.getData(DataFlavor.stringFlavor));
						if (imageFlavor)
							factory.setImage((Image) c.getData(DataFlavor.imageFlavor));
						if (javaFileListFlavor)
							factory.setFiles((List<File>) c.getData(DataFlavor.javaFileListFlavor));
//						else
//							JOptionPane.showMessageDialog(frame, "Clipboard data type is unrecognized: ");// + lastDataType.getClass().getName());
						
						clipboards.add(factory.instantiate());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}), // save
				item("Load", (action) -> {
					if (clipboards.size() == 0) {
						JOptionPane.showMessageDialog(widget, "No cliboards saved.");
						return;
					}
					
					JComboBox<Integer> clipboardIndexBox = new JComboBox<Integer>();
					for (int i = 1; i <= clipboards.size(); i++)
						clipboardIndexBox.addItem(i);
					
					Dimension previewDim = new Dimension(300, 300);
					JPanel previewPanel = new JPanel() {
						private static final long serialVersionUID = 1L;
						
						public void paint(Graphics g) {
							super.paint(g);
							
							int index = clipboardIndexBox.getSelectedIndex();
							Point pos = new Point(horizBuff, 0);
							if (clipboards.get(index).files != null)
								drawFileFlavor(clipboards.get(index).files, g, pos);
							if (clipboards.get(index).text != null)
								drawStringFlavor(clipboards.get(index).text, g, pos);
							if (clipboards.get(index).img != null)
								drawImageFlavor(clipboards.get(index).img, g, pos);
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
					addBagLine(constraints, 0, row++, p, "Preview", previewPanel);
					addBagLine(constraints, 0, row++, p, "Index", clipboardIndexBox);
					
					if (!promptOpenFlag) {
						promptOpenFlag = true;
						new Thread(() -> {
							while (promptOpenFlag) {
								previewPanel.repaint();
								try { Thread.sleep(10); } catch (Exception exc) {}
							}
						}).start();
						
						if (JOptionPane.showConfirmDialog(widget, p, "Clipboard Selector", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
							SavableClipboard clip = clipboards.get(clipboardIndexBox.getSelectedIndex());
							
							StringSelection ss = new StringSelection(clip.text);
							c.setContents(ss, null);
							
							ImageSelection is = new ImageSelection(clip.img);
							c.setContents(is, null);
							
							FileSelection fs = new FileSelection(clip.files);
							c.setContents(fs, null);
							
							promptOpenFlag = false;
						}
						else {
							promptOpenFlag = false;
						}
					}
				}) // load
			)
		);
		
		menuBar.add(
			menu("Options", 
				item("Exit", (action) -> {
					widget.close();
				}) // exit
			)
		);
		
		return menuBar;
	}
	public JMenu menu(String name, JMenuItem ...items) {
		JMenu menu = new JMenu(name);
		
		for (JMenuItem item: items)
			menu.add(item);
		
		return menu;
	}
	public JMenuItem item(String name, ActionListener action) {
		JMenuItem menuItem = new JMenuItem(name);
		
		menuItem.addActionListener(action);
		
		return menuItem;
	}
	
	
	
	public static void addBagLine(GridBagConstraints constraints, int col, int row, JPanel panel, String label, JComponent component) {
		col = col * 2;
		addBagToPanel(constraints, col, row, panel, new JLabel(label));
		addBagToPanel(constraints, col + 1, row, panel, component);
	}
	public static void addBagToPanel(GridBagConstraints constraints, int x, int y, JPanel panel, JComponent component) {
		constraints.gridy = y;
		constraints.gridx = x;
		panel.add(component, constraints);
	}
}

class SavableClipboard {
	String text;
	Image img;
	List<File> files;
	
	private SavableClipboard(String text, Image img, List<File> files) {
		this.text = text;
		this.img = img;
		this.files = files;
	}
	
	static class Builder {
		private String text = null;
		private Image img = null;
		private List<File> files = null;
		
		public Builder() {
			text = null;
			img = null;
			files = null;
		}
		
		public Builder setText(String text) {
			this.text = text;
			return this;
		}
		
		public Builder setImage(Image img) {
			this.img = img;
			return this;
		}
		
		public Builder setFiles(List<File> files) {
			this.files = files;
			return this;
		}
		
		public SavableClipboard instantiate() {
			return new SavableClipboard(text, img, files);
		}
	}
}

class ImageSelection implements Transferable {
	private Image image;
	
	
	public ImageSelection(Image image) {
		this.image = image;
	}
	
	
	// Returns supported flavors
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.imageFlavor};
	}
	
	
	// Returns true if flavor is supported
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}
	
	
	// Returns image
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!DataFlavor.imageFlavor.equals(flavor))
			throw new UnsupportedFlavorException(flavor);
		
		return image;
	}
}

class FileSelection implements Transferable {
	private List<File> files;
	
	
	public FileSelection(List<File> files) {
		this.files = files;
	}
	

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.javaFileListFlavor};
	}
	

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}
	

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (!DataFlavor.javaFileListFlavor.equals(flavor))
			throw new UnsupportedFlavorException(flavor);
		
		return files;
	}
}
