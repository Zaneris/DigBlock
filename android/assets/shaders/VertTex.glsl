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
	vec3 depthPos = a_Position;
	if((i<0.5 && u_LightVector.x<0.0) 
			|| (i>4.5 && u_LightVector.x>0.0)
			|| (i>0.5 && i<2.5))
		v_Height = (a_Position.y+v_DiffuseUV.y)/256.0;
	else {
		if(i<0.5 || i>4.5) {
			v_Height = (a_Position.y-1.0+v_DiffuseUV.y)/256.0;
			depthPos.y = depthPos.y-0.5+v_DiffuseUV.y;
		} else if(i>0.5 && i<2.5) {
			if(i<1.5) depthPos.z += 0.5;
			else depthPos.z -= 0.5;
		} else {
			float offset = mod(a_Position.y,1.0);
			if(offset>0.5)
				v_Height = (a_Position.y + 1.0 - offset)/256.0;
			else
				v_Height = a_Position.y/256.0;
		}
	}
	v_DepthMap = (u_LightMatrix*vec4(depthPos, 1.0)).xy*0.5+0.5;
	v_Light = max(dot(normal(i),-u_LightVector), 0.4)+0.2;
	v_Tex = (normData-i)/6.0;
	gl_Position = u_CamMatrix * vec4(a_Position, 1.0);
}
