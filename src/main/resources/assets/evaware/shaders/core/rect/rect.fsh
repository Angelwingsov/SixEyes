#version 330 core

#include<math_utils>

precision lowp float;

in vec4 vertexColor;
in vec2 Size;
in vec4 Radius;
in vec2 fragCoord;

out vec4 fragColor;

void main() {
    float alpha = ralpha(Size, fragCoord, Radius, 1.0);

    fragColor = vertexColor;
    fragColor *= alpha;

    if (fragColor.a == 0.0) {
        discard;
    }
}