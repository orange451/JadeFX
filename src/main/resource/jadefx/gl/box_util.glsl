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

float roundedBox(vec2 lower, vec2 upper, vec2 point, vec4 cornerRadii) {
	float edgeSoftness = 1.0;
	vec2 center = (upper + lower) * 0.5;
	vec2 size = upper - lower;
  
    float distance = roundedBoxSDF(point.xy - center, size / 2.0, cornerRadii);
    return 1.0-smoothstep(-edgeSoftness/2.0, edgeSoftness/2.0, distance);
}