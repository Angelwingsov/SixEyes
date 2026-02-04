#version 330 core

precision lowp float;

in vec4 vColor;
in vec2 vTexCoord;
in vec3 vStyle;
in vec4 vOutlineColor;

uniform float uRange;
uniform sampler2D uTexture;

out vec4 fragColor;

float median(vec3 color) {
    return max(min(color.r, color.g), min(max(color.r, color.g), color.b));
}

void main() {
    float thickness = vStyle.x;
    float smoothness = vStyle.y;
    float outlineThickness = vStyle.z;
    bool hasOutline = outlineThickness > 0.0;

    vec3 sample = texture(uTexture, vTexCoord).rgb;
    float dist = median(sample) - 0.5 + thickness;

    vec2 d = fwidth(vTexCoord) * textureSize(uTexture, 0);
    float pixels = uRange * inversesqrt(d.x * d.x + d.y * d.y);

    float alpha = smoothstep(-smoothness, smoothness, dist * pixels);

    vec4 al = vColor;
    al *= alpha;

    if (hasOutline) {
        float outlineAlpha = smoothstep(-smoothness, smoothness, (dist + outlineThickness) * pixels);
        vec4 mixed = mix(vOutlineColor, al, alpha);
        mixed.a *= outlineAlpha;
        fragColor = mixed;
    } else {
        fragColor = al;
    }

    if (fragColor.a <= 0.0) {
        discard;
    }
}