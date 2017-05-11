attribute vec3 a_Position;
attribute float a_TexNormal;

uniform mat4 u_CamMatrix;
uniform mat4 u_LightMatrix;
uniform mat4 u_LightMatrix2;
uniform vec3 u_LightVector;
varying vec2 v_DiffuseUV;
varying vec2 v_DepthMap;
varying vec2 v_DepthMap2;
varying float v_Tex;
varying float v_Shadow;
varying float v_Light;
varying float v_Height;

vec3 normal(float i) {
	if(i<0.5) return vec3(1,0,0);
	else if(i<1.5) return vec3(0,0,1);
	else if(i<2.5) return vec3(0,0,-1);
	else if(i<3.5) return vec3(0,-1,0);
	else if(i<4.5) return vec3(0,1,0);
	else return vec3(-1,0,0);
}

vec2 texCoords(float i) {
	if(i<0.5) return vec2(0,0);			// Top Left
	else if (i<1.5) return vec2(1,0);	// Top Right
	else if (i<2.5) return vec2(0,1);	// Bottom Left
	else return vec2(1,1);				// Bottom Right
}

void main() {
	float i = mod(a_TexNormal,4.0);
	v_DiffuseUV = texCoords(i);
	float normData = (a_TexNormal-i)/4.0;
	i = mod(normData,6.0);
	if((i<0.5 && u_LightVector.x>0.0) || (i>4.5 && u_LightVector.x<0.0) || (i>1.5 && i<2.5))
	    v_Shadow = 2.0;
	else if ((i>0.5 && i < 1.5) || (i<0.5 && u_LightVector.x<0.0) || (i>4.5 && u_LightVector.x>0.0)) v_Shadow = 1.0;
	else v_Shadow = 0.0;
	v_DepthMap = (u_LightMatrix*vec4(a_Position, 1.0)).xy*0.5+0.5;
	v_DepthMap2 = (u_LightMatrix2*vec4(a_Position, 1.0)).xy*0.5+0.5;
	v_Height = a_Position.y/256.0;
	v_Light = max(dot(normal(i),-u_LightVector), 0.4)+0.2;
	v_Tex = (normData-i)/6.0;
	gl_Position = u_CamMatrix * vec4(a_Position, 1.0);
}
