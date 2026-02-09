package com.sixeyes.client.render.texture.texture;

import com.sixeyes.client.render.texture.GLTextureSystem;
import com.sixeyes.client.render.texture.GlTex;
import com.sixeyes.client.render.texture.builder.GLTextureInfo;
import com.sixeyes.client.render.texture.controller.GlController;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.util.Random;

import static org.lwjgl.stb.STBImage.nstbi_image_free;


public class GLTexture implements GlTex {
    protected int texId;

    protected final String name;
    protected int width;
    protected int height;

    protected TextureFiltering filtering;
    protected TextureWrapping wrapping;
    protected ColorMode colorMode;

    protected GLTexture(String name) {
        this.name = name;
        GLTextureSystem.addTexture(this);
    }

    private GLTexture create(GLTextureInfo glTextureInfo) {
        GlController controller = GLTextureSystem.getGlController();

        controller.run(() -> {
            texId = controller.genTexId();

            long bufferAddress = MemoryUtil.memAddress(glTextureInfo.getPixels());

            this.width = glTextureInfo.getWidth();
            this.height = glTextureInfo.getHeight();

            controller.bindTexture(texId);
            controller.texParameter(GL11.GL_TEXTURE_2D, 33085, 0);
            controller.texParameter(GL11.GL_TEXTURE_2D, 33082, 0);
            controller.texParameter(GL11.GL_TEXTURE_2D, 33083, 0);
            controller.texParameter(GL11.GL_TEXTURE_2D, 34049, 0.0F);
            applyFiltering(controller, glTextureInfo.getFiltering());
            applyWrapping(controller, glTextureInfo.getWrapping());
            this.colorMode = glTextureInfo.getColorMode();

            controller.texImage2D(GL11.GL_TEXTURE_2D, 0, glTextureInfo.getColorMode().glId, width, height, 0, glTextureInfo.getColorMode().glId, 5121, null);
            controller.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
            controller.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            controller.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
            controller.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);
            controller.texSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, glTextureInfo.getColorMode().glId, 5121, bufferAddress);

            controller.bindTexture(0);

            if (glTextureInfo.isUsingStb())
                nstbi_image_free(bufferAddress);
            else
                MemoryUtil.memFree(glTextureInfo.getPixels());
        });

        return this;
    }

    private void applyFiltering(GlController controller, TextureFiltering textureFiltering) {
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, textureFiltering.id);
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, textureFiltering.id);
        this.filtering = textureFiltering;
    }

    private void applyWrapping(GlController controller, TextureWrapping textureWrapping) {
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, textureWrapping.id);
        controller.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, textureWrapping.id);
        this.wrapping = textureWrapping;
    }

    public GLTexture subTexture(float u1, float v1, float u2, float v2) {
        GlController controller = GLTextureSystem.getGlController();
        GLTexture texture = new GLTexture(name.concat(String.format("_sub_%s", new Random().nextInt())));

        controller.run(() -> {
            texture.texId = controller.genTexId();

            int fbo = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, this.texId, 0);

            if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
                throw new RuntimeException(String.format("An error occurred while creating FrameBuffer for subtexture '%s'.", texture.getName()));

            texture.width = (int) ((u2 - u1) * width);
            texture.height = (int) ((v2 - v1) * height);

            controller.bindTexture(texture.texId);
            controller.texParameter(GL11.GL_TEXTURE_2D, 33085, 0);
            controller.texParameter(GL11.GL_TEXTURE_2D, 33082, 0);
            controller.texParameter(GL11.GL_TEXTURE_2D, 33083, 0);
            controller.texParameter(GL11.GL_TEXTURE_2D, 34049, 0.0F);
            texture.applyFiltering(controller, filtering);
            texture.applyWrapping(controller, wrapping);
            texture.colorMode = colorMode;

            controller.texImage2D(GL11.GL_TEXTURE_2D, 0, texture.colorMode.glId, texture.width, texture.height, 0, texture.colorMode.glId, 5121, null);
            controller.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0);
            controller.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            controller.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
            controller.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4);

            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0,
                    0, 0,
                    (int) (u1 * width), (int) (v1 * height),
                    texture.width, texture.height);

            controller.bindTexture(0);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            GL30.glDeleteFramebuffers(fbo);
        });

        return texture;
    }

    
    @Override
    public void delete() {
        GLTextureSystem.getGlController().deleteTexture(texId);
        GLTextureSystem.removeTexture(this);
    }

    @Override
    public void bind() {
        GLTextureSystem.getGlController().bindTexture(getTexId());
    }

    @Override
    public void unBind() {
        GLTextureSystem.getGlController().bindTexture(0);
    }

    public String getName() {
        return name;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getTexId() {
        return this.texId;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public static GLTexture of(String name, GLTextureInfo glTextureInfo) {
        return new GLTexture(name).create(glTextureInfo);
    }
}
