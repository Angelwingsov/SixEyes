package com.sixeyes.client.util.movement

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.movementForward
import com.sixeyes.client.extensions.movementSideways
import net.minecraft.client.KeyMapping
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

object MoveUtil {

    val movementKeys: Array<KeyMapping> by lazy {
        arrayOf(
            mc.options.keySprint,
            mc.options.keyUp,
            mc.options.keyDown,
            mc.options.keyLeft,
            mc.options.keyRight,
            mc.options.keyJump
        )
    }

    val allMovementKeys: Array<KeyMapping> by lazy {
        arrayOf(
            mc.options.keySprint,
            mc.options.keyUp,
            mc.options.keyDown,
            mc.options.keyLeft,
            mc.options.keyRight,
            mc.options.keyJump,
            mc.options.keyShift
        )
    }

    fun isMoving(): Boolean {
        val player = mc.player ?: return false
        return player.input.movementForward != 0f || player.input.movementSideways != 0f
    }

    fun isKeyPressed(keyMapping: KeyMapping): Boolean {
        if (keyMapping.isUnbound) return false
        val key = keyMapping.key
        return if (key.type == InputConstants.Type.MOUSE) {
            when (key.value) {
                InputConstants.MOUSE_BUTTON_LEFT -> mc.mouseHandler.isLeftPressed
                InputConstants.MOUSE_BUTTON_MIDDLE -> mc.mouseHandler.isMiddlePressed
                InputConstants.MOUSE_BUTTON_RIGHT -> mc.mouseHandler.isRightPressed
                else -> false
            }
        } else {
            InputConstants.isKeyDown(key.value)
        }
    }

    fun updateMovementKeys(includeSneak: Boolean = false) {
        val keys = if (includeSneak) allMovementKeys else movementKeys
        for (key in keys) {
            key.isDown = isKeyPressed(key)
        }
    }

    fun resetMovementKeys() {
        for (key in allMovementKeys) {
            key.isDown = false
        }
    }

    fun getDistanceToGround(): Float {
        val player = mc.player ?: return 0f
        val level = mc.level ?: return 0f
        val pos = player.position()
        val startY = pos.y - 1e-5
        val mutablePos = BlockPos.MutableBlockPos()

        val bottomY = level.minY
        var y = floor(startY).toInt()
        while (y >= bottomY) {
            mutablePos.set(pos.x, y.toDouble(), pos.z)
            val state = level.getBlockState(mutablePos)
            val collisionShape = state.getCollisionShape(level, mutablePos)
            if (!collisionShape.isEmpty) {
                val surfaceHeight = y + collisionShape.max(Direction.Axis.Y)
                return (pos.y - surfaceHeight).toFloat()
            }
            y--
        }
        return (pos.y - bottomY).toFloat()
    }

    fun isAboveWater(): Boolean {
        val player = mc.player ?: return false
        val level = mc.level ?: return false
        return player.isUnderWater || level.getBlockState(player.blockPosition().below()).block == Blocks.WATER
    }

    fun isInWeb(): Boolean {
        val player = mc.player ?: return false
        val level = mc.level ?: return false
        val box = player.boundingBox
        val pos = player.blockPosition()
        val mutablePos = BlockPos.MutableBlockPos()

        for (x in pos.x - 2..pos.x + 2) {
            for (y in pos.y - 1..pos.y + 4) {
                for (z in pos.z - 2..pos.z + 2) {
                    mutablePos.set(x, y, z)
                    if (level.getBlockState(mutablePos).block == Blocks.COBWEB && box.intersects(AABB(mutablePos))) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun forward(speed: Double, customYaw: Float? = null): DoubleArray {
        val player = mc.player ?: return doubleArrayOf(0.0, 0.0)
        var forward = player.input.movementForward
        var strafe = player.input.movementSideways
        var yaw = customYaw ?: player.yRot

        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                yaw += if (forward > 0.0f) -45f else 45f
            } else if (strafe < 0.0f) {
                yaw += if (forward > 0.0f) 45f else -45f
            }
            strafe = 0.0f
            forward = if (forward > 0.0f) 1.0f else -1.0f
        }

        val rad = Math.toRadians(yaw + 90.0)
        val cosStrafe = sin(rad)
        val sinStrafe = cos(rad)
        val x = forward * speed * sinStrafe + strafe * speed * cosStrafe
        val z = forward * speed * cosStrafe - strafe * speed * sinStrafe
        return doubleArrayOf(x, z)
    }

    fun setSpeed(speed: Double) {
        val player = mc.player ?: return
        val motion = forward(speed)
        player.deltaMovement = Vec3(motion[0], player.deltaMovement.y, motion[1])
    }
}
