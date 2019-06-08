package com.WidgetHub.widget.magnifier;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.ArrayList;

public enum Contraster {
	None {
		@Override
		public void apply(BufferedImage img, int value) {
			// placeholder for doing nothing
		}
	},
	Grayscale {
		@Override
		public void apply(BufferedImage img, int value) {
			for (int y = 0; y < img.getHeight(); ++y) {
				for (int x = 0; x < img.getWidth(); ++x) {
					int rgb = img.getRGB(x, y);
					int r = (rgb & 0xFF0000) >> 16;
					int g = (rgb & 0x00FF00) >> 8;
					int b = (rgb & 0x0000FF);
					
					int gray = (r + g + b)/3;
					
					img.setRGB(x, y, new Color(gray, gray, gray).getRGB());
				}
			}
		}
	},
	High_Contrast {
		@Override
		public void apply(BufferedImage img, int exponent) {
			if (exponent < 2)
				return;
			
			if (exponent < 9) {
				final long divisor = ipow(255, exponent - 1);
				
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						int rgb = img.getRGB(x, y) & 0xFFFFFF;
						long r = ipow((rgb >> 16) & 0xFF, exponent) / divisor;
						long g = ipow((rgb >> 8)  & 0xFF, exponent) / divisor;
						long b = ipow((rgb >> 0)  & 0xFF, exponent) / divisor;
						
						img.setRGB(x, y,  (int) ((r << 16) | (g << 8) | (b << 0)));
					}
				}
			}
			else {
				final BigInteger divisor = BigInteger.valueOf(255).pow(exponent - 1);
				
				for (int y = 0; y < img.getHeight(); y++) {
					for (int x = 0; x < img.getWidth(); x++) {
						int rgb = img.getRGB(x, y) & 0xFFFFFF;
						int r = highContrast((rgb >> 16) & 0xFF, exponent, divisor);
						int g = highContrast((rgb >>  8) & 0xFF, exponent, divisor);
						int b = highContrast((rgb >>  0) & 0xFF, exponent, divisor);
						
						img.setRGB(x, y,  (int) ((r << 16) | (g << 8) | (b << 0)));
					}
				}
			}
		}
		
		private int highContrast(int base, int exp, BigInteger divisor) {
			return BigInteger.valueOf(base).pow(exp).divide(divisor).intValue();
		}
		
		private long ipow(int base, int exp) {
			long val = 1;
			
			for (; exp > 0; exp--)
				val *= base;
			
			return val;
		}
	},
	Negative {
		@Override
		public void apply(BufferedImage img, int value) {
			for (int y = 0; y < img.getHeight(); y++) {
				for (int x = 0; x < img.getWidth(); x++) {
					img.setRGB(x, y,  img.getRGB(x, y) ^ 0xFFFFFF); // effectively 255 - colorComponent
				}
			}
		}
	},
	Bitmap {
		@Override
		public void apply(BufferedImage img, int cutoff) {
			cutoff += 64;
			if (cutoff > 255)
				cutoff = 255;
			else if (cutoff < 0)
				cutoff = 0;
			
			Contraster.Grayscale.apply(img, 0);
			
			for (int y = 0; y < img.getHeight(); ++y) {
				for (int x = 0; x < img.getWidth(); ++x) {
					int gray = img.getRGB(x, y) & 0xFF;
					
					if (gray < cutoff) {
						img.setRGB(x, y, 0x000000);
					}
					else {
						img.setRGB(x, y, 0xFFFFFF);
					}
				}
			}
		}
	},
	Sharpen {
		@Override
		public void apply(BufferedImage img, int k) {
			final double k_ = k;
			final double[][] kernel = {
					{-k_/8, -k_/8, -k_/8},
					{-k_/8,  k_+1, -k_/8},
					{-k_/8, -k_/8, -k_/8},
			};
			
			applyKernel(img, kernel);
		}
	},
	Shift {
		@Override
		public void apply(BufferedImage img, int value) {
			final double[][] kernel = {
					{0, 0,  0},
					{0, 1,  0},
					{0, 0, -1},
			};
			
			applyKernel(img, kernel);
		}
	},
	Soft_Shift {
		@Override
		public void apply(BufferedImage img, int value) {
			final double[][] kernel = {
					{0,        0,        0},
					{0,        1, -2.0/5.0},
					{0, -2.0/5.0, -1.0/5.0},
			};
			
			applyKernel(img, kernel);
		}
	},
	Simplify {
		@Override
		public void apply(BufferedImage img, int colorChannels) {
			if (colorChannels < 0)
				return;
			colorChannels += 2;
			
			for (int y = 0; y < img.getHeight(); ++y) {
				for (int x = 0; x < img.getWidth(); ++x) {
					int rgb = img.getRGB(x, y);
					int r = (rgb & 0xFF0000) >> 16;
					int g = (rgb & 0x00FF00) >> 8;
					int b = (rgb & 0x0000FF);

					int r_ = (int) (Math.round(colorChannels * r/255.0) * 255/(colorChannels-1)),
						g_ = (int) (Math.round(colorChannels * g/255.0) * 255/(colorChannels-1)),
						b_ = (int) (Math.round(colorChannels * b/255.0) * 255/(colorChannels-1));
					
					int rgb_ = (r_ << 16) | (g_ << 8) | b_;
					img.setRGB(x, y, rgb_);
				}
			}
		}
	},
	K_Means {
		@Override
		public void apply(BufferedImage img, int numSegments) {
			// TODO Auto-generated method stub
			/*
			if (numSegments <= 0)
				return;

			Point3D[] centroids = new Point3D[numSegments];
			ArrayList<ArrayList<Point3D>> means = new ArrayList<ArrayList<Point3D>>();
			
			for (int i = 0; i < numSegments; i++) {
				int r = (int) ((double) i / numSegments * 255),
					g = (int) ((double) i / numSegments * 255),
					b = (int) ((double) i / numSegments * 255);
				
				means.add(new Point3D(r, g, b));
			}
			
			while (!centroids.equals(means)) {
				centroids = means;
				for (int i = 0; i < means.length; i++) {
					means[i] = new Point3D(0, 0, 0);
				}
				
				for (int y = 0; y < img.getHeight(); ++y) {
					for (int x = 0; x < img.getWidth(); ++x) {
						Point3D pixel = rgbToPoint3D(img.getRGB(x, y));
						int index = classifyPixel(centroids, pixel);
						
						means[index].add(pixel);
						dataCounts[index]++;
					}
				}
			}
			*/
		}
		
		private int classifyPixel(Point3D[] centroids, Point3D pixel) {
			return 0; // TODO method stub
		}
		
		private Point3D rgbToPoint3D(int rgb) {
			return new Point3D((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
		}
		
		private boolean pointArraysEqual(Point3D[] arr1, Point3D[] arr2) {
			for (Point3D elem1: arr1) {
				for (Point3D elem2: arr2) {
					if (!elem1.equals(elem2)) {
						return false;
					}
				}
			}
			
			return true;
		}
	};
	

	
	public abstract void apply(BufferedImage img, int value);
	
	private static void applyKernel(BufferedImage img, double[][] kernel) {
		Contraster.Grayscale.apply(img, 0);
		BufferedImage baseImage = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		baseImage.createGraphics().drawImage(img, 0, 0, null);
		
		for (int y = 0; y < img.getHeight(); ++y) {
			for (int x = 0; x < img.getWidth(); ++x) {
				double weightedSum = 0;
				
				for (int kernelY = 0; kernelY < kernel.length; kernelY++) {
					if (y-1 + kernelY < 0 || y-1 + kernelY >= img.getHeight())
						continue;
					
					for (int kernelX = 0; kernelX < kernel[0].length; kernelX++) {
						if (x-1 + kernelX < 0 || x-1 + kernelX >= img.getWidth())
							continue;
						
						int gray = baseImage.getRGB(x-1 + kernelX, y-1 + kernelY) & 0xFF;
						weightedSum += gray * kernel[kernelY][kernelX];
					}
				}

				if (weightedSum > 255)
					weightedSum = 255;
				else if (weightedSum < 0)
					weightedSum = 0;
				
				int sum = (int) weightedSum;
				int rgb = (sum << 16) | (sum << 8) | sum;
				
				img.setRGB(x, y, rgb);
			}
		}
	}
}

class Point3D {
	double x, y, z;
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double distance(Point3D other) {
		double dx = x - other.x,
			   dy = y - other.y,
			   dz = z - other.z;
		
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	public void add(Point3D other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point3D other = (Point3D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}
}
