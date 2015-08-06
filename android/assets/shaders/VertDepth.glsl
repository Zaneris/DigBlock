attribute vec3 a_Position;
attribute float a_TexNormal;
uniform mat4 u_LightMatrix;
varying float v_Height;

void main() {
	float uv = mod(a_TexNormal,4.0);
	float normal = mod((a_TexNormal-uv)/4.0,6.0);
	if(uv>1.5 && (normal<2.5||normal>4.5)) 
		v_Height = (a_Position.y+0.3)/256.0;
	else 
		v_Height = a_Position.y/256.0;
	gl_Position = u_LightMatrix * vec4(a_Position, 1.0);
}
