#version 330

uniform vec4 boxClip;
uniform vec2 window;

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec4 inColor;

out vec2 vertex;

void main() {
  vertex = mix(boxClip.xy, boxClip.zw, inPos.xy);
  gl_Position = vec4(vertex / window * 2.0 - 1.0, 0.0, 1.0);
}