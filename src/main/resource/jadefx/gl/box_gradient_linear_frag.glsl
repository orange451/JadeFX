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

vec4 linearGrad( int index, float alpha, float gradientStartPosRotatedX, float gradientEndPosRotatedX, float xLocRotated, float d) {
	return mix(colors[index], colors[index+1], clamp(smoothstep( gradientStartPosRotatedX + stops[index]*d, gradientStartPosRotatedX + stops[index+1]*d, xLocRotated ), 0.0, 1.0) );
}

void main() {
    // these should be uniforms
    vec2 gradient_start_pos = vec2(0.0, 0.0); // top-left
    vec2 gradient_end_pos = vec2(0.2, 1.0); // bottom-right
    
    // define colors and stops
    const int num_stops = 2;
    float stops[32];
    stops[0] = 0.0;
    stops[1] = 1.0;
    
    
	vec2 uv = (gl_FragCoord.xy / boxClip.zw);
    
    float alpha = atan(
        gradient_end_pos.y - gradient_start_pos.y,
        gradient_end_pos.x - gradient_start_pos.x
	); // this is the angle of the gradient in rad
    
    float gradient_startpos_rotated_x = gradient_start_pos.x * cos(-alpha) - gradient_start_pos.y * sin(-alpha);
    float gradient_endpos_rotated_x = gradient_end_pos.x * cos(-alpha) - gradient_end_pos.y * sin(-alpha);
    float len = gradient_endpos_rotated_x - gradient_startpos_rotated_x;
    float x_loc_rotated = uv.x * cos(-alpha) - uv.y * sin(-alpha);
    
    if (num_stops == 1) {
        outColor = colors[0];
    } else if (num_stops > 1) {    
        outColor = mix(colors[0], colors[1], smoothstep(
            gradient_startpos_rotated_x + stops[0] * len,
            gradient_startpos_rotated_x + stops[1] * len,
            x_loc_rotated
        ));
        for (int i = 1; i < num_stops - 1; i++) {
            outColor = mix(outColor, colors[i + 1], smoothstep(
                gradient_startpos_rotated_x + stops[i] * len,
                gradient_startpos_rotated_x + stops[i + 1] * len,
                x_loc_rotated
            ));
        }
    }
	
	// Corner radius
	outColor.a *= clamp(roundedBox(boxClip.xy, boxClip.zw, vertex, cornerRadii), 0.0, 1.0);
	
	if ( vertex.x < scissor.x || vertex.y < scissor.y || vertex.x > scissor.z || vertex.y > scissor.w )
		discard;
		
	if ( outColor.a <= 0.001 )
		discard;
}
