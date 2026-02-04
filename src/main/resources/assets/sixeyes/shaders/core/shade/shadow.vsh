#version 330 core

#include<shapes>

in vec4 Position;
in vec4 Color;
in vec2 aSize;
in vec4 aRadius;
in float aShadowSize;
in vec2 aSettings; // x = Fill (0/1), y = Inner (0/1)

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