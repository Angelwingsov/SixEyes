#version 330 core

#include<shapes>

in vec4 Position;
in vec4 Color;
in vec2 aSize;
in vec4 aRadius;

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