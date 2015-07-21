#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_DepthMap;
uniform sampler2D u_Water;
uniform sampler2D u_Dirt;
uniform sampler2D u_GrassSide;
uniform sampler2D u_GrassTop;
uniform float u_Alpha;
varying vec2 v_DiffuseUV;
varying vec2 v_DepthMap;
varying float v_Tex;
varying float v_Light;
varying float v_Height;

vec3 rgb (int i) {
	if(i==0) return texture2D(u_Water, v_DiffuseUV).rgb;
	else if(i==1) return texture2D(u_Dirt, v_DiffuseUV).rgb;
	else if(i==2) return texture2D(u_GrassSide, v_DiffuseUV).rgb;
	else return texture2D(u_GrassTop, v_DiffuseUV).rgb;
}

bool depthChk() {
	float bias = 0.002;
	vec4 rgba = texture2D(u_DepthMap, v_DepthMap);
	if(v_Height+bias<rgba.r)
		return true;
	else return false;
}

void main() {
	vec3 final;
	float avgLight;
	if(v_Light>=0.6 && depthChk()) {
		avgLight = v_Light*0.5;
	} else {
		avgLight = v_Light;
	}
	gl_FragColor.rgb = rgb(int(v_Tex))*avgLight;
	gl_FragColor.a = u_Alpha;
}