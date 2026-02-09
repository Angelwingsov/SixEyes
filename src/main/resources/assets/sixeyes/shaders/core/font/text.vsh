#version 330 core

in vec4 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Style0;
in vec4 OutlineColor0;
in vec4 Fade0;
in vec4 Scissor0;

#include<matrices>

out vec4 FragColor;
out vec2 TexCoord;
out vec3 Style;
out vec4 OutlineColor;
out vec4 FadeParams;
out vec2 FragPos;
out vec4 Scissor;

void main() {
    FragColor = Color;
    TexCoord = UV0;
    Style = Style0;
    OutlineColor = OutlineColor0;

    FadeParams = Fade0;
    FragPos = Position.xy;

    Scissor = Scissor0;

    gl_Position = projMat * ModelViewMat * Position;
}