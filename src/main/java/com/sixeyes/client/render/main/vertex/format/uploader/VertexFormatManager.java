package com.sixeyes.client.render.main.vertex.format.uploader;

import com.sixeyes.client.render.main.buffer.BufferTarget;
import com.sixeyes.client.render.main.buffer.GpuBuffer;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.WrongGpuBufferTargetException;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;
import org.lwjgl.opengl.GL;

public class VertexFormatManager {
    private static final VertexFormatBufferUploader formatUploader =
            GL.getCapabilities().GL_ARB_vertex_attrib_binding ?
                    new ARBVertexFormatBufferUploader()
                    :
                    new DefaultVertexFormatBufferUploader();

    
    public static void uploadFormatToBuffer(GpuBuffer vertexBuffer, VertexFormat vertexFormat) {
        if (vertexBuffer.target != BufferTarget.ARRAY_BUFFER)
            ExceptionPrinter.printAndExit(new WrongGpuBufferTargetException(vertexBuffer.target.glId, BufferTarget.ARRAY_BUFFER.glId));

        formatUploader.applyFormatToBuffer(vertexBuffer, vertexFormat);
    }
}
