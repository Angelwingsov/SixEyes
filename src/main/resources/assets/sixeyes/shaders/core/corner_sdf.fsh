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

void main() {
    vec4 texSample = texture(Sampler0, texCoord0);
    vec2 p = texCoord0 - vec2(1.0);
    float d = length(p);
    float sd = d - 1.0;
    float aa = max(0.5 * (fwidth(texCoord0.x) + fwidth(texCoord0.y)), 1e-4);
    float alpha = clamp(0.5 - sd / aa, 0.0, 1.0);

    vec4 col = texSample * vertexColor * ColorModulator;
    col.a *= alpha;
    if (col.a <= 0.001) {
        discard;
    }
    fragColor = col;
}
