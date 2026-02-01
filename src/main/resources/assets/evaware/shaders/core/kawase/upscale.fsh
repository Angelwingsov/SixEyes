#version 330 core

precision lowp float;

uniform sampler2D uTexture;
uniform vec2 uHalfTexelSize;
uniform float uOffset;

out vec4 fragColor;

void main() {
    vec2 texCoord = gl_FragCoord.xy * uHalfTexelSize;

    fragColor = (
        texture(uTexture, texCoord + vec2(-uHalfTexelSize.x * 2, 0) * uOffset) +
        texture(uTexture, texCoord + vec2(-uHalfTexelSize.x, uHalfTexelSize.y) * uOffset) * 2 +
        texture(uTexture, texCoord + vec2(0, uHalfTexelSize.y * 2) * uOffset) +
        texture(uTexture, texCoord + uHalfTexelSize * uOffset) * 2 +
        texture(uTexture, texCoord + vec2(uHalfTexelSize.x * 2, 0) * uOffset) +
        texture(uTexture, texCoord + vec2(uHalfTexelSize.x, -uHalfTexelSize.y) * uOffset) * 2 +
        texture(uTexture, texCoord + vec2(0, -uHalfTexelSize.y * 2) * uOffset) +
        texture(uTexture, texCoord - uHalfTexelSize * uOffset) * 2
    ) / 12;

    fragColor.a = 1;
}