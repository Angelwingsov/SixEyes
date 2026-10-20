package com.sixeyes.client.util.render.font

import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

object VulkanFontPipelines {
    val MSDF_FONT_G_SANS: RenderPipeline by lazy {
        val pipeline = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("sixeyes", "pipeline/msdf_font_g_sans"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("sixeyes", "core/font_msdf"))
            .withShaderDefine("MSDF_RANGE", 10.0f)
            .build()
        RenderPipelines.register(pipeline)
        pipeline
    }

    val MSDF_FONT_P_SANS: RenderPipeline by lazy {
        val pipeline = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("sixeyes", "pipeline/msdf_font_p_sans"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("sixeyes", "core/font_msdf"))
            .withShaderDefine("MSDF_RANGE", 32.0f)
            .build()
        RenderPipelines.register(pipeline)
        pipeline
    }

    fun getPipeline(distanceRange: Float): RenderPipeline {
        return if (distanceRange > 16.0f) MSDF_FONT_P_SANS else MSDF_FONT_G_SANS
    }

    fun init() {
        MSDF_FONT_G_SANS
        MSDF_FONT_P_SANS
    }
}
