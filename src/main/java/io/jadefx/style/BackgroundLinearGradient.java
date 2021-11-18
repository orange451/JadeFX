package io.jadefx.style;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import io.jadefx.paint.Color;
import io.jadefx.stage.Context;
import io.jadefx.util.JadeFXUtil;

public class BackgroundLinearGradient extends Background {
	private ColorStop[] stops;
	private float angle;
	
	public BackgroundLinearGradient(float angle, ColorStop... stops) {
		this.stops = stops;
		this.angle = angle;
	}
	
	public BackgroundLinearGradient(float angle, Color...colors) {
		this.angle = angle;
		
		this.stops = new ColorStop[colors.length];
		for (int i = 0; i < colors.length; i++) {
			this.stops[i] = new ColorStop( colors[i], (float)i / (float)(colors.length-1) );
		}
	}

	@Override
	public void render(Context context, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;
		
		if ( stops == null || stops.length == 0 )
			return;
		
		if ( cornerRadii == null )
			cornerRadii = new float[] {0,0,0,0};
		
		JadeFXUtil.linearGradient(context, angle, stops, x, y, width, height, cornerRadii);
	}
}
