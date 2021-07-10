#version 330

uniform vec4 box;
uniform vec4 boxColor;
uniform vec4 boxClip;
uniform vec4 scissor;
uniform vec3 sigmaCornerInset;
uniform vec4 cornerRadii;

in vec2 vertex;
out vec4 outColor;

// A standard gaussian function, used for weighting samples
float gaussian(float x, float sigma) {
  const float pi = 3.141592653589793;
  return exp(-(x * x) / (2.0 * sigma * sigma)) / (sqrt(2.0 * pi) * sigma);
}

// This approximates the error function, needed for the gaussian integral
vec2 erf(vec2 x) {
  vec2 s = sign(x), a = abs(x);
  x = 1.0 + (0.278393 + (0.230389 + 0.078108 * (a * a)) * a) * a;
  x *= x;
  return s - s / (x * x);
}

// Return the blurred mask along the x dimension
float roundedBoxShadowX(float x, float y, float sigma, float corner, vec2 halfSize) {
  float delta = min(halfSize.y - corner - abs(y), 0.0);
  float curved = halfSize.x - corner + sqrt(max(0.0, corner * corner - delta * delta));
  vec2 integral = 0.5 + 0.5 * erf((x + vec2(-curved, curved)) * (sqrt(0.5) / sigma));
  return integral.y - integral.x;
}

// Test function to generate varying round box
float roundedBoxSDF(vec2 CenterPosition, vec2 Size, vec4 Radius) {
    float corner = (CenterPosition.y<0.0)?
    					(CenterPosition.x>0.0 ?
    						Radius.z
    					:
    						Radius.w)
    				: 
    					(CenterPosition.x>0.0 ?
    						Radius.x
    					:
    						Radius.y);
    
    vec2 q = abs(CenterPosition)-Size+corner;
    return min(max(q.x,q.y),0.0) + length(max(q,0.0)) - corner;
}

// Return the mask for the shadow of a box from lower to upper
float roundedBoxShadow(vec2 lower, vec2 upper, vec2 point, float sigma, float corner) {
  // Center everything to make the math easier
  vec2 center = (lower + upper) * 0.5;
  vec2 halfSize = (upper - lower) * 0.5;
  point -= center;

  // The signal is only non-zero in a limited range, so don't waste samples
  float low = point.y - halfSize.y;
  float high = point.y + halfSize.y;
  float start = clamp(-3.0 * sigma, low, high);
  float end = clamp(3.0 * sigma, low, high);

  // Accumulate samples (we can get away with surprisingly few samples)
  float step = (end - start) / 4.0;
  float y = start + step * 0.5;
  float value = 0.0;
  for (int i = 0; i < 4; i++) {
    value += roundedBoxShadowX(point.x, point.y - y, sigma, corner, halfSize) * gaussian(y, sigma) * step;
    y += step;
  }

  return value;
}

float roundedBox(vec2 lower, vec2 upper, vec2 point, vec4 cornerRadii) {
	float edgeSoftness = 1.0;
	vec2 center = (upper + lower) * 0.5;
	vec2 size = upper - lower;
  
    float distance = roundedBoxSDF(point.xy - center, size / 2.0f, cornerRadii);
    
    return 1.0-smoothstep(-edgeSoftness/2.0, edgeSoftness/2.0, distance);
}

void main() {
	float sigma = sigmaCornerInset.x;
	float corner = sigmaCornerInset.y;
	float inset = sigmaCornerInset.z;
	float shadow = clamp(roundedBoxShadow(box.xy, box.zw, vertex, sigma, corner), 0.0, 1.0);
	float boxClip = clamp(roundedBox(boxClip.xy, boxClip.zw, vertex, cornerRadii), 0.0, 1.0);
	
	float shadowFactorInset = (1.0 - shadow)*(boxClip);
	float shadowFactorNormal = (shadow)*(1.0-boxClip);
	float shadowFactor = mix(shadowFactorNormal, shadowFactorInset, inset);
	
	outColor = boxColor;
	outColor.a *= shadowFactor;
	
	if ( vertex.x < scissor.x || vertex.y < scissor.y || vertex.x > scissor.z || vertex.y > scissor.w )
		discard;
		
	if ( outColor.a <= 0.001 )
		discard;
}
