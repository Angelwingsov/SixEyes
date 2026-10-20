package com.sixeyes.client.util.render

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.renderpearl.api.textures.FilterMode
import net.minecraft.client.renderer.texture.DynamicTexture
import java.util.function.Supplier

class LinearDynamicTexture(label: Supplier<String>, image: NativeImage) : DynamicTexture(label, image) {
    init {
        this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
    }
}
