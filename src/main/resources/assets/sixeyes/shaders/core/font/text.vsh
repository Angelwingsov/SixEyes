#version 330 core

layout(location = 0) in vec4 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;
layout(location = 3) in vec3 Style0;
layout(location = 4) in vec4 OutlineColor0;
layout(location = 5) in vec4 Fade0;
layout(location = 6) in vec4 Scissor0;

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
