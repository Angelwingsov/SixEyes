package com.sixeyes.client.gui.material.components

interface GuiComponent {
    var x: Float
    var y: Float
    var width: Float
    var height: Float

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = false
    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {}
    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {}
    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false
}
