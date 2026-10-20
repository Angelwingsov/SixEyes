package com.sixeyes.client.gui.material.components

import com.mojang.blaze3d.platform.InputConstants
import com.sixeyes.client.extensions.Category
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.ScissorUtil
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.ModuleManager
import org.joml.Matrix4f

class CategoryPanel(
    val category: Category,
    var x: Float,
    var y: Float,
    val width: Float = 122f
) {
    var height: Float = 42f
    var isExpanded = true
    private val expandAnim = AnimFloat(if (isExpanded) 1f else 0f, if (isExpanded) 1f else 0f, 16f)

    var isDragging = false
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    val moduleComponents: MutableList<ModuleComponent> = ArrayList()

    init {
        val mods = ModuleManager.modules.filter { it.category == category }
        for (mod in mods) {
            moduleComponents.add(ModuleComponent(mod))
        }
    }

    fun render(
        matrix: Matrix4f,
        mouseX: Int,
        mouseY: Int,
        dt: Float
    ): Float {
        val collapsedH = 42f
        val headerBtnH = 27f
        val innerPadX = 5f
        val innerPadY = 5f
        val outerRadius = 12f
        val innerRadius = 8f
        val headerH = 37f
        var totalBodyContentH = 0f
        if (moduleComponents.isNotEmpty()) {
            for (modComp in moduleComponents) {
                totalBodyContentH += modComp.height + 4f
            }
            totalBodyContentH += 6f
        }

        val expandProgress = expandAnim.update(if (isExpanded) 1f else 0f, dt)
        val currentBodyH = totalBodyContentH * expandProgress
        val totalPanelH = collapsedH + (headerH + totalBodyContentH - collapsedH) * expandProgress
        this.height = totalPanelH

        val outerCardBg = Color4b(20, 18, 24, 255)
        RenderUtils.roundedRect(matrix = matrix, x = x, y = y, width = width, height = totalPanelH, color = outerCardBg, radius = outerRadius, pipeline = ClientRenderPipeline.WINDOW_RECT)

        val innerHeaderBg = Color4b(33, 31, 36, 255)
        val innerBtnW = width - innerPadX * 2f
        RenderUtils.roundedRect(matrix = matrix, x = x + innerPadX, y = y + innerPadY, width = innerBtnW, height = headerBtnH, color = innerHeaderBg, radius = innerRadius, pipeline = ClientRenderPipeline.WINDOW_RECT)
        Fonts.google_sans_medium.drawCenteredString(matrix = matrix, text = category.name, centerX = x + width * 0.5f, y = y + innerPadY + 8f, size = 11.5f, color = Color4b(230, 224, 233, 255), shadow = false, pipeline = ClientRenderPipeline.WINDOW_TEXT)

        if (currentBodyH > 1f && moduleComponents.isNotEmpty()) {
            ScissorUtil.start(x, y + headerH, width, currentBodyH)

            var curModY = y + headerH
            for (modComp in moduleComponents) {
                val modH = modComp.render(
                    matrix = matrix,
                    x = x + 5f,
                    y = curModY,
                    width = width - 10f,
                    mouseX = mouseX,
                    mouseY = mouseY,
                    dt = dt
                )
                curModY += modH + 4f
            }

            ScissorUtil.end()
        }

        return height
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val headerH = 37f

        if (isHovered(mouseX, mouseY, x, y, width, if (isExpanded) headerH else 42f)) {
            if (button == 0) {
                isExpanded = !isExpanded
                isDragging = true
                dragOffsetX = (mouseX - x).toFloat()
                dragOffsetY = (mouseY - y).toFloat()
                return true
            } else if (button == 1) {
                isDragging = true
                dragOffsetX = (mouseX - x).toFloat()
                dragOffsetY = (mouseY - y).toFloat()
                return true
            }
        }

        if (isExpanded) {
            for (modComp in moduleComponents) {
                if (modComp.mouseClicked(mouseX, mouseY, button)) {
                    return true
                }
            }
        }

        return false
    }

    fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        isDragging = false
        if (isExpanded) {
            for (modComp in moduleComponents) {
                modComp.mouseReleased(mouseX, mouseY, button)
            }
        }
    }

    fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {
        if (isDragging) {
            x = (mouseX - dragOffsetX).toFloat().coerceIn(4f, mc.window.guiScaledWidth - width - 4f)
            y = (mouseY - dragOffsetY).toFloat().coerceIn(4f, mc.window.guiScaledHeight - 40f)
        } else if (isExpanded) {
            for (modComp in moduleComponents) {
                modComp.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
            }
        }
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isExpanded) {
            for (modComp in moduleComponents) {
                if (modComp.keyPressed(keyCode, scanCode, modifiers)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isHovered(mouseX: Double, mouseY: Double, x: Float, y: Float, w: Float, h: Float): Boolean {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
    }
}
