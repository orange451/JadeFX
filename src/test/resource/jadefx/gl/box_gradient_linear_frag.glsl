#version 330

uniform vec4 boxClip;
uniform vec4 scissor;
uniform vec4 cornerRadii;

uniform vec2  gradientStartPos;
uniform vec2  gradientEndPos;
uniform int   numStops;
uniform vec4  colors[16];
uniform float stops[16];

in vec2 vertex;
out vec4 outColor;

// Draw normal rounded box functions
#include box_util.glsl

void main() {
    vec2 dt = gradientEndPos - gradientStartPos;
    vec2 pt = gl_FragCoord.xy - gradientStartPos;
    float t = dot(pt, dt)/ dot(dt, dt);

    vec4 color = colors[0];    
    for (int i = 0; i < numStops-1; i++) {
    	color = mix(color, colors[i+1], clamp((t - stops[i])/(stops[i+1] - stops[i]), 0.0, 1.0));
    }

	outColor = color;
	outColor.a *= clamp(roundedBox(boxClip.xy, boxClip.zw, vertex, cornerRadii), 0.0, 1.0);
	
	if ( vertex.x < scissor.x || vertex.y < scissor.y || vertex.x > scissor.z || vertex.y > scissor.w )
		discard;
		
	if ( outColor.a <= 0.001 )
		discard;
}
