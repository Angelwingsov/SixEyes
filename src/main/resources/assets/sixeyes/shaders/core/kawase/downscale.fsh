#version 330 core

precision lowp float;

uniform sampler2D uTexture;
uniform vec2 uHalfTexelSize;
uniform float uOffset;

out vec4 fragColor;

void main() {
    vec2 texCoord = gl_FragCoord.xy * uHalfTexelSize;

    fragColor = (
        texture(uTexture, texCoord) * 4 +
        texture(uTexture, texCoord - uHalfTexelSize.xy * uOffset) +
        texture(uTexture, texCoord + uHalfTexelSize.xy * uOffset) +
        texture(uTexture, texCoord + vec2(uHalfTexelSize.x, -uHalfTexelSize.y) * uOffset) +
        texture(uTexture, texCoord - vec2(uHalfTexelSize.x, -uHalfTexelSize.y) * uOffset)
    ) / 8;

    fragColor.a = 1;
}