#version 330 core

uniform sampler2D Sampler0;

in vec2 a_texCoord0;

uniform float closeAlpha;
uniform float theColorR;
uniform float theColorG;
uniform float theColorB;
uniform float time;
uniform int pass;

out vec4 outColor;

#define PI 3.1415926535897932384626433832795

const float D_INNER_START = 0.165;
const float D_INNER_FADE  = 0.1725;
const float D_RING_END    = 0.200;
const float D_SHADOW_END  = 0.205;
const float D_SPIN_END    = 0.2375;
const float D_OUTER_END   = 0.2025;

float rand(vec2 c) {
    return fract(sin(dot(c.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float noise(vec2 p, float freq) {
    float unit = 1.0 / freq;
    vec2 ij = floor(p / unit);
    vec2 xy = mod(p, unit) / unit;
    xy = .5 * (1. - cos(PI * xy));
    float a = rand((ij + vec2(0., 0.)));
    float b = rand((ij + vec2(1., 0.)));
    float c = rand((ij + vec2(0., 1.)));
    float d = rand((ij + vec2(1., 1.)));
    float x1 = mix(a, b, xy.x);
    float x2 = mix(c, d, xy.x);
    return mix(x1, x2, xy.y);
}

float pNoise(vec2 p, int res) {
    float persistance = .5;
    float n = 0.5;
    float normK = 0.4;
    float f = 4.0;
    float amp = 0.5;
    int iCount = 0;
    for(int i = 0; i < 50; i++) {
        n += amp * noise(p, f);
        f *= 2.;
        normK += amp;
        amp *= persistance;
        if(iCount == res)
            break;
        iCount++;
    }
    float nf = n / normK;
    return nf * nf * nf * nf;
}

float ripple(vec2 p) {
    vec2 i = p;
    float c = 2.0;
    float inten = .05;

    for(int n = 0; n < 3; n++) {
        float t = time * (1.0 - (0.060 / float(n + 1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0 / length(vec2(p.x / (sin(i.x + t) / inten), p.y / (cos(i.y + t) / inten)));
    }

    c /= 3.0;
    c = 1.5 - sqrt(c);
    return c;
}

vec3 rgbToHsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsvToRgb(float hue, float saturation, float value) {
    float h = floor(hue * 6.0);
    float f = hue * 6.0 - h;
    float p = value * (1.0 - saturation);
    float q = value * (1.0 - f * saturation);
    float t = value * (1.0 - (1.0 - f) * saturation);
    int hi = int(h);

    if(hi == 0) return vec3(value, t, p);
    if(hi == 1) return vec3(q, value, p);
    if(hi == 2) return vec3(p, value, t);
    if(hi == 3) return vec3(p, q, value);
    if(hi == 4) return vec3(t, p, value);
    if(hi == 5) return vec3(value, p, q);
    return vec3(1.0);
}

vec2 rotate(vec2 v, float angle)
{
    float s = sin(angle);
    float c = cos(angle);
    return vec2(
        c * v.x - s * v.y,
        s * v.x + c * v.y
    );
}

float cubicFade(float x)
{
    float t = x + 1.0;
    return (t*t*t - 1.0) / 7.0;
}

float animatedNoise(vec2 pos, float scale, float frame, float interp)
{
    float f = mod(frame, 10.0);

    return mix(
        pNoise(pos * scale + 10.0 * f, 4),
        pNoise(pos * scale + 10.0 * mod(f + 1.0, 10.0), 4),
        interp
    );
}

void main(void) {
    vec2 coord = a_texCoord0;

    float timeMulInner = 2.5;

    vec3 theColor = vec3(theColorR, theColorG, theColorB);

    vec2 sp = (coord - 0.5) / vec2(1.0, 1.0);

    float realTime = time;
    int tint = int(realTime);
    float tfl = fract(realTime);
    int tintf = int(realTime * timeMulInner);
    float tflf = fract(realTime * timeMulInner);

    vec2 p = rotate(sp, length(sp) * 5.0 - time * 0.015);
    vec2 p2 = rotate(sp, length(sp) * 25.0 - time * 0.15);

    float d = length(p);

    float rainbowTime = realTime * 0.5;
    float off = float(int(p.y * 75.0)) * PI * 0.005;

    vec4 color = vec4(0.7, 0.7, 0.7, 0.0);
    vec4 portalColor = vec4(theColor, 1.0);
    if(theColor.r < 0.0) {
        portalColor = vec4(hsvToRgb(mod(rainbowTime / 4.0 + p.y + off, 1.0), 1.0, 1.0), 1.0);
    }
    vec4 insideColor = vec4(0.0, 0.0, 0.15 * (1.0 - d * 2.0), 0.0);
    vec4 shadowColor = vec4(0.0, 0.0, 0.0, 0.15);

    float noise1 = 10.0;
    float noise2 = 25.0;

    // First pass
    if(pass == 0) {
        // Inside
        if(d < 0.2) {
            color = insideColor;
        }

        // Coloured inner edge
        float m1 = animatedNoise(p, noise1, float(tintf), tflf) * 0.5 + 0.25;
        float m2 = animatedNoise(p, noise2, float(tintf), tflf);
        float m = mix(m1, m2, 0.5);
        float n1 = animatedNoise(p, noise1 / 8.0, float(tint), tfl) * 0.5 + 0.25;
        float n2 = animatedNoise(p, noise2 / 8.0, float(tint), tfl);
        float n = mix(n1, n2, 0.5);
        if(closeAlpha > 0.0 && d < D_RING_END) {
            vec3 hsv = rgbToHsv(theColor);
            hsv.z *= (1.0 - coord.y * coord.y);
            hsv.z *= (1.0 - coord.y * coord.y);
            hsv.z *= 1.125;
            hsv.y += coord.y * coord.y * coord.y;
            float r = 0.0;
            const int amt = 10;
            for(int i = 1; i < amt; i++) {
                float fi = float(i * i * i);
                r += sin(ripple(coord * 41.5 * fi) * 10.0) * 0.5;
                r += sin(ripple(coord * 73.2 * fi) * 10.0) * 0.75;
            }
            hsv.z *= (n / (n * 0.15 + 0.85) + 0.375);
            hsv.z += ((r / 4.0) * (r / 4.0) * 0.0625 * 0.375);
            hsv.z *= 1.25;
            color = insideColor = vec4(hsvToRgb(hsv.x, hsv.y, hsv.z), closeAlpha);
        }
        if((d > D_INNER_START && d < D_RING_END)) {
            float intD = (d - D_INNER_START) / (D_RING_END - D_INNER_FADE);
            float alpha = cubicFade(intD);
            alpha *= 0.5 + m * (1.0 + (1.0 - intD) * 0.5);
            if(alpha > 0.0) {
                float n = max(1.0 - sp.y * 3.0 - 1.0, 0.0);
                color = mix(color, mix(mix(vec4(0.0, 0.0, 0.0, 1.0), portalColor, 0.85), portalColor, (-sp.y + 0.5)), alpha * (1.125 + 1.25 * n));
            }
        }

        // Shadow
        if(d > D_RING_END && d < D_SHADOW_END) {
            float intD = (d - D_RING_END) / (D_SHADOW_END - D_RING_END);
            float alpha = 1.0 - cubicFade(intD);
            color = mix(color, mix(color, shadowColor, shadowColor.a), alpha);
        }

        // Coloured outside ring
        m = animatedNoise(p, 10.0, float(tint), sin(tfl * PI / 2.0));
        if((d > D_RING_END && d < D_OUTER_END)) {
            float intD = 1.0 - (d - D_RING_END) / (D_OUTER_END - D_RING_END);
            float alpha = cubicFade(intD);
            alpha *= 1.0 + m * 2.0;
            if(alpha > 0.0) {
                float n = max(1.0 - sp.y * 5.0 - 1.0, 0.0);
                color = mix(color, portalColor * 1.1, alpha * (1.125 + 1.25 * n));
            }
        }
    } else {
        // Coloured outer spinny thing
        float m = animatedNoise(p2, 10.0, float(tint), sin(tfl * PI / 2.0));
        if((d > D_RING_END && d < D_SPIN_END)) {
            float intD = 1.0 - (d - D_RING_END) / (D_SPIN_END - D_RING_END);
            float alpha = cubicFade(intD);
            alpha *= 0.5 + m * 2.0;
            color = mix(color, mix(insideColor, portalColor, d * 5.0), alpha * 0.5);
        }
    }

    outColor = color;
}
