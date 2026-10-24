package com.sixeyes.client.render.main.vertex.format.uploader;

import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.vertex.element.VertexElement;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;
import com.sixeyes.client.render.main.vertex.format.VertexFormatBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultVertexFormatBufferUploader extends VertexFormatBufferUploader {
    @Override
    public void applyFormatToBuffer(GpuBuffer vertexBuffer, VertexFormat vertexFormat) {
        VertexFormatBuffer vertexFormatBuffer = vertexFormat.getVertexFormatBufferOrCreate(() -> createVertexFormatBuffer(vertexFormat));

        GL30.glBindVertexArray(vertexFormatBuffer.glId());
        vertexBuffer.bind();
        setupBuffer(vertexFormat);
        vertexFormatBuffer.buffer().set(vertexBuffer);
    }

    @Override
    public VertexFormatBuffer createVertexFormatBuffer(VertexFormat vertexFormat) {
        int i = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(i);
        List<VertexElement> list = vertexFormat.vertexElements;
        for (int j = 0; j < list.size(); j++) {
            GL30.glEnableVertexAttribArray(j);
        }
        return new VertexFormatBuffer(i, vertexFormat, new AtomicReference<>());
    }

    private void setupBuffer(VertexFormat format) {
        int i = format.vertexSize;
        List<VertexElement> list = format.vertexElements;

        for (int j = 0; j < list.size(); j++) {
            VertexElement vertexElement = list.get(j);

            if (vertexElement.getType().glId() == GL11.GL_FLOAT) {
                GL30.glVertexAttribPointer(
                        j, vertexElement.getCount(), vertexElement.getType().glId(), false, i, format.getElementOffset(vertexElement)
                );
            } else {
                GL30.glVertexAttribIPointer(j, vertexElement.getCount(), vertexElement.getType().glId(), i, format.getElementOffset(vertexElement));
            }
        }
    }
}
