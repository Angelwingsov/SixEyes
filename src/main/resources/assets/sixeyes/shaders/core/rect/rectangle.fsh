#version 330 core

#include<math_utils>
#include<scissor_check>

precision highp float;

in vec4 TopRightColor;
in vec4 TopLeftColor;
in vec4 BottomRightColor;
in vec4 BottomLeftColor;
in vec2 TexCoord;
in vec2 Size;
in vec4 Radius;
in float Mix;
in float Alpha;
in float Mode;
in float BorderWidth;
in vec4 BorderColor;
in float Type;
in vec4 Scissor;

in vec2 fragCoord;

uniform sampler2D uTexture;

layout(location = 0) out vec4 fragColor;

vec4 interpolateCornerColors(vec2 uv) {
    vec4 topColor = mix(TopLeftColor, TopRightColor, uv.x);
    vec4 bottomColor = mix(BottomLeftColor, BottomRightColor, uv.x);

    return mix(topColor, bottomColor, uv.y);
}

void main() {
    float scAlpha = applyScissor(Scissor, gl_FragCoord.xy);

    vec2 pos = fragCoord - Size * 0.5;
    float dist = sd_dist(Size, pos, Radius, Mode);
    float aa = max(fwidth(dist), 1e-4);

    if (dist > aa * 0.5 && BorderWidth <= 0.0) {
        discard;
    }

    int rectType = int(round(Type));
    vec2 normCoord = clamp(pos / Size + 0.5, 0.0, 1.0);
    vec4 gradientColor = interpolateCornerColors(normCoord);

    if (rectType == 0) {
        fragColor = sd_render(gradientColor, BorderColor, dist, BorderWidth);
    } else if (rectType == 1) {
        vec4 texColor = texture(uTexture, clamp(TexCoord, 0.0, 1.0));
        vec4 baseColor = mix(texColor, gradientColor, Mix);
        baseColor.a *= Alpha;
        fragColor = sd_render(baseColor, BorderColor, dist, BorderWidth);
    } else {
        fragColor = vec4(0.0);
    }

    fragColor.a *= scAlpha;

    if (fragColor.a <= 0.0) {
        discard;
    }
}
