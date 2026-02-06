#version 330 core

layout(location = 0) in vec4 Position;
layout(location = 1) in vec2 Texture;
layout(location = 2) in vec4 Color;

// x = Thickness, y = Smoothness, z = OutlineThickness
layout(location = 3) in vec3 aStyle;
layout(location = 4) in vec4 aOutlineColor;

layout(std140) uniform Projection {
    mat4 projMat;
};
uniform mat4 modelViewMat;

out vec4 vColor;
out vec2 vTexCoord;
out vec3 vStyle;
out vec4 vOutlineColor;

void main() {
    vColor = Color;
    vTexCoord = Texture;

    vStyle = aStyle;
    vOutlineColor = aOutlineColor;

    gl_Position = projMat * modelViewMat * Position;
}