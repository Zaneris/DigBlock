#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_DepthMap;
uniform sampler2D u_DepthMap2;
uniform sampler2D u_Water;
uniform sampler2D u_Dirt;
uniform sampler2D u_GrassSide;
uniform sampler2D u_GrassTop;
uniform float u_Alpha;
varying vec2 v_DiffuseUV;
varying vec2 v_DepthMap;
varying vec2 v_DepthMap2;
varying float v_Tex;
varying float v_Light;
varying float v_Height;
varying float v_Shadow;

const float scl = 16.0;

vec3 rgb (float i) {
	if(i<0.5) return texture2D(u_Water, v_DiffuseUV).rgb;
	else if(i<1.5) return texture2D(u_Dirt, v_DiffuseUV).rgb;
	else if(i<2.5) return texture2D(u_GrassSide, v_DiffuseUV).rgb;
	else return texture2D(u_GrassTop, v_DiffuseUV).rgb;
}

float depthChk(vec2 xy, sampler2D depthMap) {
	float bias = 0.0005;
    if(v_Shadow > 0.5) bias = 0.0008;
	vec4 rgba = texture2D(depthMap, xy);
	rgba /= 16.0;
	float height = rgba.a;
	height += rgba.b * scl;
	height += rgba.g * scl * scl;
	height += rgba.r * scl * scl * scl;
	height /= 256.0;
	if(v_Height+bias<height)
		return 0.0;
	else return 0.4;
}

float avgDepth() {
    if(v_Shadow > 1.5)
        return 0.6;
	float bias = 0.0005;
	float toReturn;
	if(v_DepthMap.x > 0.0 && v_DepthMap.x < 1.0 && v_DepthMap.y > 0.0 && v_DepthMap.y <1.0) {
        toReturn = depthChk(v_DepthMap, u_DepthMap);
        toReturn += depthChk(vec2(v_DepthMap.x+bias,v_DepthMap.y), u_DepthMap);
        toReturn += depthChk(vec2(v_DepthMap.x-bias,v_DepthMap.y), u_DepthMap);
        toReturn += depthChk(vec2(v_DepthMap.x,v_DepthMap.y+bias), u_DepthMap);
        toReturn += depthChk(vec2(v_DepthMap.x,v_DepthMap.y-bias), u_DepthMap);
        toReturn /= 5.0;
	} else {
	    toReturn = depthChk(v_DepthMap2, u_DepthMap2);
	}
	return 0.6 + toReturn;
}

void main() {
	gl_FragColor.rgb = v_Light*avgDepth()*rgb(v_Tex);
	gl_FragColor.a = u_Alpha;
}