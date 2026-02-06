#version 330 core

#include<shapes>

layout(location = 0) in vec4 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 Texture;
layout(location = 3) in vec2 aSize;
layout(location = 4) in vec4 aRadius;
layout(location = 5) in float aMix;

layout(std140) uniform Projection {
    mat4 projMat;
};
uniform mat4 modelViewMat;

out vec4 vertexColor;
out vec2 TexCoord;
out vec2 Size;
out vec4 Radius;
out float Mix;

out vec2 fragCoord;

void main() {
    vertexColor = Color;
    TexCoord = Texture;
    Size = aSize;
    Radius = aRadius;
    Mix = aMix;

    fragCoord = rvertexcoord(gl_VertexID);

    gl_Position = projMat * modelViewMat * Position;
}