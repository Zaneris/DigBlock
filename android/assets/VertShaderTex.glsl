attribute vec3 a_position;
attribute vec2 a_texCoords;
attribute float a_tex;

uniform mat4 u_projTrans;
varying vec2 v_diffuseUV;
varying float v_tex;

void main() {
	gl_Position =  u_projTrans * vec4(a_position.xyz, 1.0);
	v_diffuseUV = a_texCoords;
	v_tex = a_tex;
}