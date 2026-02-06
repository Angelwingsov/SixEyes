#version 330 core

#include<shapes>

layout(location = 0) in vec4 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 aSize;
layout(location = 3) in vec4 aRadius;

layout(std140) uniform Projection {
    mat4 projMat;
};
uniform mat4 modelViewMat;

out vec4 vertexColor;
out vec2 Size;
out vec4 Radius;
out vec2 fragCoord;

void main() {
    gl_Position = projMat * modelViewMat * Position;
    vertexColor = Color;
    Size = aSize;
    Radius = aRadius;
    fragCoord = rvertexcoord(gl_VertexID);
}