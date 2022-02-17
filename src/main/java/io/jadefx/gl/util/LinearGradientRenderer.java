package io.jadefx.gl.util;

import org.lwjgl.opengl.GL20;
import io.jadefx.gl.LinearGradientShader;
import io.jadefx.gl.TexturedQuad;
import io.jadefx.stage.Context;
import io.jadefx.style.ColorStop;

public class LinearGradientRenderer {

	private static LinearGradientShader shader;
	
	private static TexturedQuad unitQuad;
	
	public static void render(Context context, double angle, ColorStop[] stops, double xx, double yy, double ww, double hh, float[] cornerRadii) {

		if (shader == null)
			shader = new LinearGradientShader();

		if (unitQuad == null)
			unitQuad = new TexturedQuad(0, 0, 1, 1, -1);

		shader.bind();
		
		// Compute position
		float centerx = (float)xx + (float)ww*0.5f;
		float centery = (float)yy + (float)hh*0.5f;
		float startX = centerx - (float)((Math.cos(Math.toRadians(angle)) * ww) * 0.5 + 0.5);
		float startY = centery - (float)((Math.sin(Math.toRadians(angle)) * hh) * 0.5 + 0.5);
		float dirX = (float) Math.cos(Math.toRadians(angle));
		float dirY = (float) Math.sin(Math.toRadians(angle));
		float endX = (float)startX + (dirX * (float)ww);
		float endY = (float)startY + (dirY * (float)hh);
		
		int i = 0;
		float[] fColors = new float[stops.length * 4];
		float[] fStops = new float[stops.length * 1];
		for (ColorStop stop : stops) {
			fColors[i+0] = stop.getColor().getVector().x;
			fColors[i+1] = stop.getColor().getVector().y;
			fColors[i+2] = stop.getColor().getVector().z;
			fColors[i+3] = stop.getColor().getVector().w;
			fStops[i / 4] = stop.getRatio();
			i+=4;
		}
		
		float pixelRatio = context.getWindow().getPixelRatio();

		GL20.glUniform1i(shader.getUniformLocation("numStops"), stops.length);
		GL20.glUniform4fv(shader.getUniformLocation("colors"), fColors);
		GL20.glUniform1fv(shader.getUniformLocation("stops"), fStops);
		GL20.glUniform2f(shader.getUniformLocation("gradientStartPos"), (float)startX, (float)startY);
		GL20.glUniform2f(shader.getUniformLocation("gradientEndPos"), (float)(endX), (float)(endY));

		GL20.glUniform4f(shader.getUniformLocation("boxClip"), (float)xx, (float)yy, (float)(xx+ww)*pixelRatio, (float)(yy+hh)*pixelRatio );
		GL20.glUniform4f(shader.getUniformLocation("scissor"),
				(float)0,
				(float)0,
				(float)context.getWindow().getWidth(),
				(float)context.getWindow().getHeight());
		GL20.glUniform2f(shader.getUniformLocation("window"), context.getWindow().getWidth(), context.getWindow().getHeight());
		GL20.glUniform4f(shader.getUniformLocation("cornerRadii"), cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3]);
		
		// Draw fullscreen quad
		unitQuad.render();
	}
}
