#version 330 core

#include<scissor_check>

precision lowp float;

in vec4 FragColor;
in vec2 TexCoord;
in vec3 Style;
in vec4 OutlineColor;
in vec4 FadeParams;
in vec2 FragPos;
in vec4 Scissor;

uniform float uRange;
uniform sampler2D uTexture;

out vec4 fragColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    applyScissor(Scissor, gl_FragCoord.xy);

    float thickness = Style.x;
    float smoothness = Style.y;
    float outlineThickness = Style.z;
    bool hasOutline = outlineThickness > 0.0;

    float dist = median(texture(uTexture, TexCoord).rgb) - 0.5 + thickness;
    vec2 d = vec2(dFdx(TexCoord.x), dFdy(TexCoord.y)) * textureSize(uTexture, 0);
    float pixels = uRange * inversesqrt(d.x * d.x + d.y * d.y);
    float alpha = smoothstep(-smoothness, smoothness, dist * pixels);

    float minX = FadeParams.x;
    float maxX = FadeParams.y;
    float leftW = FadeParams.z;
    float rightW = FadeParams.w;

    float fadeAlpha = 1.0;

    if (leftW > 0.0) {
        fadeAlpha *= smoothstep(minX, minX + leftW, FragPos.x);
    } else if (FragPos.x < minX) {
        fadeAlpha = 0.0;
    }

    if (rightW > 0.0) {
        fadeAlpha *= (1.0 - smoothstep(maxX - rightW, maxX, FragPos.x));
    } else if (FragPos.x > maxX) {
        fadeAlpha = 0.0;
    }

    vec4 color = vec4(FragColor.rgb, FragColor.a * alpha * fadeAlpha);

    if (hasOutline) {
        float outlineAlpha = smoothstep(-smoothness, smoothness, (dist + outlineThickness) * pixels);

        vec4 outCol = OutlineColor;
        outCol.a *= fadeAlpha;
        vec4 finalRes = mix(outCol, vec4(FragColor.rgb, FragColor.a * fadeAlpha), alpha);
        finalRes.a *= outlineAlpha;
        fragColor = finalRes;

    } else {
        fragColor = color;
    }

    if (fragColor.a <= 0.0) {
        discard;
    }
}