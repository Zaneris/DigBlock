attribute vec3 a_Position;
attribute float a_TexNormal;

uniform mat4 u_ProjTrans;
uniform vec3 u_VectorToLight;
varying vec2 v_DiffuseUV;
varying float v_Tex;
varying float v_Light;

vec3 normal(int i) {
	if(i==0) return vec3(0,0,1);
	else if (i==1) return vec3(0,0,-1);
	else if(i==2) return vec3(-1,0,0);
	else if(i==3) return vec3(1,0,0);
	else if(i==4) return vec3(0,1,0);
	else return vec3(0,-1,0);
}

vec2 texCoords(int i) {
	if(i==0) return vec2(0,0);
	else if (i==1) return vec2(0,1);
	else if (i==2) return vec2(1,0);
	else return vec2(1,1);
}

void main() {
	float i = mod(a_TexNormal,4.0);
	v_DiffuseUV = texCoords(int(i));
	float normData = (a_TexNormal-i)/4.0;
	i = mod(normData,6.0);
	v_Light = max(dot(normal(int(i)), u_VectorToLight), 0.0);
	v_Light += 0.5;
	gl_Position = u_ProjTrans * vec4(a_Position.xyz, 1.0);
	v_Tex = (normData-i)/6.0;
}