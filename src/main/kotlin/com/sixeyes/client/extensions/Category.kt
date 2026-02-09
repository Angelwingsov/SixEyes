package com.sixeyes.client.extensions

val COMBAT = Category("Combat")
val MOVEMENT = Category("Movement")
val RENDER = Category("Render")
val PLAYER = Category("Player")
val OTHER = Category("Other")

val categories = listOf(COMBAT, MOVEMENT, RENDER, PLAYER, OTHER)

open class Category(
    val name: String
)