#version 330 core

layout(location = 0) in vec4 pos;

layout(std140) uniform Projection {
    mat4 projMat;
};

uniform mat4 modelViewMat;

void main() {
    gl_Position = projMat * modelViewMat * pos;
}