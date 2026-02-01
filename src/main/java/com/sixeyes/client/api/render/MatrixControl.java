package com.sixeyes.client.api.render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.ProjectionMatrix2;
import org.joml.Matrix4f;
import com.sixeyes.client.Mc;
import com.sixeyes.client.mixin.IProjectionMatrix2;

public class MatrixControl implements Mc {
    private final ProjectionMatrix2 matrix = new ProjectionMatrix2("evaware-projection-matrix", -1000, 1000, true);
    public Matrix4f projectionMatrix = new Matrix4f();

    /*
     * Р Р€Р В±Р С‘РЎР‚Р В°Р ВµРЎвЂљ Р СР В°Р в„–Р Р…Р С”РЎР‚Р В°РЎвЂћРЎвЂљР С•Р Р†РЎРѓР С”Р С‘Р в„– Р СР В°РЎРѓРЎв‚¬РЎвЂљР В°Р В± Р С–РЎС“Р С‘ Р С‘ РЎС“РЎРѓРЎвЂљР В°Р Р…Р В°Р Р†Р В»Р С‘Р Р†Р В°Р ВµРЎвЂљ Р С”Р В°РЎРѓРЎвЂљР С•Р СР Р…РЎвЂ№Р в„–
     */
    public void unscaledProjection() {
        float width = mc.getWindow().getFramebufferWidth() / (float) 2;
        float height = mc.getWindow().getFramebufferHeight() / (float) 2;

        RenderSystem.setProjectionMatrix(matrix.set(width, height), ProjectionType.ORTHOGRAPHIC);
        projectionMatrix.set(((IProjectionMatrix2) matrix)._getMatrix(width, height));
    }

    /*
     * Р вЂ™Р С•Р В·Р Р†РЎР‚Р В°РЎвЂ°Р В°Р ВµРЎвЂљ Р СР В°РЎРѓРЎв‚¬РЎвЂљР В°Р В± Р С•Р В±РЎР‚Р В°РЎвЂљР Р…Р С•
     */
    public void scaledProjection() {
        float width = (float) (mc.getWindow().getFramebufferWidth() / 2f);
        float height = (float) (mc.getWindow().getFramebufferHeight() / 2f);

        RenderSystem.setProjectionMatrix(matrix.set(width, height), ProjectionType.PERSPECTIVE);
        projectionMatrix.set(((IProjectionMatrix2) matrix)._getMatrix(width, height));
    }
}


