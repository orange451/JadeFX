package io.jadefx.gl;

public class LinearGradientShader extends GenericShader {

	public LinearGradientShader() {
		super( "LinearGradient",
			LinearGradientShader.class.getClassLoader().getResource("jadefx/gl/box_vert.glsl"),
			LinearGradientShader.class.getClassLoader().getResource("jadefx/gl/box_gradient_linear_frag.glsl")
		);
	}
}
