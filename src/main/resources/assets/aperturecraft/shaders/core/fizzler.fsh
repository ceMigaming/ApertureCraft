// Based on https://www.shadertoy.com/view/sttSzf
#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform float time;
uniform vec4 ColorModulator;

// The field is an arbitrary distance between two emitters, so UVs arrive
// normalised to 0..1 along it. The noise needs world units instead, or cell
// size would scale with how far apart the pair happens to be: a 1 block gap
// would get 6 tiny cells and a 16 block gap 6 huge ones.
uniform float FieldLength;
uniform float FieldHeight;

out vec4 fragColor;

vec2 random(vec2 st) {
    float x = fract(sin(dot(st, vec2(3.0, 72.233))) * 43758.5453123);
    float y = fract(x * 77.0);
    return vec2(x, y);
}

float smoothNoise(vec2 uv) {
    vec2 repeatedUv = smoothstep(0.0, 1.0, fract(uv));
    vec2 tileCoords = floor(uv);

    float x1 = random(tileCoords).x;
    float x2 = random(tileCoords + vec2(1.0, 0.0)).x;

    float xValues = mix(x1, x2, repeatedUv.x);

    float y1 = random(tileCoords + vec2(0.0, 1.0)).x;
    float y2 = random(tileCoords + vec2(1.0, 1.0)).x;

    float yValues = mix(y1, y2, repeatedUv.x);

    return mix(xValues, yValues, repeatedUv.y);
}

float cellularNoise(vec2 uv, float size) {
    vec2 scaledUv = uv * size;
    vec2 repeatedUv = fract(scaledUv);
    vec2 uvCoords = floor(scaledUv);

    float dist = 1.0;

    for (int i = -1; i <= 1; i++) {
        for (int j = -1; j <= 1; j++) {
            vec2 neighborTile = vec2(float(i), float(j));

            vec2 point = random(uvCoords + neighborTile);
            point += sin(time * 1.5 * point) * 0.3;

            float currentDistance = distance(
                point + neighborTile,
                repeatedUv
            );

            dist = min(dist, currentDistance);
        }
    }

    return dist;
}

float border(vec2 uv) {
    float col = 0.02 / max(uv.x, 0.0001);

    col += 0.02 / max(uv.y, 0.0001);
    col = smoothstep(0.1, 1.0, col);

    return col * 0.4;
}

void main() {
    vec2 uv = texCoord0;
    vec2 worldUv = vec2(uv.x * FieldLength, uv.y * FieldHeight);

    float noise = smoothNoise(worldUv * 9.0) * 0.05;
    worldUv += noise;

    vec2 movingUv = worldUv;
    movingUv.y += time * 0.07;

    float cells1 = cellularNoise(movingUv, 6.0);
    cells1 = pow(cells1, 6.0) * 0.5;

    float cells2 = cellularNoise(movingUv, 12.0);
    cells2 = pow(cells2, 5.0) * 0.1;

    float cells3 = cellularNoise(movingUv, 12.0);
    cells3 = pow(cells3, 6.0) * 0.25;

    float cells4 = cellularNoise(movingUv, 24.0);
    cells4 = pow(cells4, 5.0) * 0.05;

    float cells = cells1 + cells2;
    float cells_1 = cells3 + cells4;

    float borders = border(1.05 - uv) + border(uv);

    vec3 blue = vec3(0.259, 0.729, 1.0);

    vec3 color = blue * (borders + cells + cells_1);
    color += blue * 0.1;

    color *= vertexColor.rgb * ColorModulator.rgb;

    float alpha = vertexColor.a * ColorModulator.a;

    fragColor = vec4(color, alpha);
}