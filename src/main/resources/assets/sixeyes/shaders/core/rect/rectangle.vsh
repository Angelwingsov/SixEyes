#version 330 core

#include<math_utils>

layout(location = 0) in vec4 Position;
layout(location = 1) in vec4 TopRightColor0;
layout(location = 2) in vec4 TopLeftColor0;
layout(location = 3) in vec4 BottomRightColor0;
layout(location = 4) in vec4 BottomLeftColor0;
layout(location = 5) in vec2 LocalCoord0;
layout(location = 6) in vec2 UV0;
layout(location = 7) in vec2 Size0;
layout(location = 8) in vec4 Radius0;
layout(location = 9) in float Mix0;
layout(location = 10) in float Alpha0;
layout(location = 11) in float Mode0;
layout(location = 12) in float BorderWidth0;
layout(location = 13) in vec4 BorderColor0;
layout(location = 14) in float Type0;
layout(location = 15) in vec4 Scissor0;

#include<matrices>

out vec4 TopRightColor;
out vec4 TopLeftColor;
out vec4 BottomRightColor;
out vec4 BottomLeftColor;
out vec2 TexCoord;
out vec2 Size;
out vec4 Radius;
out float Mix;
out float Alpha;
out float Mode;
out float BorderWidth;
out vec4 BorderColor;
out float Type;
out vec4 Scissor;

out vec2 fragCoord;

void main() {
    TopRightColor = TopRightColor0;
    TopLeftColor = TopLeftColor0;
    BottomRightColor = BottomRightColor0;
    BottomLeftColor = BottomLeftColor0;

    TexCoord = UV0;

    Size = Size0;
    Radius = Radius0;

    Mix = Mix0;
    Alpha = Alpha0;

    Mode = Mode0;
    Type = Type0;

    BorderWidth = BorderWidth0;
    BorderColor = BorderColor0;

    Scissor = Scissor0;

    fragCoord = LocalCoord0;
    gl_Position = projMat * ModelViewMat * Position;
}
