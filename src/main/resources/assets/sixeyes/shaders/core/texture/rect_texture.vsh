#version 330 core

#include<shapes>

in vec4 Position;
in vec4 Color;
in vec2 Texture;
in vec2 aSize;
in vec4 aRadius;
in float aMix;

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