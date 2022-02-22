package io.jadefx.gl;

public class BlurShaderOld extends GenericShader {

	public BlurShaderOld() {
		super( "BlurShaderOld",
			BlurShaderOld.class.getClassLoader().getResource("blur_vertOld.glsl"),
			BlurShaderOld.class.getClassLoader().getResource("blur_fragOld.glsl")
		);
	}
}
