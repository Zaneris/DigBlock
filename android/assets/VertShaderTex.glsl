attribute vec3 a_Position;
attribute float a_TexNormal;

uniform mat4 u_ProjTrans;
uniform vec3 u_VectorToLight;
varying vec2 v_DiffuseUV;
varying float v_Tex;
varying float v_Light;

void main() {
	float f = mod(a_TexNormal,4.0);
	int i = int(f);
	if(i==0)
		v_DiffuseUV = vec2(0,0);
	else if (i==1)
		v_DiffuseUV = vec2(0,1);
	else if (i==2)
		v_DiffuseUV = vec2(1,0);
	else
		v_DiffuseUV = vec2(1,1);
	float normData = (a_TexNormal-f)/4.0;
	f = mod(normData,6.0);
	i = int(f);
	vec3 normal;
	if(i==0)		// North
		normal = vec3(0,0,1);
	else if (i==1)	// South
		normal = vec3(0,0,-1);
	else if(i==2)	// East
		normal = vec3(-1,0,0);
	else if(i==3)	// West
		normal = vec3(1,0,0);
	else if(i==4)	// Top
		normal = vec3(0,1,0);
	else			// Bottom
		normal = vec3(0,-1,0);
	v_Light = max(dot(normal, u_VectorToLight), 0.0);
	v_Light += 0.5;
	gl_Position =  u_ProjTrans * vec4(a_Position.xyz, 1.0);
	v_Tex = (normData-f)/6.0;
}