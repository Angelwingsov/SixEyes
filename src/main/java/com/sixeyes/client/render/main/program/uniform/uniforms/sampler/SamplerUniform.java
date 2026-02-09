package com.sixeyes.client.render.main.program.uniform.uniforms.sampler;

import com.mojang.blaze3d.opengl.GlTexture;
import com.sixeyes.client.render.texture.GlTex;
import com.sixeyes.client.render.main.program.GlProgram;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTextureView;

public class SamplerUniform extends GlUniform {
    public final int samplerId;
    private Runnable uploadRunnable = null;

    public SamplerUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);

        samplerId = glProgram.samplersAmount + 1;
        glProgram.samplersAmount = samplerId;
    }


    public void set(GlTex texture) {
        this.uploadRunnable = () -> SamplerUniformUploader.GL_TEX.uploadConsumer().accept(this, texture);
        this.program.addUpdatedUniform(this);
    }


    public void set(GlTextureView textureView) {
        this.uploadRunnable = () -> SamplerUniformUploader.GL_TEXTURE_VIEW.uploadConsumer().accept(this, textureView);
        this.program.addUpdatedUniform(this);
    }

    public void set(GlTexture textureView) {
        this.uploadRunnable = () -> SamplerUniformUploader.GL_TEXTURE.uploadConsumer().accept(this, textureView);
        this.program.addUpdatedUniform(this);
    }

    public void set(int textureId) {
        this.uploadRunnable = () -> SamplerUniformUploader.TEXTURE_ID.uploadConsumer().accept(this, textureId);
        this.program.addUpdatedUniform(this);
    }


    public <T> void set(SamplerUniformUploader<T> applier, T texture) {
        this.uploadRunnable = () -> applier.uploadConsumer().accept(this, texture);
        this.program.addUpdatedUniform(this);
    }

    @Override
    public void upload() {
        if (this.uploadRunnable != null) {
            if (GlProgram.ACTIVE_PROGRAM == null)
                GlStateManager._glUniform1i(this.location, samplerId);


            this.uploadRunnable.run();
        }
    }
}
