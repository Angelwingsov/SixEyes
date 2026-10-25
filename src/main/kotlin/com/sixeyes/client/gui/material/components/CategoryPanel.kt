package com.sixeyes.client.gui.material.components

import com.sixeyes.client.extensions.Category
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.gui.material.AnimFloat
import com.sixeyes.client.gui.material.ClickGuiScreen
import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.ScissorUtil
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.ModuleManager
import org.joml.Matrix4f
import kotlin.math.hypot

class CategoryPanel(
    val category: Category,
    override var x: Float,
    override var y: Float,
    override var width: Float = 122f
) : GuiComponent {
    override var height: Float = 42f
    var isExpanded = true
    private val expandAnim = AnimFloat(if (isExpanded) 1f else 0f, if (isExpanded) 1f else 0f, 16f)
    private val scrollYAnim = AnimFloat(y, y, 18f)

    var isDragging = false
    private var hasMovedWhileDragging = false
    private var dragStartMouseX = 0.0
    private var dragStartMouseY = 0.0
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f

    val moduleComponents: MutableList<ModuleComponent> = ArrayList()

    init {
        for (mod in ModuleManager.getByCategory(category)) {
            moduleComponents.add(ModuleComponent(mod))
        }
    }

    fun render(
        matrix: Matrix4f,
        mouseX: Int,
        mouseY: Int,
        dt: Float
    ): Float {
        val palette = M3Theme.current
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

        if (!isDragging) {
            y = scrollYAnim.update(clampY(scrollYAnim.target), dt)
        }

        val outerCardBg = Color4b(20, 18, 24, 255)
        RenderUtils.roundedRect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = totalPanelH,
            color = outerCardBg,
            radius = outerRadius,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )

        val innerHeaderBg = Color4b(33, 31, 36, 255)
        val innerBtnW = width - innerPadX * 2f
        RenderUtils.roundedRect(
            matrix = matrix,
            x = x + innerPadX,
            y = y + innerPadY,
            width = innerBtnW,
            height = headerBtnH,
            color = innerHeaderBg,
            radius = innerRadius,
            pipeline = ClientRenderPipeline.WINDOW_RECT
        )
        Fonts.google_sans_medium.drawCenteredString(
            matrix = matrix,
            text = category.displayName,
            centerX = x + width * 0.5f,
            y = y + innerPadY + 7.5f,
            size = 11.5f,
            color = palette.onSurface,
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        if (currentBodyH > 1f && moduleComponents.isNotEmpty()) {
            ScissorUtil.withScissor(x, y + headerH, width, currentBodyH) {
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
            }
        }

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val headerH = 37f

        if (isHovered(mouseX, mouseY, x, y, width, if (isExpanded) headerH else 42f)) {
            if (button == 0) {
                ClickGuiScreen.clearAllListening()
                isDragging = true
                hasMovedWhileDragging = false
                dragStartMouseX = mouseX
                dragStartMouseY = mouseY
                dragOffsetX = (mouseX - x).toFloat()
                dragOffsetY = (mouseY - y).toFloat()
                return true
            } else if (button == 1) {
                ClickGuiScreen.clearAllListening()
                isExpanded = !isExpanded
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

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        if (isDragging && button == 0) {
            if (!hasMovedWhileDragging) {
                isExpanded = !isExpanded
            }
            isDragging = false
            hasMovedWhileDragging = false
        }
        if (isExpanded) {
            for (modComp in moduleComponents) {
                modComp.mouseReleased(mouseX, mouseY, button)
            }
        }
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double) {
        if (isDragging) {
            if (!hasMovedWhileDragging && hypot(mouseX - dragStartMouseX, mouseY - dragStartMouseY) > 2.5) {
                hasMovedWhileDragging = true
            }
            x = (mouseX - dragOffsetX).toFloat().coerceIn(4f, mc.window.guiScaledWidth - width - 4f)
            y = clampY((mouseY - dragOffsetY).toFloat())
            scrollYAnim.setImmediate(y)
        } else if (isExpanded) {
            for (modComp in moduleComponents) {
                modComp.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
            }
        }
    }

    fun mouseScrolled(scrollY: Double) {
        val step = 24f
        scrollYAnim.target = clampY(scrollYAnim.target + scrollY.toFloat() * step)
    }

    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return isHovered(mouseX, mouseY, x, y, width, height)
    }

    private fun clampY(candidateY: Float): Float {
        val screenH = mc.window.guiScaledHeight.toFloat()
        val minY = 4f.coerceAtMost(screenH - height - 8f)
        val maxY = (screenH - 40f).coerceAtLeast(minY)
        return candidateY.coerceIn(minY, maxY)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
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
