package io.jadefx.gl.util;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import io.jadefx.gl.BoxShadowShader;
import io.jadefx.gl.TexturedQuad;
import io.jadefx.paint.Color;
import io.jadefx.scene.Context;

public class BoxShadowRenderer {

	private static BoxShadowShader boxShadowShader;
	
	private static TexturedQuad unitQuad;
	
	public static void boxShadow(Context context, double xx, double yy, double ww, double hh, double f, double r, Color color, float[] cornerRadii, boolean isInset, float[] boxClip) {

		// Draw shadow to current FBO
		{
			if (boxShadowShader == null)
				boxShadowShader = new BoxShadowShader();

			if (unitQuad == null)
				unitQuad = new TexturedQuad(0, 0, 1, 1, -1);

			boxShadowShader.bind();

			// Enable blending
			GL32.glBlendFunc(GL32.GL_SRC_ALPHA, GL32.GL_ONE_MINUS_SRC_ALPHA);
			GL32.glBlendFuncSeparate(GL32.GL_ONE, GL32.GL_ONE_MINUS_SRC_ALPHA, GL32.GL_ONE, GL32.GL_ONE_MINUS_SRC_ALPHA);
			GL32.glEnable(GL32.GL_BLEND);

			if (f < 0.5)
				f = 0.5f;

			float sigma = (float) (f / 2f);
			float corner = (float) Math.max(f / 2, r); // Legacy. Should be phased out eventually in favor of cornerRadii array.
			float inset = isInset ? 1.0f : 0.0f;
			
			if ( boxClip == null )
				boxClip = new float[] {
						(float) xx,
						(float) yy,
						(float) ww,
						(float) hh
				};

            // Apply uniforms
			GL20.glUniform4f(boxShadowShader.getUniformLocation("boxColor"),
					color.getVector().x,
					color.getVector().y,
					color.getVector().z,
					color.getVector().w);
			GL20.glUniform4f(boxShadowShader.getUniformLocation("box"), (float)xx, (float)yy, (float)(xx+ww), (float)(yy+hh) );
			GL20.glUniform4f(boxShadowShader.getUniformLocation("boxClip"), boxClip[0], boxClip[1], boxClip[0]+boxClip[2], boxClip[1]+boxClip[3] );
			GL20.glUniform4f(boxShadowShader.getUniformLocation("scissor"),
					(float)0,
					(float)0,
					(float)context.getWindow().getWidth(),
					(float)context.getWindow().getHeight());
			GL20.glUniform2f(boxShadowShader.getUniformLocation("window"), context.getWindow().getWidth(), context.getWindow().getHeight());
			GL20.glUniform3f(boxShadowShader.getUniformLocation("sigmaCornerInset"), sigma, corner, inset);
			GL20.glUniform4f(boxShadowShader.getUniformLocation("cornerRadii"), cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3]);
			
			// Draw fullscreen quad
			unitQuad.render();
		}
	}

}
