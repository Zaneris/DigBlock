#version 120

#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_diffuseTexture;
varying vec2 v_diffuseUV;


void main() {
	gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV);
}