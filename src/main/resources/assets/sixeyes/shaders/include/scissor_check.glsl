// Ну это же просто секс!
void applyScissor(vec4 scissor, vec2 fragCoord) {
    if (scissor.x < -90000.0) return;

    if (fragCoord.x < scissor.x || fragCoord.y < scissor.y ||
        fragCoord.x > scissor.z || fragCoord.y > scissor.w) {
        discard;
    }
}