package io.jadefx.gl;

public class BlurShader extends GenericShader {

	public BlurShader() {
		super(
			BlurShader.class.getClassLoader().getResource("lwjgui/gl/blur_vert.glsl"),
			BlurShader.class.getClassLoader().getResource("lwjgui/gl/blur_frag.glsl")
		);
	}
}
