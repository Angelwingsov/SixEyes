package com.sixeyes.client.util.render

import com.mojang.renderpearl.api.GpuFormat
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.mojang.renderpearl.api.vertex.VertexFormat
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

object VulkanShapePipelines {
    val RECT_SDF_FORMAT: VertexFormat by lazy {
        VertexFormat.builder(0)
            .addAttribute("Position", GpuFormat.RGB32_FLOAT)
            .addAttribute("Color", GpuFormat.RGBA8_UNORM)
            .addAttribute("UV0", GpuFormat.RG32_FLOAT)
            .addAttribute("UV1", GpuFormat.RG16_SINT)
            .addAttribute("UV2", GpuFormat.RG16_SINT)
            .addAttribute("UV3", GpuFormat.RG32_FLOAT)
            .addAttribute("LineWidth", GpuFormat.R32_FLOAT)
            .build()
    }

    val RECT_SDF: RenderPipeline by lazy {
        val pipeline = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("sixeyes", "pipeline/rect_sdf"))
            .withVertexShader(Identifier.fromNamespaceAndPath("sixeyes", "core/rect_sdf"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("sixeyes", "core/rect_sdf"))
            .withVertexBinding(0, RECT_SDF_FORMAT)
            .build()
        RenderPipelines.register(pipeline)
        pipeline
    }

    val CORNER_SDF: RenderPipeline by lazy {
        val pipeline = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("sixeyes", "pipeline/corner_sdf"))
            .withFragmentShader(Identifier.fromNamespaceAndPath("sixeyes", "core/corner_sdf"))
            .build()
        RenderPipelines.register(pipeline)
        pipeline
    }

    fun init() {
        RECT_SDF
        CORNER_SDF
    }
}
