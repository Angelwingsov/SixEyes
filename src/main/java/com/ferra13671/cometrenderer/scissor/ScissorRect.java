package com.ferra13671.cometrenderer.scissor;

import com.ferra13671.cometrenderer.CometRenderer;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL20;

public record ScissorRect(int x, int y, int width, int height) {


    public void bind() {
        GL20.glScissor(this.x, this.y, this.width, this.height);
    }


    public ScissorRect fixRect() {
        int scale = CometRenderer.getScaleGetter().get();
        int frameBufferHeight = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
        return new ScissorRect(
                this.x * scale,
                frameBufferHeight - ((this.y + this.height) * scale),
                this.width * scale,
                this.height * scale
        );
    }


    public ScissorRect intersection(ScissorRect other) {
        int x1 = Math.max(this.x, other.x);
        int y1 = Math.max(this.y, other.y);
        int x2 = Math.min(this.x + this.width, other.x + other.width);
        int y2 = Math.min(this.y + this.height, other.y + other.height);

        return x1 < x2 && y1 < y2 ? new ScissorRect(x1, y1, x2 - x1, y2 - y1) : null;
    }
}
