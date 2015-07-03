#version 120

attribute vec3 a_position;
attribute vec2 a_texCoords;

uniform mat4 u_projTrans;
varying vec2 v_diffuseUV;

void main() {
	gl_Position =  u_projTrans * vec4(a_position.xyz, 1.0);
	v_diffuseUV = a_texCoords;
}