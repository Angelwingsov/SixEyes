#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV1;
layout(location = 4) in ivec2 UV2;
layout(location = 5) in vec2 UV3;
layout(location = 6) in float LineWidth;

layout(location = 0) out vec2 fragCoord;
layout(location = 1) out vec4 vertexColor;
layout(location = 2) out vec2 rectSize;
layout(location = 3) out vec4 rectRadius;
layout(location = 4) out vec4 borderColor;
layout(location = 5) out float borderWidth;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position.xy, 0.0, 1.0);

    fragCoord = UV0;
    vertexColor = Color;
    rectSize = UV3;
    rectRadius = vec4(vec2(UV1), vec2(UV2)) / 64.0;

    int rgbBits = int(Position.z + 0.5);
    int bwaBits = int(LineWidth + 0.5);
    borderColor = vec4(
        float((rgbBits >> 16) & 255) / 255.0,
        float((rgbBits >> 8) & 255) / 255.0,
        float(rgbBits & 255) / 255.0,
        float(bwaBits & 255) / 255.0
    );
    borderWidth = float(bwaBits >> 8) / 64.0;
}
