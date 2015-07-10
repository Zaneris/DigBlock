attribute vec3 a_Position;
attribute vec2 a_TexCoords;
attribute vec3 a_Normal;
attribute float a_Tex;

uniform mat4 u_ProjTrans;
uniform vec3 u_VectorToLight;
varying vec2 v_DiffuseUV;
varying float v_Tex;
varying float v_Light;

void main() {
	v_Light = max(dot(a_Normal, u_VectorToLight), 0.0);
	v_Light += 0.5;
	gl_Position =  u_ProjTrans * vec4(a_Position.xyz, 1.0);
	v_DiffuseUV = a_TexCoords;
	v_Tex = a_Tex;
}