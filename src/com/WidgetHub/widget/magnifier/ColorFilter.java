package com.WidgetHub.widget.magnifier;

import java.awt.image.BufferedImage;

public enum ColorFilter {
	None {
		@Override
		public void apply(BufferedImage img, int filterRGB) {
			// placeholder for doing nothing
		}
	},
	Filter {
		@Override
		public void apply(BufferedImage img, int filterRGB) {
			for (int y = 0; y < img.getHeight(); ++y) {
				for (int x = 0; x < img.getWidth(); ++x) {
					img.setRGB(x, y, img.getRGB(x, y) & filterRGB);
				}
			}
		}
	};
	
	public abstract void apply(BufferedImage img, int filterRGB);
}
