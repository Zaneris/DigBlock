#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_DepthMap;
uniform sampler2D u_Water;
uniform sampler2D u_Dirt;
uniform sampler2D u_GrassSide;
uniform sampler2D u_GrassTop;
uniform float u_WorldSize;
uniform float u_Alpha;
varying vec2 v_DiffuseUV;
varying vec2 v_DepthMap;
varying float v_Tex;
varying float v_Light;
varying float v_DistToLight;

vec3 rgb (int i) {
	if(i==0) return texture2D(u_Water, v_DiffuseUV).rgb;
	else if(i==1) return texture2D(u_Dirt, v_DiffuseUV).rgb;
	else if(i==2) return texture2D(u_GrassSide, v_DiffuseUV).rgb;
	else return texture2D(u_GrassTop, v_DiffuseUV).rgb;
}

void main() {
	vec3 final = rgb(int(v_Tex))*v_Light;
	if(v_Light>=0.6) {
		vec4 rgba = texture2D(u_DepthMap, v_DepthMap);
		float fromMap = rgba.r + rgba.g + rgba.b + rgba.a;
		if(v_DistToLight>(fromMap+0.0025))
			final *= 0.5;
	}
	gl_FragColor.rgb = final;
	gl_FragColor.a = u_Alpha;
}