#version 330 core

#include<math_utils>

in vec4 Position;
in vec4 TopRightColor0;
in vec4 TopLeftColor0;
in vec4 BottomRightColor0;
in vec4 BottomLeftColor0;
in vec2 LocalCoord0;
in vec2 UV0;
in vec2 Size0;
in vec4 Radius0;
in float Mix0;
in float Alpha0;
in float Mode0;
in float BorderWidth0;
in vec4 BorderColor0;
in float Type0;
in vec4 Scissor0;

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