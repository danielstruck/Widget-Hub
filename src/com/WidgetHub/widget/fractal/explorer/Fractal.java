package com.WidgetHub.widget.fractal.explorer;

public enum Fractal {
	Mandelbrot {
		@Override
		public int findMembership(double pixelX, double pixelY, int resolution, FractalInfo info) {
			double	x = 0,
					y = 0;
			int iteration;
			
			for (iteration = resolution; (x*x + y*y) < 4 && iteration > 0; iteration--) {
				double xTmp = x*x - y*y + pixelX;
				y = 2*x*y + pixelY;
				x = xTmp;
			}
			
			return iteration;
		}
	},
	Julia {
		@Override
		public int findMembership(double xPixel, double yPixel, int resolution, FractalInfo info) {
			double	x = 0,
					y = 0;
			int iteration;
			
			for (iteration = resolution; (x*x + y*y) < 4 && iteration > 0; iteration--) {
				double atan = n * Math.atan2(y,  x),
					   sq_n = Math.pow(x*x + y*y, n/2);
				
				x = sq_n * Math.cos(atan) + xPixel;
				y = sq_n * Math.sin(atan) + yPixel;
			}
			
			return iteration;
		}
	},
	JuliaC {
		@Override
		public int findMembership(double x, double y, int resolution, FractalInfo info) {
			int iteration;
			
			for (iteration = resolution; (x*x + y*y) < 4 && iteration > 0; iteration--) {
				double xTmp = x*x - y*y + cx;
				y = 2*x*y + cy;
				x = xTmp;
			}
			
			return iteration;
		}
	};
	public double n, cx, cy;
	
	public abstract int findMembership(double xPixel, double yPixel, int resolution, FractalInfo info);

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
