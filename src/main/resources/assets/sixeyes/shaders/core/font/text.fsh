#version 330 core

#include<scissor_check>

precision highp float;

in vec4 FragColor;
in vec2 TexCoord;
in vec3 Style;
in vec4 OutlineColor;
in vec4 FadeParams;
in vec2 FragPos;
in vec4 Scissor;

uniform float uRange;
uniform sampler2D uTexture;

layout(location = 0) out vec4 fragColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    float scAlpha = applyScissor(Scissor, gl_FragCoord.xy);

    float thickness = Style.x;
    float outlineThickness = Style.z;
    bool hasOutline = outlineThickness > 0.0;

    float dist = median(texture(uTexture, TexCoord).rgb) - 0.5 + thickness;
    vec2 unitRange = vec2(uRange) / vec2(textureSize(uTexture, 0));
    vec2 dU = vec2(dFdx(TexCoord.x), dFdy(TexCoord.x));
    vec2 dV = vec2(dFdx(TexCoord.y), dFdy(TexCoord.y));
    vec2 screenTexSize = vec2(1.0) / max(vec2(length(dU), length(dV)), vec2(1e-6));
    float screenPxRange = max(0.5 * dot(unitRange, screenTexSize), 1.0);

    float pxDist = dist * screenPxRange;
    float alpha = clamp(pxDist + 0.5, 0.0, 1.0);

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
        float outlineAlpha = clamp((dist + outlineThickness) * screenPxRange + 0.5, 0.0, 1.0);

        vec4 outCol = OutlineColor;
        outCol.a *= fadeAlpha;
        vec4 finalRes = mix(outCol, vec4(FragColor.rgb, FragColor.a * fadeAlpha), alpha);
        finalRes.a *= outlineAlpha;
        fragColor = finalRes;

    } else {
        fragColor = color;
    }

    fragColor.a *= scAlpha;

    if (fragColor.a <= 0.0) {
        discard;
    }
}
