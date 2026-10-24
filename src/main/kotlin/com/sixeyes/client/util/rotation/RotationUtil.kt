package com.sixeyes.client.util.rotation

import com.sixeyes.client.extensions.box
import com.sixeyes.client.extensions.player
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt

object RotationUtil {
    fun getRotationToVec(targetVec: Vec3, eyePos: Vec3 = player.eyePosition): Rotation {
        val diffX = targetVec.x - eyePos.x
        val diffY = targetVec.y - eyePos.y
        val diffZ = targetVec.z - eyePos.z
        val distXZ = sqrt(diffX * diffX + diffZ * diffZ)

        val yaw = Mth.wrapDegrees((Math.toDegrees(atan2(diffZ, diffX)) - 90.0).toFloat())
        val pitch = Mth.wrapDegrees((-Math.toDegrees(atan2(diffY, distXZ))).toFloat()).coerceIn(-90.0f, 90.0f)

        return Rotation(yaw, pitch)
    }

    fun getNearestPoint(eyePos: Vec3, box: AABB, marginRatio: Double = 0.0): Vec3 {
        val marginX = (box.maxX - box.minX) * marginRatio
        val marginY = (box.maxY - box.minY) * marginRatio
        val marginZ = (box.maxZ - box.minZ) * marginRatio
        val x = eyePos.x.coerceIn(box.minX + marginX, box.maxX - marginX)
        val y = eyePos.y.coerceIn(box.minY + marginY, box.maxY - marginY)
        val z = eyePos.z.coerceIn(box.minZ + marginZ, box.maxZ - marginZ)
        return Vec3(x, y, z)
    }

    fun getMultipoint(eyePos: Vec3, box: AABB, heightFactor: Double = 0.75): Vec3 {
        val marginX = (box.maxX - box.minX) * 0.15
        val marginZ = (box.maxZ - box.minZ) * 0.15
        val minX = box.minX + marginX
        val maxX = box.maxX - marginX
        val minZ = box.minZ + marginZ
        val maxZ = box.maxZ - marginZ

        val clampedX = eyePos.x.coerceIn(minX, maxX)
        val clampedZ = eyePos.z.coerceIn(minZ, maxZ)

        val targetY = (box.minY + (box.maxY - box.minY) * heightFactor)
            .coerceIn(box.minY + 0.1, box.maxY - 0.1)
        val clampedY = (eyePos.y * 0.65 + targetY * 0.35).coerceIn(box.minY + 0.1, box.maxY - 0.1)

        return Vec3(clampedX, clampedY, clampedZ)
    }

    fun getDistanceToBox(entity: Entity, eyePos: Vec3 = player.eyePosition): Double {
        val nearest = getNearestPoint(eyePos, entity.box)
        return eyePos.distanceTo(nearest)
    }

    fun getFovToEntity(entity: Entity, baseRotation: Rotation = Rotation(player.yRot, player.xRot)): Float {
        val center = entity.box.center
        val rotToTarget = getRotationToVec(center)
        val dYaw = Mth.wrapDegrees(rotToTarget.yaw - baseRotation.yaw)
        val dPitch = (rotToTarget.pitch - baseRotation.pitch) * 0.5f
        return hypot(dYaw, dPitch)
    }

    fun isInFov(entity: Entity, fov: Float, baseRotation: Rotation = Rotation(player.yRot, player.xRot)): Boolean {
        if (fov >= 180.0f) return true
        val center = entity.box.center
        val rotToTarget = getRotationToVec(center)
        val yawDiff = kotlin.math.abs(Mth.wrapDegrees(rotToTarget.yaw - baseRotation.yaw))
        return yawDiff <= fov
    }
}
