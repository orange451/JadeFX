package io.jadefx.gl;

public class BlurShaderOld extends GenericShader {

	public BlurShaderOld() {
		super( "BlurShaderOld",
			BlurShaderOld.class.getResource("blur_vertOld.glsl"),
			BlurShaderOld.class.getResource("blur_fragOld.glsl")
		);
	}
}
