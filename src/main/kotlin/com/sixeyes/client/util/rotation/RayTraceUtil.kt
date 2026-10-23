package com.sixeyes.client.util.rotation

import com.sixeyes.client.extensions.box
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.level
import com.sixeyes.client.extensions.player
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

object RayTraceUtil {
    fun isLookingAtEntity(
        target: Entity,
        rotation: Rotation = RotationManager.getServerRotation(),
        range: Double,
        throughWalls: Boolean = true
    ): Boolean {
        if (!inGame) return false

        val eyePos = player.eyePosition
        val targetBox = target.box

        if (targetBox.contains(eyePos)) {
            return true
        }

        val direction = rotation.toDirectionVec()
        val traceRange = range + 2.0
        val endPos = eyePos.add(direction.scale(traceRange))

        val hitOptional = targetBox.clip(eyePos, endPos)
        if (hitOptional.isEmpty) {
            return false
        }

        val hitVec: Vec3 = hitOptional.get()
        val entityDistSq = eyePos.distanceToSqr(hitVec)
        if (entityDistSq > range * range) {
            return false
        }

        if (!throughWalls) {
            val blockHit = level.clip(
                ClipContext(
                    eyePos,
                    hitVec,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    player
                )
            )
            if (blockHit.type != HitResult.Type.MISS) {
                val blockDistSq = eyePos.distanceToSqr(blockHit.location)
                if (blockDistSq + 1e-4 < entityDistSq) {
                    return false
                }
            }
        }

        return true
    }
}
