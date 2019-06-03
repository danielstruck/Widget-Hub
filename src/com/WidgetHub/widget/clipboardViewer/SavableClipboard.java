package com.WidgetHub.widget.clipboardViewer;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SavableClipboard implements Transferable {
	String text;
	Image img;
	List<File> files;
	
	public SavableClipboard(ClipboardData<?>[] data) {
		try {
			this.files = ((ClipboardFileData) data[0]).getData();
		} catch (UnsupportedFlavorException | IOException e) {}
		
		try {
			this.text = ((ClipboardStringData) data[1]).getData();
		} catch (UnsupportedFlavorException | IOException e) {}
		
		try {
			this.img = ((ClipboardImageData) data[2]).getData();
		} catch (UnsupportedFlavorException | IOException e) {}
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.imageFlavor, DataFlavor.stringFlavor, DataFlavor.javaFileListFlavor};
	}
	
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (flavor.equals(DataFlavor.imageFlavor))
			return img;
		else if (flavor.equals(DataFlavor.stringFlavor))
			return text;
		else if (flavor.equals(DataFlavor.javaFileListFlavor))
			return files;
		else
			throw new UnsupportedFlavorException(flavor);
	}
}