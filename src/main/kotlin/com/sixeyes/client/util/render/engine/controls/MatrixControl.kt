package com.sixeyes.client.util.render.engine.controls

import com.mojang.blaze3d.ProjectionType
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer
import org.joml.Matrix4f
import org.joml.Matrix4fStack
import com.sixeyes.client.extensions.CLIENT_ID
import com.sixeyes.client.extensions.mc

object MatrixControl {
    private val matrix = CachedOrthoProjectionMatrixBuffer("$CLIENT_ID-projection-matrix", -1000f, 1000f, true)
    private val projectionMatrix: Matrix4f = Matrix4f()

    @JvmField
    val matrix4fStack = Matrix4fStack(16).apply {
        identity()
    }

    fun unscaledProjection() {
        val width = mc.window.guiScaledWidth.toFloat()
        val height = mc.window.guiScaledHeight.toFloat()
        RenderSystem.setProjectionMatrix(matrix.getBuffer(width, height), ProjectionType.ORTHOGRAPHIC)
        projectionMatrix.set(matrix.createProjectionMatrix(width, height))
    }

    fun scaledProjection() {
        val width = mc.window.guiScaledWidth.toFloat() / 2
        val height = mc.window.guiScaledHeight.toFloat() / 2

        RenderSystem.setProjectionMatrix(matrix.getBuffer(width, height), ProjectionType.PERSPECTIVE)
        projectionMatrix.set(matrix.createProjectionMatrix(width, height))
    }

    fun startScale(x: Float, y: Float, scale: Float) {
        matrix4fStack.translate(x, y, 0f)
        matrix4fStack.scale(scale, scale, 1.0f)
        matrix4fStack.translate(-x, -y, 0f)
    }

    fun reset() {
        matrix4fStack.identity()
    }

    fun pushMatrix() {
        matrix4fStack.pushMatrix()
    }

    fun popMatrix() {
        matrix4fStack.popMatrix()
    }
}