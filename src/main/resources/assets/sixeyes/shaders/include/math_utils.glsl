float selectRadius(vec2 pos, vec4 radius) {
    if (pos.x < 0.0) {
        return (pos.y < 0.0) ? radius.x : radius.w;
    } else {
        return (pos.y < 0.0) ? radius.y : radius.z;
    }
}

float sd_dist(vec2 size, vec2 uv, vec4 radius, float mode) {
    vec2 halfSize = max(size * 0.5 - vec2(1.0), vec2(0.0));
    vec2 pos = (uv * size) - (size * 0.5);
    float r = selectRadius(pos, radius);
    r = min(max(r - 1.0, 0.0), min(halfSize.x, halfSize.y));

    vec2 q = abs(pos) - (halfSize - vec2(r));
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

vec4 sd_render(vec4 fillColor, vec4 borderColor, float dist, float borderWidth) {
    float aa = 1.0;
    float fillAlpha = 1.0 - smoothstep(0.0, aa, dist);

    if (borderWidth <= 0.0) {
        return vec4(fillColor.rgb, fillColor.a * fillAlpha);
    }

    float borderEdge = abs(dist) - borderWidth;
    float borderAlpha = 1.0 - smoothstep(0.0, aa, borderEdge);
    vec4 color = mix(fillColor, borderColor, borderAlpha);
    color.a *= fillAlpha;
    return color;
}
