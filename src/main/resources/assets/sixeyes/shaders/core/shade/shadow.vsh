#version 330 core

#include<shapes>

layout(location = 0) in vec4 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 aSize;
layout(location = 3) in vec4 aRadius;
layout(location = 4) in float aShadowSize;
layout(location = 5) in vec2 aSettings;

layout(std140) uniform Projection {
    mat4 projMat;
};
uniform mat4 modelViewMat;

out vec4 vertexColor;
out vec2 Size;
out vec4 Radius;
out float ShadowSize;
out vec2 Settings;
out vec2 fragCoord;

void main() {
    gl_Position = projMat * modelViewMat * Position;
    vertexColor = Color;
    Size = aSize;
    Radius = aRadius;
    ShadowSize = aShadowSize;
    Settings = aSettings;
    fragCoord = rvertexcoord(gl_VertexID) * Size;
}