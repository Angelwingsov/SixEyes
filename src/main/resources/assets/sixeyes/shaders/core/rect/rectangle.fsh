#version 330 core

#include<math_utils>
#include<scissor_check>

precision lowp float;

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

out vec4 fragColor;

vec4 SuperKuniOtDonichki(vec2 uv) {
    vec4 topColor = mix(TopLeftColor, TopRightColor, uv.x);
    vec4 bottomColor = mix(BottomLeftColor, BottomRightColor, uv.x);

    return mix(topColor, bottomColor, uv.y);
}

void main() {
    applyScissor(Scissor, gl_FragCoord.xy);

    int sex = int(round(Type));
    vec4 gradientColor = SuperKuniOtDonichki(fragCoord);
    float dist = sd_dist(Size, fragCoord, Radius, Mode);

    if (sex == 0) { // RECT (GRADIENT & BASIC)
        fragColor = sd_render(gradientColor, BorderColor, dist, BorderWidth);
    } else if (sex == 1) { // TEXTURE & BLUR
        vec4 texColor = texture(uTexture, TexCoord);
        vec4 baseColor = mix(texColor, gradientColor, Mix);
        baseColor.a *= Alpha;
        fragColor = sd_render(baseColor, BorderColor, dist, BorderWidth);
    } else {
        fragColor = vec4(0, 0, 0, 0);
    }

    if (fragColor.a == 0.0) {
        discard;
    }
}