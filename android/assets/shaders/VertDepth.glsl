attribute vec3 a_Position;
uniform mat4 u_LightMatrix;
varying float v_DistToLight;

void main() {
	vec4 matrix = u_LightMatrix * vec4(a_Position, 1.0);
	v_DistToLight = (matrix.z+1.0)*2;
	gl_Position = matrix;
}