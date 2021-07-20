package io.jadefx.style;

import org.lwjgl.nanovg.NanoVG;

import io.jadefx.paint.Color;
import io.jadefx.stage.Context;
import io.jadefx.util.JadeFXUtil;

public class BackgroundSolid extends Background {

	private Color color;
	
	public BackgroundSolid(Color color) {
		this.color = color;
	}

	@Override
	public void render(Context context, double x, double y, double width, double height, float[] cornerRadii) {
		if ( context == null )
			return;
		
		if ( color == null )
			return;
		
		if ( color.getAlpha() <= 0 )
			return;
		
		boolean hasCorner = cornerRadii[0] != 0 || cornerRadii[1] != 0 || cornerRadii[2] != 0 || cornerRadii[3] != 0;
		
		if ( hasCorner ) {
			JadeFXUtil.fillRoundRect(context, (float) x, (float) y, width, height, cornerRadii, color);
		} else {
			NanoVG.nvgBeginPath(context.getNVG());
			NanoVG.nvgRect(context.getNVG(), (float) x, (float) y, (float) width, (float) height);
			NanoVG.nvgFillColor(context.getNVG(), color.getNVG());
			NanoVG.nvgFill(context.getNVG());
		}
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		return "BackgroundSolid("+color+")";
	}
}
