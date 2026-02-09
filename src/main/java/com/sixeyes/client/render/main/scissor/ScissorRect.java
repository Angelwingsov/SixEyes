package com.sixeyes.client.render.main.scissor;

public record ScissorRect(float x, float y, float width, float height) {
    public ScissorRect intersection(ScissorRect other) {
        float x1 = Math.max(this.x, other.x);
        float y1 = Math.max(this.y, other.y);
        float x2 = Math.min(this.x + this.width, other.x + other.width);
        float y2 = Math.min(this.y + this.height, other.y + other.height);

        if (x2 < x1 || y2 < y1) {
            return new ScissorRect(0, 0, 0, 0);
        }

        return new ScissorRect(x1, y1, x2 - x1, y2 - y1);
    }
}
