package com.sixeyes.client.api.render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.ProjectionMatrix2;
import org.joml.Matrix4f;
import com.sixeyes.client.api.utility.other.Mc;
import com.sixeyes.client.mixin.IProjectionMatrix2;

public class MatrixControl implements Mc {
    private final ProjectionMatrix2 matrix = new ProjectionMatrix2("sixeyes-projection-matrix", -1000, 1000, true);
    public Matrix4f projectionMatrix = new Matrix4f();

    public void unscaledProjection() {
        float width = mc.getWindow().getFramebufferWidth() / (float) 2;
        float height = mc.getWindow().getFramebufferHeight() / (float) 2;

        RenderSystem.setProjectionMatrix(matrix.set(width, height), ProjectionType.ORTHOGRAPHIC);
        projectionMatrix.set(((IProjectionMatrix2) matrix)._getMatrix(width, height));
    }

    public void scaledProjection() {
        float width = (float) (mc.getWindow().getFramebufferWidth() / 2f);
        float height = (float) (mc.getWindow().getFramebufferHeight() / 2f);

        RenderSystem.setProjectionMatrix(matrix.set(width, height), ProjectionType.PERSPECTIVE);
        projectionMatrix.set(((IProjectionMatrix2) matrix)._getMatrix(width, height));
    }
}
