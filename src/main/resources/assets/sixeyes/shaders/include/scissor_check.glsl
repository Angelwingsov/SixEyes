float applyScissor(vec4 scissor, vec2 fragCoord) {
    if (scissor.x < -90000.0) return 1.0;

    vec2 center = 0.5 * (scissor.xy + scissor.zw);
    vec2 halfSize = 0.5 * (scissor.zw - scissor.xy);
    vec2 d = abs(fragCoord - center) - halfSize;
    float sd = length(max(d, vec2(0.0))) + min(max(d.x, d.y), 0.0);

    if (sd >= 0.5) {
        discard;
    }

    return smoothstep(0.5, -0.5, sd);
}
