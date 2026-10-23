package com.sixeyes.client.extensions

enum class Category(val displayName: String) {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player"),
    OTHER("Other");

    override fun toString(): String = displayName
}

val COMBAT = Category.COMBAT
val MOVEMENT = Category.MOVEMENT
val RENDER = Category.RENDER
val PLAYER = Category.PLAYER
val OTHER = Category.OTHER

val categories: List<Category> = Category.entries
