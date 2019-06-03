package com.WidgetHub.widget.clipboardViewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.WidgetHub.tone.Note;
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
	private static final String iconPath = null; // TODO make clipboard widget icon
	
	// instance variables
	private ArrayList<SavableClipboard> clipboards;
	private WidgetResizer resizer;
	private int infoHeight;
	private int scroll;
	private int spacing;
	private boolean promptOpenFlag;
	private ClipboardData<?> data[] = {new ClipboardFileData(), new ClipboardStringData(), new ClipboardImageData(), new ClipboardNullData()};
	
	
	public ClipboardWidget() {
		super(isTransparent, updateDelay, iconPath);

		clipboards = new ArrayList<SavableClipboard>();
		infoHeight = 0;
		scroll = 0;
		spacing = 5;
		
		resizer = new WidgetResizer(this);
		panel.addMouseListener(resizer);
		panel.addMouseMotionListener(resizer);
		
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
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_0:
					case KeyEvent.VK_1:
					case KeyEvent.VK_2:
					case KeyEvent.VK_3:
					case KeyEvent.VK_4:
					case KeyEvent.VK_5:
					case KeyEvent.VK_6:
					case KeyEvent.VK_7:
					case KeyEvent.VK_8:
					case KeyEvent.VK_9:
						int index = e.getKeyChar() - '0';
						
						if (index < clipboards.size()) {
							SavableClipboard clip = clipboards.get(index);
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clip, null);
						}
						else {
							Note.playSinNotes(75, "A4");
						}
					break;
				}
			}
		});
		
		setJMenuBar(menubar(this));
		setTitle("Clipboard Widget");
		setBackground(new Color(80, 80, 80));
		setSize(450, 500);
		setMinimumSize(new Dimension(50, 50));
		revalidate();
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		int y = -scroll;
		
		boolean dataMatchFound = false;
		for (ClipboardData<?> dataElement: data) {
			if (dataElement.getClass().getName().equals(ClipboardNullData.class.getName()) && dataMatchFound)
				continue;
			
			int canvasHeight = dataElement.getHeight();
			if (canvasHeight > 0) {
				BufferedImage canvas = new BufferedImage(panel.getWidth(), canvasHeight, BufferedImage.TYPE_INT_ARGB);
				dataElement.tryFlavor(canvas);
				g.drawImage(canvas, 0, y, null);
				y += canvasHeight + spacing;
				dataMatchFound = true;
			}
		}
		
		infoHeight = y + scroll;
		
		g.setColor(new Color(0, 255, 255, 40));
		g.fillOval(panel.getWidth() - WidgetResizer.DRAG_AREA_SZ, panel.getHeight() - WidgetResizer.DRAG_AREA_SZ, 2 * WidgetResizer.DRAG_AREA_SZ, 2 * WidgetResizer.DRAG_AREA_SZ);
		
		g.setColor(Color.black);
		g.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);
	}
	
	
	public JMenuBar menubar(JFrame frame) {
		ClipboardWidget widget = this;
		
		JMenuBar menuBar = new JMenuBar();
		
		menuBar.add(
			menu("File", 
				item("Edit", (action) -> {
					JOptionPane.showMessageDialog(frame, "Edit tool has been disabled due to bugs.");
//					try {
//						if (stringFlavor) {
//							String input = JOptionPane.showInputDialog(frame, "Change clipboard contents", ClipboardData.clipboard.getData(DataFlavor.stringFlavor));
//							
//							if (input != null) {
//								StringSelection str = new StringSelection(input);
//								ClipboardData.clipboard.setContents(str, str);
//							}
//							else {
//								JOptionPane.showMessageDialog(frame, "Clipboard edit canceled");
//							}
//						}
//						if (imageFlavor) {
//							JOptionPane.showMessageDialog(frame, "Image editing unimplemented");
//						}
//						if (javaFileListFlavor) {
//							JOptionPane.showMessageDialog(frame, "File editing unimplemented");
//						}
//						else {
//							JOptionPane.showMessageDialog(frame, "Clipboard data type is unrecognized: ");// + lastDataType.getClass().getName());
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}), // Edit
				item("Save", (action) -> {
					clipboards.add(new SavableClipboard(data));
				}), // Save
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
							SavableClipboard clip = clipboards.get(index);
							
							int y = 0;
							
							if (clip.files != null) {
								BufferedImage img = new BufferedImage((int) previewDim.getWidth(), ClipboardFileData.getHeight(clip.files), BufferedImage.TYPE_INT_ARGB);
								Graphics2D g2 = img.createGraphics();
								((ClipboardFileData) data[0]).drawFlavor(clip.files, g2);
								g.drawImage(img, 0, y, null);
								y += img.getHeight();
							}

							if (clip.text != null) {
								BufferedImage img = new BufferedImage((int) previewDim.getWidth(), ClipboardStringData.getHeight(clip.text), BufferedImage.TYPE_INT_ARGB);
								Graphics2D g2 = img.createGraphics();
								((ClipboardStringData) data[1]).drawFlavor(clip.text, g2);
								g.drawImage(img, 0, y, null);
								y += img.getHeight();
							}

							if (clip.img != null) {
								BufferedImage img = new BufferedImage((int) previewDim.getWidth(), ClipboardImageData.getHeight(clip.img), BufferedImage.TYPE_INT_ARGB);
								Graphics2D g2 = img.createGraphics();
								((ClipboardImageData) data[2]).drawFlavor(clip.img, g2);
								g.drawImage(img, 0, y, null);
								y += img.getHeight();
							}
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
					
					addBagLine(constraints, 0, 0, p, "Preview", previewPanel);
					addBagLine(constraints, 0, 1, p, "Index", clipboardIndexBox);
					
					if (!promptOpenFlag) {
						promptOpenFlag = true;
						new Thread(() -> {
							while (promptOpenFlag) {
								previewPanel.repaint();
								try { Thread.sleep(100); } catch (Exception exc) {}
							}
						}).start();
						
						if (JOptionPane.showConfirmDialog(widget, p, "Clipboard Selector", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
							SavableClipboard clip = clipboards.get(clipboardIndexBox.getSelectedIndex());
							
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clip, null);
							
							promptOpenFlag = false;
						}
						else {
							promptOpenFlag = false;
						}
					}
				}) // Load
			) // File
		);
		
		menuBar.add(
			menu("Options"//,
//				item("Exit", (action) -> { // deprecated for widget hub
//					widget.close();
//				}) // exit
			) // Options
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
