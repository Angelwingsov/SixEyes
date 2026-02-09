package com.sixeyes.client.render.main.program.uniform.uniforms.sampler;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.GlTextureView;
import com.sixeyes.client.render.texture.GlTex;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.function.BiConsumer;

public record SamplerUniformUploader<T>(BiConsumer<SamplerUniform, T> uploadConsumer) {
    public static final SamplerUniformUploader<GlTex> GL_TEX = new SamplerUniformUploader<>(
            (samplerUniform, glTex) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.samplerId);
                GlStateManager._bindTexture(glTex.getTexId());
            }
    );

    public static final SamplerUniformUploader<GlTextureView> GL_TEXTURE_VIEW = new SamplerUniformUploader<>(
            (samplerUniform, glTextureView) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.samplerId);
                GlTexture glTexture = glTextureView.texture();
                int o;
                if ((glTexture.usage() & 16) != 0) {
                    o = 34067;
                    GL11.glBindTexture(34067, glTexture.glId());
                } else {
                    o = GlConst.GL_TEXTURE_2D;
                    GlStateManager._bindTexture(glTexture.glId());
                }

                GlStateManager._texParameter(o, 33084, glTextureView.baseMipLevel());
                GlStateManager._texParameter(o, GL12.GL_TEXTURE_MAX_LEVEL, glTextureView.baseMipLevel() + glTextureView.mipLevels() - 1);
            }
    );

    public static final SamplerUniformUploader<GlTexture> GL_TEXTURE = new SamplerUniformUploader<>(
            (samplerUniform, glTexture) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.samplerId);
                int o;
                if ((glTexture.usage() & 16) != 0) {
                    o = 34067;
                    GL11.glBindTexture(o, glTexture.glId());
                } else
                    GlStateManager._bindTexture(glTexture.glId());
            }
    );

    public static final SamplerUniformUploader<Integer> TEXTURE_ID = new SamplerUniformUploader<>(
            (samplerUniform, id) -> {
                GlStateManager._activeTexture(GlConst.GL_TEXTURE0 + samplerUniform.samplerId);
                GlStateManager._bindTexture(id);
            }
    );
}
