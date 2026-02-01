#version 330 core

#include<math_utils>

precision lowp float;

in vec4 vertexColor;
in vec2 TexCoord;
in vec2 Size;
in vec4 Radius;
in float Mix;
in vec2 fragCoord;

uniform sampler2D uTexture;

out vec4 fragColor;

void main() {
    float alpha = ralpha(Size, fragCoord, Radius, 1.0);

    vec4 texColor = texture(uTexture, TexCoord);
    vec4 mixedColor = mix(texColor, vertexColor, Mix);

    fragColor = mixedColor;
    fragColor *= alpha;

    if (fragColor.a == 0.0) {
        discard;
    }
}