#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};

layout(location = 0) in vec2 fragCoord;
layout(location = 1) in vec4 vertexColor;
layout(location = 2) in vec2 rectSize;
layout(location = 3) in vec4 rectRadius;
layout(location = 4) in vec4 borderColor;
layout(location = 5) in float borderWidth;

layout(location = 0) out vec4 fragColor;

float selectRadius(vec2 pos, vec4 radius) {
    if (pos.x < 0.0) {
        return (pos.y < 0.0) ? radius.x : radius.w;
    } else {
        return (pos.y < 0.0) ? radius.y : radius.z;
    }
}

float sdRoundedBox(vec2 size, vec2 pos, vec4 radius) {
    vec2 halfSize = size * 0.5;
    float r = selectRadius(pos, radius);
    r = min(max(r, 0.0), min(halfSize.x, halfSize.y));

    vec2 q = abs(pos) - (halfSize - vec2(r));
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

void main() {
    vec2 pos = fragCoord - rectSize * 0.5;
    float dist = sdRoundedBox(rectSize, pos, rectRadius);
    float aa = max(0.5 * (fwidth(fragCoord.x) + fwidth(fragCoord.y)), 1e-4);

    float fillAlpha = clamp(0.5 - dist / aa, 0.0, 1.0);
    if (fillAlpha <= 0.001) {
        discard;
    }

    vec4 color;
    if (borderWidth <= 0.001) {
        color = vec4(vertexColor.rgb, vertexColor.a * fillAlpha);
    } else {
        float borderEdge = max(dist, -dist - borderWidth);
        float borderAlpha = clamp(0.5 - borderEdge / aa, 0.0, 1.0);
        color = mix(vertexColor, borderColor, borderAlpha);
        color.a *= fillAlpha;
    }

    color *= ColorModulator;
    if (color.a <= 0.001) {
        discard;
    }
    fragColor = color;
}
