package io.jadefx.gl;

public class BlurShader extends GenericShader {

	public BlurShader() {
		super( "BlurShader",
			BlurShader.class.getClassLoader().getResource("jadefx/gl/blur_vert.glsl"),
			BlurShader.class.getClassLoader().getResource("jadefx/gl/blur_frag.glsl")
		);
	}
}
