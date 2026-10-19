package com.sixeyes.client.gui.material.components

import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.module.setting.ModeSetting
import com.sixeyes.module.setting.MultiModeSetting
import com.sixeyes.module.setting.Setting
import org.joml.Matrix4f

class ModeComponent(
    setting: Setting<*>,
    private val modes: List<String>,
    private val isSelected: (String) -> Boolean,
    private val onSelect: (String) -> Unit
) : SettingComponent(setting) {

    constructor(modeSetting: ModeSetting) : this(
        setting = modeSetting,
        modes = modeSetting.modes,
        isSelected = { modeSetting.isMode(it) },
        onSelect = { modeSetting.set(it) }
    )

    constructor(multiModeSetting: MultiModeSetting) : this(
        setting = multiModeSetting,
        modes = multiModeSetting.modes,
        isSelected = { multiModeSetting.isSelected(it) },
        onSelect = { multiModeSetting.toggle(it) }
    )

    private data class ChipRect(
        val mode: String,
        val x: Float,
        val y: Float,
        val w: Float,
        val h: Float
    )

    private val chipRects = mutableListOf<ChipRect>()

    override fun render(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        mouseX: Int,
        mouseY: Int,
        dt: Float
    ): Float {
        this.x = x
        this.y = y
        this.width = width
        chipRects.clear()

        Fonts.google_sans_medium.drawString(
            matrix = matrix,
            text = setting.name,
            x = x + 2f,
            y = y + 2f,
            size = 10.5f,
            color = Color4b(230, 224, 233, 255),
            shadow = false,
            pipeline = ClientRenderPipeline.WINDOW_TEXT
        )

        val chipH = 14f
        val chipRadius = 4f
        val gapX = 3.5f
        val gapY = 3.5f
        val startX = x + 2f
        val maxRight = x + width - 2f

        var curX = startX
        var curY = y + 14f

        for (mode in modes) {
            val textW = Fonts.google_sans_medium.width(mode, 8.5f)
            val chipW = (textW + 8f).coerceAtLeast(24f)

            if (curX + chipW > maxRight && curX > startX) {
                curX = startX
                curY += chipH + gapY
            }

            chipRects.add(ChipRect(mode, curX, curY, chipW, chipH))

            val selected = isSelected(mode)
            val hovered = isHovered(mouseX.toDouble(), mouseY.toDouble(), curX, curY, chipW, chipH)

            val chipBg = if (selected) {
                Color4b(208, 188, 255, 255)
            } else if (hovered) {
                Color4b(82, 74, 98, 255)
            } else {
                Color4b(67, 60, 79, 255)
            }

            val textCol = if (selected) {
                Color4b(33, 0, 93, 255)
            } else {
                Color4b(230, 224, 233, 255)
            }

            RenderUtils.roundedRect(
                matrix = matrix,
                x = curX,
                y = curY,
                width = chipW,
                height = chipH,
                color = chipBg,
                radius = chipRadius,
                pipeline = ClientRenderPipeline.WINDOW_RECT
            )

            Fonts.google_sans_medium.drawCenteredString(
                matrix = matrix,
                text = mode,
                centerX = curX + chipW * 0.5f,
                y = curY + 2.8f,
                size = 8.5f,
                color = textCol,
                shadow = false,
                pipeline = ClientRenderPipeline.WINDOW_TEXT
            )

            curX += chipW + gapX
        }

        this.height = (curY + chipH) - y
        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            for (rect in chipRects) {
                if (isHovered(mouseX, mouseY, rect.x, rect.y, rect.w, rect.h)) {
                    onSelect(rect.mode)
                    return true
                }
            }
        }
        return false
    }
}
