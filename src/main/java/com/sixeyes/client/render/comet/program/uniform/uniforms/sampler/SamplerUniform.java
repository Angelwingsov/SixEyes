package com.sixeyes.client.render.comet.program.uniform.uniforms.sampler;

import com.sixeyes.client.render.texture.GlTex;
import com.sixeyes.client.render.comet.program.GlProgram;
import com.sixeyes.client.render.comet.program.uniform.GlUniform;
import com.sixeyes.client.render.comet.program.uniform.UniformType;
import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.texture.GlTextureView;

public class SamplerUniform extends GlUniform {

    private final int samplerId;

    private Runnable uploadRunnable = null;


    public SamplerUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);

        this.samplerId = glProgram.getSamplersAmount() + 1;
        glProgram.setSamplersAmount(this.samplerId);
    }


    public void set(GlTex texture) {
        this.uploadRunnable = () -> SamplerUniformUploader.GL_TEX.uploadConsumer().accept(this, texture);
        this.program.addUpdatedUniform(this);
    }


    public void set(GlTextureView textureView) {
        this.uploadRunnable = () -> SamplerUniformUploader.GL_TEXTURE_VIEW.uploadConsumer().accept(this, textureView);
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


    public int getSamplerId() {
        return this.samplerId;
    }

    @Override
    public void upload() {
        if (this.uploadRunnable != null) {
            if (GlProgram.ACTIVE_PROGRAM == null)
                GlStateManager._glUniform1i(this.location, getSamplerId());


            this.uploadRunnable.run();
        }
    }
}


