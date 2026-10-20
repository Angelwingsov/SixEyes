#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};

uniform sampler2D Sampler0;

layout(location = 0) in vec2 texCoord0;
layout(location = 1) in vec4 vertexColor;

layout(location = 0) out vec4 fragColor;

float median(vec3 rgb) {
    return max(min(rgb.r, rgb.g), min(max(rgb.r, rgb.g), rgb.b));
}

#ifndef MSDF_RANGE
#define MSDF_RANGE 10.0
#endif

void main() {
    vec4 s = texture(Sampler0, texCoord0);
    float dist = median(s.rgb) - 0.5;

    vec2 d = vec2(dFdx(texCoord0.x), dFdy(texCoord0.y)) * vec2(textureSize(Sampler0, 0));
    float pixels = float(MSDF_RANGE) * inversesqrt(max(dot(d, d), 1e-6));

    float alpha = smoothstep(-0.5, 0.5, dist * pixels);

    vec4 color = vec4(vertexColor.rgb, vertexColor.a * alpha) * ColorModulator;
    if (color.a <= 0.003) {
        discard;
    }
    fragColor = color;
}
