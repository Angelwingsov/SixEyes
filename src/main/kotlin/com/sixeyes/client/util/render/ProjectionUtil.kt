package com.sixeyes.client.util.render

import com.sixeyes.client.extensions.mc
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

object ProjectionUtil {
    fun worldToScreen(worldPos: Vec3): Vector3f? =
        worldToScreen(worldPos.x, worldPos.y, worldPos.z)

    fun worldToScreen(worldX: Double, worldY: Double, worldZ: Double): Vector3f? {
        val gameRenderer = mc.gameRenderer
        val camera = gameRenderer.mainCamera()
        if (!camera.isInitialized) return null

        val camPos = camera.position()
        val dx = worldX - camPos.x
        val dy = worldY - camPos.y
        val dz = worldZ - camPos.z

        val forward = camera.forwardVector()
        val dot = dx * forward.x() + dy * forward.y() + dz * forward.z()
        if (dot <= 0.05) return null

        val projected = gameRenderer.projectPointToScreen(Vec3(worldX, worldY, worldZ))

        val guiW = mc.window.guiScaledWidth.toFloat()
        val guiH = mc.window.guiScaledHeight.toFloat()

        val screenX = ((projected.x + 1.0) * 0.5 * guiW).toFloat()
        val screenY = ((1.0 - projected.y) * 0.5 * guiH).toFloat()

        return Vector3f(screenX, screenY, projected.z.toFloat())
    }
}
