#ifdef GL_ES
	precision mediump float;
#endif

uniform sampler2D u_water;
uniform sampler2D u_dirt;
uniform sampler2D u_grassSide;
uniform sampler2D u_grassTop;
uniform float u_alpha;
varying vec2 v_diffuseUV;
varying float v_tex;


void main() {
	int i = int(v_tex);
	if(i==0)
		gl_FragColor.rgb = texture2D(u_water, v_diffuseUV).rgb;
	else if(i==1)
		gl_FragColor.rgb = texture2D(u_dirt, v_diffuseUV).rgb;
	else if(i==2)
		gl_FragColor.rgb = texture2D(u_grassSide, v_diffuseUV).rgb;
	else if(i==3)
        gl_FragColor.rgb = texture2D(u_grassTop, v_diffuseUV).rgb;
	gl_FragColor.a = u_alpha;
}