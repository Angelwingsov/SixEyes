#version 330 core

in vec4 Position;
in vec2 Texture;
in vec4 Color;

// x = Thickness, y = Smoothness, z = OutlineThickness
in vec3 aStyle;
in vec4 aOutlineColor;

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