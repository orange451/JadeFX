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

		// Draw shadow to current FBO
		{
			if (shader == null)
				shader = new LinearGradientShader();

			if (unitQuad == null)
				unitQuad = new TexturedQuad(0, 0, 1, 1, -1);

			shader.bind();
			
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

			double ex = xx+ww;
			double ey = yy+hh;
			float pixelRatio = context.getWindow().getPixelRatio();

			GL20.glUniform1i(shader.getUniformLocation("numStops"), stops.length);
			GL20.glUniform4fv(shader.getUniformLocation("colors"), fColors);
			GL20.glUniform1fv(shader.getUniformLocation("stops"), fStops);
			GL20.glUniform2f(shader.getUniformLocation("gradientStartPos"), (float)xx, (float)yy);
			GL20.glUniform2f(shader.getUniformLocation("gradientEndPos"), (float)(ex), (float)(ey));

			GL20.glUniform4f(shader.getUniformLocation("boxClip"), (float)xx, (float)yy, (float)ex*pixelRatio, (float)ey*pixelRatio );
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
}
