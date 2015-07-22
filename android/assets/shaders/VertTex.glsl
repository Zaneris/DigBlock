attribute vec3 a_Position;
attribute float a_TexNormal;

uniform mat4 u_CamMatrix;
uniform mat4 u_LightMatrix;
uniform vec3 u_LightVector;
varying vec2 v_DiffuseUV;
varying vec2 v_DepthMap;
varying float v_Tex;
varying float v_Light;
varying float v_Height;

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
	v_Light = max(dot(normal(int(i)), -u_LightVector), 0.5)+0.2;
	v_Tex = (normData-i)/6.0;
	vec4 matrix = u_LightMatrix * vec4(a_Position, 1.0);
	v_DepthMap = matrix.xy*0.5+0.5;
	v_Height = a_Position.y/256.0;
	gl_Position = u_CamMatrix * vec4(a_Position, 1.0);
}