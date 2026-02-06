package com.sixeyes.client.render.comet.program.uniform.uniforms.sampler;

import com.sixeyes.client.render.texture.GlTex;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.GlTextureView;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.util.function.BiConsumer;

public record SamplerUniformUploader<T>(BiConsumer<SamplerUniform, T> uploadConsumer) {

    public static final SamplerUniformUploader<GlTex> GL_TEX = new SamplerUniformUploader<>(
            (samplerUniform, glTex) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.getSamplerId());
                GlStateManager._bindTexture(glTex.getTexId());
            }
    );
    public static final SamplerUniformUploader<GlTextureView> GL_TEXTURE_VIEW = new SamplerUniformUploader<>(
            (samplerUniform, textureView) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.getSamplerId());
                GlTexture glTexture = textureView.texture();
                int o;
                if ((glTexture.usage() & GpuTexture.USAGE_CUBEMAP_COMPATIBLE) != 0) {
                    o = GL13.GL_TEXTURE_CUBE_MAP;
                    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, glTexture.getGlId());
                } else {
                    o = GlConst.GL_TEXTURE_2D;
                    GlStateManager._bindTexture(glTexture.getGlId());
                }

                GlStateManager._texParameter(o, 33084, textureView.baseMipLevel());
                GlStateManager._texParameter(o, GL12.GL_TEXTURE_MAX_LEVEL, textureView.baseMipLevel() + textureView.mipLevels() - 1);
            }
    );
    public static final SamplerUniformUploader<Integer> TEXTURE_ID = new SamplerUniformUploader<>(
            (samplerUniform, id) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.getSamplerId());
                GlStateManager._bindTexture(id);
            }
    );
}


