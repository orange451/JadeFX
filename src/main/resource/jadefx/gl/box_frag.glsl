#version 330

uniform vec4 boxClip;
uniform vec4 scissor;
uniform vec4 cornerRadii;

in vec2 vertex;
out vec4 outColor;

// Draw normal rounded box functions
#include box_util.glsl

void main() {
	float boxClip = clamp(roundedBox(boxClip.xy, boxClip.zw, vertex, cornerRadii), 0.0, 1.0);

	outColor = vec4(0.5, 0.7, 0.9, 1.0);
	outColor.a *= boxClip;
	
	if ( vertex.x < scissor.x || vertex.y < scissor.y || vertex.x > scissor.z || vertex.y > scissor.w )
		discard;
		
	if ( outColor.a <= 0.001 )
		discard;
}
