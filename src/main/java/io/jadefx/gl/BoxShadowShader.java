package io.jadefx.gl;

public class BoxShadowShader extends GenericShader {

	public BoxShadowShader() {
		super(
			BoxShadowShader.class.getClassLoader().getResource("lwjgui/gl/box_shadow_vert.glsl"),
			BoxShadowShader.class.getClassLoader().getResource("lwjgui/gl/box_shadow_frag.glsl")
		);
	}
}
