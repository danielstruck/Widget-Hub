package com.WidgetHub.widget.fractal;

import java.awt.Graphics;

import com.WidgetHub.widget.AbstractWidget;
import com.WidgetHub.widget.fractal.explorer.Fractal;
import com.WidgetHub.widget.fractal.view.FractalExplorer;

/**
 * An import of a fractal explorer application.
 * 
 * @author Daniel Struck
 *
 */
public class FractalWidget extends AbstractWidget {
	private static final long serialVersionUID = 1L;


	public FractalWidget() {
		Fractal.Julia.n = 2;
		Fractal.JuliaC.cx = -.7;
		Fractal.JuliaC.cy = .27015;
		
		new FractalExplorer().setFractal(Fractal.JuliaC)
							 .setImgHeight(50)
							 .setImgWidth(50)
							 .setXOffset(0)
							 .setYOffset(0)
							 .setZoom(15)
							 .setResolution(1000)
							 .setup();
	}
	
	
	@Override
	public void update() {
		
	}
	
	
	@Override
	public void render(Graphics g) {
		
	}
}
