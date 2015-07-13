attribute vec3 a_Position;
attribute vec4 a_Color;
uniform mat4 u_CamMatrix;
varying vec4 v_Color;

void main() {
	v_Color = a_Color;
	gl_Position =  u_CamMatrix * vec4(a_Position, 1.0);
}