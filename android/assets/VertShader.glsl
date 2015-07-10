attribute vec3 a_position;
attribute vec4 a_color;
uniform mat4 u_projTrans;
varying vec4 v_Color;

void main() {
	v_Color = a_color;
	gl_Position =  u_projTrans * vec4(a_position.xyz, 1.0);
}