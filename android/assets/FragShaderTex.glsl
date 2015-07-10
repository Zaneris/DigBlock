#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_Water;
uniform sampler2D u_Dirt;
uniform sampler2D u_GrassSide;
uniform sampler2D u_GrassTop;
uniform float u_Alpha;
varying vec2 v_DiffuseUV;
varying float v_Tex;
varying float v_Light;


void main() {
	int i = int(v_Tex);
	vec3 rgb;
	if(i==0)
		rgb = texture2D(u_Water, v_DiffuseUV).rgb;
	else if(i==1)
		rgb = texture2D(u_Dirt, v_DiffuseUV).rgb;
	else if(i==2)
		rgb = texture2D(u_GrassSide, v_DiffuseUV).rgb;
	else if(i==3)
        rgb = texture2D(u_GrassTop, v_DiffuseUV).rgb;
    rgb *= v_Light;
    gl_FragColor.rgb = rgb;
	gl_FragColor.a = u_Alpha;
}