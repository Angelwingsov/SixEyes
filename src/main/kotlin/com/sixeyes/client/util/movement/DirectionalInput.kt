package com.sixeyes.client.util.movement

import net.minecraft.client.player.ClientInput
import net.minecraft.world.entity.player.Input

data class DirectionalInput(
    var forwards: Boolean = false,
    var backwards: Boolean = false,
    var left: Boolean = false,
    var right: Boolean = false
) {
    constructor(input: Input) : this(
        input.forward(),
        input.backward(),
        input.left(),
        input.right()
    )

    constructor(input: ClientInput) : this(
        input.keyPresses.forward(),
        input.keyPresses.backward(),
        input.keyPresses.left(),
        input.keyPresses.right()
    )

    constructor(movementForward: Float, movementSideways: Float) : this(
        movementForward > 0.0f,
        movementForward < 0.0f,
        movementSideways > 0.0f,
        movementSideways < 0.0f
    )

    val isMoving: Boolean
        get() = forwards || backwards || left || right

    companion object {
        val NONE = DirectionalInput(forwards = false, backwards = false, left = false, right = false)
        val FORWARDS = DirectionalInput(forwards = true, backwards = false, left = false, right = false)
        val BACKWARDS = DirectionalInput(forwards = false, backwards = true, left = false, right = false)
        val LEFT = DirectionalInput(forwards = false, backwards = false, left = true, right = false)
        val RIGHT = DirectionalInput(forwards = false, backwards = false, left = false, right = true)
    }
}
