package com.sixeyes.module.modules.render

import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.level
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.player
import com.sixeyes.client.gui.material.M3Theme
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.ProjectionUtil
import com.sixeyes.client.util.render.RenderUtils
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.controls.MatrixControl
import com.sixeyes.client.util.render.font.Fonts
import com.sixeyes.event.events.OverlayRenderEvent
import com.sixeyes.module.Module
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityAttachment
import net.minecraft.world.entity.player.Player
import sweetie.evaware.flora.api.Commando
import kotlin.math.ceil

object TagsModule : Module(
    name = "Tags",
    category = RENDER,
    description = ""
) {
    val size = number(
        name = "Size",
        value = 1.0,
        min = 0.5,
        max = 2.5,
        step = 0.05,
        precision = 2,
        description = ""
    )

    val self = boolean(
        name = "Self",
        value = true,
        description = ""
    )

    private val lowhp = Color4b(242, 95, 92, 255)
    private val midhp = Color4b(255, 209, 102, 255)
    private val highhp = Color4b(102, 224, 122, 255)
    private val absorptionhp = Color4b(255, 215, 64, 255)

    private data class ProjectedTag(
        val target: Player,
        val screenX: Float,
        val screenY: Float,
        val depth: Float
    )

    private val projectedTags = ArrayList<ProjectedTag>()

    @Commando
    fun onOverlayRender(event: OverlayRenderEvent) {
        if (!inGame) return

        val partialTicks = event.partialTicks
        val isFirstPerson = mc.options.cameraType.isFirstPerson
        projectedTags.clear()

        for (target in level.players()) {
            if (!target.isAlive || target.isRemoved) continue
            if (target === player && (isFirstPerson || !self.value)) continue

            val interpX = Mth.lerp(partialTicks.toDouble(), target.xo, target.x)
            val interpY = Mth.lerp(partialTicks.toDouble(), target.yo, target.y)
            val interpZ = Mth.lerp(partialTicks.toDouble(), target.zo, target.z)

            val attachmentY = target.attachments
                .getNullable(EntityAttachment.NAME_TAG, 0, target.getYRot(partialTicks))
                ?.y ?: (target.bbHeight.toDouble() + 0.25)

            val projected = ProjectionUtil.worldToScreen(interpX, interpY + attachmentY + 0.2, interpZ) ?: continue
            projectedTags.add(ProjectedTag(target, projected.x, projected.y, projected.z))
        }

        if (projectedTags.isEmpty()) return

        projectedTags.sortByDescending { it.depth }

        val matrix = MatrixControl.matrix4fStack
        val palette = M3Theme.current
        val scale = size.getFloat()
        val font = Fonts.google_sans_medium

        val fontSize = 9.5f * scale
        val padX = 6f * scale
        val padY = 3.5f * scale
        val gap = 5f * scale
        val radius = 4.5f * scale
        val textH = font.getHeight(fontSize)
        val rectH = textH + padY * 2f

        val bgColor = Color4b(20, 18, 24, 215)

        for (tag in projectedTags) {
            val target = tag.target
            val nameText = target.name.string
            val totalHp = (target.health + target.absorptionAmount).coerceAtLeast(0f)
            val hpInt = ceil(totalHp).toInt()
            val hpText = "$hpInt HP"

            val nameW = font.width(nameText, fontSize)
            val hpW = font.width(hpText, fontSize)
            val rectW = padX * 2f + nameW + gap + hpW

            val rectX = tag.screenX - rectW * 0.5f
            val rectY = tag.screenY - rectH

            RenderUtils.roundedRect(
                matrix = matrix,
                x = rectX,
                y = rectY,
                width = rectW,
                height = rectH,
                color = bgColor,
                radius = radius,
                pipeline = ClientRenderPipeline.HUD_RECT
            )

            val maxHp = target.maxHealth.coerceAtLeast(1f)
            val hpPct = (target.health / maxHp).coerceIn(0f, 1f)
            val hpColor = getHealthColor(hpPct, target.absorptionAmount > 0f)

            val textY = rectY + rectH * 0.5f - fontSize * 0.56f

            font.drawString(
                matrix = matrix,
                text = nameText,
                x = rectX + padX,
                y = textY,
                size = fontSize,
                color = palette.onSurface,
                shadow = false,
                pipeline = ClientRenderPipeline.HUD_TEXT
            )

            font.drawString(
                matrix = matrix,
                text = hpText,
                x = rectX + padX + nameW + gap,
                y = textY,
                size = fontSize,
                color = hpColor,
                shadow = false,
                pipeline = ClientRenderPipeline.HUD_TEXT
            )
        }
    }

    private fun getHealthColor(pct: Float, hasAbsorption: Boolean): Color4b {
        if (hasAbsorption && pct >= 0.99f) {
            return absorptionhp
        }
        return if (pct < 0.5f) {
            lowhp.interpolateTo(midhp, (pct * 2f).toDouble())
        } else {
            midhp.interpolateTo(highhp, ((pct - 0.5f) * 2f).toDouble())
        }
    }

    fun shouldHideVanillaTag(entity: Entity): Boolean {
        if (!isEnabled() || entity !is Player) return false
        if (entity === mc.player) {
            return !mc.options.cameraType.isFirstPerson && self.value
        }
        return true
    }
}
