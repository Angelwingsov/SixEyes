#version 330 core

#include<math_utils>

precision lowp float;

in vec4 vertexColor;
in vec2 Size;
in vec4 Radius;
in float ShadowSize;
in vec2 Settings; // .x = Fill, .y = Inner
in vec2 fragCoord;

out vec4 fragColor;

float sdRoundedBox(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x  = (p.y > 0.0) ? r.x  : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    float doFill = Settings.x;
    float doInner = Settings.y;

    vec2 center = Size * 0.5;
    vec2 p = fragCoord - center;

    vec2 boxSize = center;

    if (doInner < 0.5) {
        boxSize -= vec2(ShadowSize);
        boxSize = max(boxSize, vec2(0.0));
    }

    float dist = sdRoundedBox(p, boxSize, Radius);

    float fillAlpha = 0.0;
    if (doFill > 0.5) {
        fillAlpha = 1.0 - smoothstep(-0.5, 0.5, dist);
    }

    float shadowAlpha = 0.0;

    if (doInner > 0.5) {
        float rawShadow = smoothstep(-ShadowSize, 0.0, dist);
        float borderMask = 1.0 - smoothstep(0.0, 0.5, dist);

        shadowAlpha = rawShadow * borderMask;
    } else {
        float rawShadow = 1.0 - smoothstep(0.0, ShadowSize, dist);
        float hollowMask = smoothstep(-0.5, 0.0, dist);

        shadowAlpha = rawShadow * hollowMask;
    }

    float finalAlpha = max(fillAlpha, shadowAlpha);

    vec4 color = vertexColor;
    color.a *= finalAlpha;

    fragColor = color;

    if (fragColor.a <= 0.001) {
        discard;
    }
}