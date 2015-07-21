#ifdef GL_ES
	precision mediump float;
#endif

varying float v_Height;

void main() {
	gl_FragColor.rgb = vec3(v_Height);
	gl_FragColor.a = 1.0;
}