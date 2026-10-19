package com.sixeyes.client.render.main.program.uniform.uniforms.sampler;

import com.mojang.blaze3d.opengl.GlTexture;
import com.sixeyes.client.render.texture.GlTex;
import com.sixeyes.client.render.main.program.GlProgram;
import com.sixeyes.client.render.main.program.uniform.GlUniform;
import com.mojang.blaze3d.opengl.GlTextureView;

public class SamplerUniform extends GlUniform {
    public final int samplerId;
    
    private int currentTexId = -1;
    private GlTex currentGlTex;
    private GlTextureView currentTextureView;
    private GlTexture currentGlTexture;
    private Runnable customRunnable;
    private int uploadMode = 0;

    public SamplerUniform(String name, int location, GlProgram glProgram) {
        super(name, location, glProgram);

        samplerId = glProgram.samplersAmount + 1;
        glProgram.samplersAmount = samplerId;
    }

    public void set(GlTex texture) {
        this.currentGlTex = texture;
        this.uploadMode = 1;
        this.program.addUpdatedUniform(this);
    }

    public void set(GlTextureView textureView) {
        this.currentTextureView = textureView;
        this.uploadMode = 2;
        this.program.addUpdatedUniform(this);
    }

    public void set(GlTexture textureView) {
        this.currentGlTexture = textureView;
        this.uploadMode = 3;
        this.program.addUpdatedUniform(this);
    }

    public void set(int textureId) {
        this.currentTexId = textureId;
        this.uploadMode = 4;
        this.program.addUpdatedUniform(this);
    }

    public <T> void set(SamplerUniformUploader<T> applier, T texture) {
        this.customRunnable = () -> applier.uploadConsumer().accept(this, texture);
        this.uploadMode = 5;
        this.program.addUpdatedUniform(this);
    }

    @Override
    public void upload() {
        switch (this.uploadMode) {
            case 1 -> {
                if (this.currentGlTex != null)
                    SamplerUniformUploader.GL_TEX.uploadConsumer().accept(this, this.currentGlTex);
            }
            case 2 -> {
                if (this.currentTextureView != null)
                    SamplerUniformUploader.GL_TEXTURE_VIEW.uploadConsumer().accept(this, this.currentTextureView);
            }
            case 3 -> {
                if (this.currentGlTexture != null)
                    SamplerUniformUploader.GL_TEXTURE.uploadConsumer().accept(this, this.currentGlTexture);
            }
            case 4 -> {
                SamplerUniformUploader.TEXTURE_ID.uploadConsumer().accept(this, this.currentTexId);
            }
            case 5 -> {
                if (this.customRunnable != null)
                    this.customRunnable.run();
            }
        }
    }
}
