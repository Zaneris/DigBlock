attribute vec3 a_Position;
attribute float a_TexNormal;
uniform mat4 u_LightMatrix;
varying float v_Height;

void main() {
	v_Height = a_Position.y;
	gl_Position = u_LightMatrix * vec4(a_Position, 1.0);
}
