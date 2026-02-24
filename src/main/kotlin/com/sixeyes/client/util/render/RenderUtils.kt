package com.sixeyes.client.util.render

import com.sixeyes.client.render.texture.texture.GLTexture
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.rect.RectBorder
import com.sixeyes.client.util.render.rect.RectColors
import com.sixeyes.client.util.render.rect.RectCorners
import com.sixeyes.client.util.render.rect.RectRenderer
import com.sixeyes.client.util.render.rect.RectStyle
import com.sixeyes.client.util.render.rect.RectType
import com.sixeyes.client.util.render.rect.RectUv
import org.joml.Matrix4f

object RenderUtils {
    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.solid(color),
            pipeline = pipeline
        )
    }

    fun roundedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        color: Color4b,
        radius: Float,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.rounded(color, radius),
            pipeline = pipeline
        )
    }

    fun gradientRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        topLeft: Color4b,
        topRight: Color4b,
        bottomRight: Color4b,
        bottomLeft: Color4b,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.gradient(topLeft, topRight, bottomRight, bottomLeft),
            pipeline = pipeline
        )
    }

    fun texturedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        textureId: Int,
        tint: Color4b = Color4b.WHITE,
        mix: Float = 0f,
        alpha: Float = 1f,
        uv: RectUv = RectUv.DEFAULT,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle.textured(
                textureId = textureId,
                tint = tint,
                mix = mix,
                alpha = alpha,
                uv = uv
            ),
            pipeline = pipeline
        )
    }

    fun texturedRect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        texture: GLTexture,
        tint: Color4b = Color4b.WHITE,
        mix: Float = 0f,
        alpha: Float = 1f,
        uv: RectUv = RectUv.DEFAULT,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        texturedRect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            textureId = texture.texId,
            tint = tint,
            mix = mix,
            alpha = alpha,
            uv = uv,
            pipeline = pipeline
        )
    }

    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        style: RectStyle,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        RectRenderer.emit(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = style,
            pipeline = pipeline
        )
    }

    fun rect(
        matrix: Matrix4f,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        colors: RectColors,
        corners: RectCorners = RectCorners.ZERO,
        border: RectBorder = RectBorder.NONE,
        mode: Float = 0f,
        textureId: Int = 0,
        mix: Float = 0f,
        alpha: Float = 1f,
        uv: RectUv = RectUv.DEFAULT,
        type: RectType = RectType.BASIC,
        pipeline: ClientRenderPipeline = ClientRenderPipeline.GUI_RECT
    ) {
        rect(
            matrix = matrix,
            x = x,
            y = y,
            width = width,
            height = height,
            style = RectStyle(
                colors = colors,
                corners = corners,
                border = border,
                mode = mode,
                textureId = textureId,
                mix = mix,
                alpha = alpha,
                uv = uv,
                type = type
            ),
            pipeline = pipeline
        )
    }
}
