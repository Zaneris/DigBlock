attribute vec3 a_Position;
attribute vec4 a_Color;
uniform mat4 u_ProjTrans;
varying vec4 v_Color;

void main() {
	v_Color = a_Color;
	gl_Position =  u_ProjTrans * vec4(a_Position.xyz, 1.0);
}