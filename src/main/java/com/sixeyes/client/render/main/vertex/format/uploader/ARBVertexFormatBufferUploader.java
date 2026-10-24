package com.sixeyes.client.render.main.vertex.format.uploader;

import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.vertex.element.VertexElement;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;
import com.sixeyes.client.render.main.vertex.format.VertexFormatBuffer;
import org.lwjgl.opengl.ARBVertexAttribBinding;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ARBVertexFormatBufferUploader extends VertexFormatBufferUploader {

    protected ARBVertexFormatBufferUploader() {
    }

    @Override
    public void applyFormatToBuffer(GpuBuffer vertexBuffer, VertexFormat vertexFormat) {
        VertexFormatBuffer vertexFormatBuffer = vertexFormat.getVertexFormatBufferOrCreate(() -> createVertexFormatBuffer(vertexFormat));

        GL30.glBindVertexArray(vertexFormatBuffer.glId());
        ARBVertexAttribBinding.glBindVertexBuffer(0, vertexBuffer.id, 0L, vertexFormat.vertexSize);
        vertexFormatBuffer.buffer().set(vertexBuffer);
    }

    @Override
    public VertexFormatBuffer createVertexFormatBuffer(VertexFormat vertexFormat) {
        int vertBuffId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertBuffId);

        List<VertexElement> vertexElements = vertexFormat.vertexElements;

        for (int i = 0; i < vertexElements.size(); i++) {
            VertexElement vertexElement = vertexElements.get(i);
            GL30.glEnableVertexAttribArray(i);

            if (vertexElement.getType().glId() == GL11.GL_FLOAT) {
                ARBVertexAttribBinding.glVertexAttribFormat(
                        i, vertexElement.getCount(), vertexElement.getType().glId(), false, vertexFormat.getElementOffset(vertexElement)
                );
            } else {
                ARBVertexAttribBinding.glVertexAttribIFormat(
                        i, vertexElement.getCount(), vertexElement.getType().glId(), vertexFormat.getElementOffset(vertexElement)
                );
            }

            ARBVertexAttribBinding.glVertexAttribBinding(i, 0);
        }

        return new VertexFormatBuffer(vertBuffId, vertexFormat, new AtomicReference<>());
    }
}
