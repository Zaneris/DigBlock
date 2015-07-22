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

float depthChk(vec2 xy) {
	float bias = 0.0021;
	vec4 rgba = texture2D(u_DepthMap, xy);
	if(v_Height+bias<rgba.r)
		return 0.0;
	else return 0.4;
}

float avgDepth() {
	float bias = 0.0001;
	float bias2= 0.00014;
	float toReturn = depthChk(v_DepthMap);
	toReturn += depthChk(vec2(v_DepthMap.x+bias,v_DepthMap.y));
	toReturn += depthChk(vec2(v_DepthMap.x-bias,v_DepthMap.y));
	toReturn += depthChk(vec2(v_DepthMap.x,v_DepthMap.y+bias));
	toReturn += depthChk(vec2(v_DepthMap.x,v_DepthMap.y-bias));
	toReturn += depthChk(vec2(v_DepthMap.x+bias2,v_DepthMap.y+bias2));
	toReturn += depthChk(vec2(v_DepthMap.x+bias2,v_DepthMap.y-bias2));
	toReturn += depthChk(vec2(v_DepthMap.x-bias2,v_DepthMap.y+bias2));
	toReturn += depthChk(vec2(v_DepthMap.x-bias2,v_DepthMap.y-bias2));
	toReturn /= 9.0;
	return 0.6 + toReturn;
}

void main() {
	gl_FragColor.rgb = v_Light*avgDepth()*rgb(int(v_Tex));
	gl_FragColor.a = u_Alpha;
}