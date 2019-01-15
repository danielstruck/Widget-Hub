package com.WidgetHub.widget.fractal.explorer;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class FractalInfo {
	public enum DisplayMode {
		tree {
			@Override
			public int apply(int col) {
				int r = ((col % 2) == 0)?  0xFF0000: 0,
					g = ((col % 4) == 0)?  0x00FF00: 0,
					b = (col == 0)?        0x0000FF: 0;
				return r | g | b;
			}
		},
		tree2 {
			@Override
			public int apply(int col) {
				int r = ((col % 2) == 0)? 0xFF0000: 0,
					g = ((col % 3) == 0)? 0x00FF00: 0,
					b = ((col % 2) == 1)? 0x0000FF: 0;
				return r | g | b;
			}
		},
		hsb {
			@Override
			public int apply(int col) {
				float h = ((float) 0xFF / col) % 1,
					  s = 1,
					  b = col > 0 ? 1 : 0;
				return Color.HSBtoRGB(h, s, b);
			}
		},
		gray {
			@Override
			public int apply(int col) {
				int r = col << 16,
					g = col << 8,
					b = col << 0;
				return r | g | b;
			}
		},
		sharp {
			@Override
			public int apply(int col) {
				col = col*col;
				
				return gray.apply(col / 0xFF);
			}
		},
		honey {
			@Override
			public int apply(int col) {
				return rawSquare(col, 2);
			}
		},
		goldMine {
			@Override
			public int apply(int col) {
				return rawSquare(col, 3);
			}
		},
		silverMine {
			@Override
			public int apply(int col) {
				return rawSquare(col, 4);
			}
		},
		noire {
			@Override
			public int apply(int col) {
				return rawSquare(col, 5);
			}
		};

		private final int primes[] = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
				43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109,
				113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191,
				193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251};
		
		public abstract int apply(int col);
		
		public int rawSquare(int col, int n) {
			int div = 1;
			for (int i = 0; i < n; i++) {
				col *= col;
				div *= 0xFF;
			}
			
			return col / div;
		}
		
		
		public boolean isPrime(int col) {
			int hi = primes.length - 1,
				lo = 0,
				mid;
			
			while (hi >= lo) {
				mid = lo + (hi - lo) / 2;
				
				if (primes[mid] == col)
					return true;
				else if (primes[mid] > col)
					hi = mid - 1;
				else
					lo = mid + 1;
			}
			
			return false;
		}
		
		public static String nameList(String delim) {
			String names = "";
			
			for (int i = 0; i < values().length; i++) {
				names += values()[i].name();
				if (i < values().length - 1)
					names += delim;
			}
			
			return names;
		}
	}
	
	public BufferedImage img;
	public Fractal fractal = Fractal.Julia;
	
	public int 	  imgWidth,
				  imgHeight,
				  resolution;
	public double xOffset,
				  yOffset;
	public long   zoom,
				  frameTime;
	public DisplayMode displayMode;
	
	
	protected FractalInfo() {
		fractal = Fractal.Julia;
		imgWidth = 100;
		imgHeight = 100;
		xOffset = 0;
		yOffset = 0;
		zoom = 20;
		resolution = 100;
		displayMode = DisplayMode.gray;
	}
	
	public void setup() {
		img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
	}
	
	public String toString1() {
		return "Vars: \n" +
		"x: " + -xOffset + "\n" +
		"y: " + -yOffset + "\n" +
		"zoom: " + zoom + "\n" +
		"res: " + resolution + "\n" +
		"size: " + imgWidth + ", " + imgHeight + "\n" +
		"frame: " + frameTime + "mS" + "\n" +
		"n: " + fractal.n + "\n" +
		"c: (" + fractal.cx + ", " + fractal.cy + ")" + "\n" +
		"disp: " + displayMode.name() + "\n";
	}
	
	@Override
	public String toString() {
		return "FractalInfo [fractal=" + fractal + ", imgWidth=" + imgWidth + ", imgHeight=" +
				imgHeight + ", resolution=" + resolution + ", xOffset=" + xOffset +
				", yOffset=" + yOffset + ", zoom=" + zoom + ", frameTime=" + frameTime + "]";
	}
	
	public FractalInfo setDisplayMode(DisplayMode mode) {
		this.displayMode = mode;
		
		return this;
	}

	public FractalInfo setImage(BufferedImage img) {
		this.img = img;
		
		return this;
	}
	
	public FractalInfo setFractal(Fractal fractal) {
		this.fractal = fractal;
		
		return this;
	}
	
	public FractalInfo setImgWidth(int width) {
		this.imgWidth = width;
		
		return this;
	}
	
	public FractalInfo setImgHeight(int height) {
		this.imgHeight = height;
		
		return this;
	}
	
	public FractalInfo setXOffset(int x) {
		this.xOffset = x;
		
		return this;
	}
	
	public FractalInfo setYOffset(int y) {
		this.yOffset = y;
		
		return this;
	}
	
	public FractalInfo setZoom(int zoom) {
		this.zoom = zoom;
		
		return this;
	}
	
	public FractalInfo setResolution(int res) {
		this.resolution = res;
		
		return this;
	}
}