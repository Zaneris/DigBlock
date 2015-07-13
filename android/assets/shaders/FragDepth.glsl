#ifdef GL_ES
	precision mediump float;
#endif

uniform float u_WorldSize;
varying float v_DistToLight;

vec4 rgba(float dist) {
	vec4 final = vec4(0);
	if(dist<=1.0) {
		final.r = dist;
		return final;
	}
	final.r = 1.0;
	dist -= 1.0;
	if(dist<=1.0) {
		final.g = dist;
		return final;
	}
	final.g = 1.0;
	dist -= 1.0;
	if(dist<=1.0) {
		final.b = dist;
		return final;
	}
	final.b = 1.0;
	dist -= 1.0;
	final.a = dist;
	return final;
}

void main() {
	gl_FragColor = rgba(v_DistToLight);
}