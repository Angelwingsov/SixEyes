package com.sixeyes.module.modules.render

import com.sixeyes.client.extensions.RENDER
import com.sixeyes.client.extensions.inGame
import com.sixeyes.client.extensions.level
import com.sixeyes.client.extensions.mc
import com.sixeyes.client.extensions.player
import com.sixeyes.client.gui.material.M3Palette
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
    private val absorptionHp = Color4b(255, 215, 64, 255)

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

        val fontSize = 9.0f * scale
        val badgeFontSize = 8.0f * scale

        val padX = 5.0f * scale
        val gap = 4.0f * scale
        val badgePadX = 3.5f * scale
        val badgeH = 11.0f * scale
        val badgeRadius = 3.0f * scale

        val rectH = 13.5f * scale
        val rectRadius = 4.0f * scale

        val bgColor = palette.surface.with(a = 215)
        val badgeBg = palette.primaryContainer.with(a = 120)

        for (tag in projectedTags) {
            val target = tag.target
            val nameText = target.name.string

            val hp = target.health.coerceAtLeast(0f)
            val absorption = target.absorptionAmount.coerceAtLeast(0f)
            val totalHp = hp + absorption
            val hpInt = ceil(totalHp).toInt()
            val hpText = "$hpInt HP"

            val maxHp = target.maxHealth.coerceAtLeast(1f)
            val hpPct = (hp / maxHp).coerceIn(0f, 1f)
            val hpColor = getHealthColor(hpPct, absorption > 0f, palette)

            val nameW = font.width(nameText, fontSize)
            val hpTextW = font.width(hpText, badgeFontSize)
            val badgeW = hpTextW + badgePadX * 2f

            val rectW = padX + nameW + gap + badgeW + padX
            val rectX = tag.screenX - rectW * 0.5f
            val rectY = tag.screenY - rectH

            RenderUtils.roundedRect(
                matrix = matrix,
                x = rectX,
                y = rectY,
                width = rectW,
                height = rectH,
                color = bgColor,
                radius = rectRadius,
                pipeline = ClientRenderPipeline.HUD_RECT
            )

            val nameY = rectY + rectH * 0.5f - fontSize * 0.56f
            font.drawString(
                matrix = matrix,
                text = nameText,
                x = rectX + padX,
                y = nameY,
                size = fontSize,
                color = palette.onSurface,
                shadow = false,
                pipeline = ClientRenderPipeline.HUD_TEXT
            )

            val badgeX = rectX + rectW - padX - badgeW
            val badgeY = rectY + (rectH - badgeH) * 0.5f

            RenderUtils.roundedRect(
                matrix = matrix,
                x = badgeX,
                y = badgeY,
                width = badgeW,
                height = badgeH,
                color = badgeBg,
                radius = badgeRadius,
                pipeline = ClientRenderPipeline.HUD_RECT
            )

            val badgeTextY = badgeY + badgeH * 0.5f - badgeFontSize * 0.56f
            font.drawCenteredString(
                matrix = matrix,
                text = hpText,
                centerX = badgeX + badgeW * 0.5f,
                y = badgeTextY,
                size = badgeFontSize,
                color = hpColor,
                shadow = false,
                pipeline = ClientRenderPipeline.HUD_TEXT
            )
        }
    }

    private fun getHealthColor(pct: Float, hasAbsorption: Boolean, palette: M3Palette): Color4b {
        if (hasAbsorption && pct >= 0.99f) {
            return absorptionHp
        }
        return if (pct < 0.5f) {
            palette.error.interpolateTo(palette.tertiary, (pct * 2f).toDouble())
        } else {
            palette.tertiary.interpolateTo(palette.primary, ((pct - 0.5f) * 2f).toDouble())
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
