#ifdef GL_ES
	precision mediump float;
#endif

varying float v_Height;
const float scl = 16.0;

void main() {
    vec4 rgba;
    float height = floor(v_Height*256.0);

    float rem = floor(height/(scl*scl*scl));
    rgba.r = rem/scl;
    height -= rem*scl*scl*scl;

    rem = floor(height/(scl*scl));
    rgba.g = rem/scl;
    height -= rem*scl*scl;

    rem = floor(height/scl);
    rgba.b = rem/scl;
    height -= rem*scl;

    rgba.a = height;

    gl_FragColor = rgba;
}