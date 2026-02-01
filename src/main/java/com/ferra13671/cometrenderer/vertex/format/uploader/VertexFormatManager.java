package com.ferra13671.cometrenderer.vertex.format.uploader;

import com.ferra13671.cometrenderer.buffer.BufferTarget;
import com.ferra13671.cometrenderer.buffer.GpuBuffer;
import com.ferra13671.cometrenderer.exceptions.ExceptionPrinter;
import com.ferra13671.cometrenderer.exceptions.impl.WrongGpuBufferTargetException;
import com.ferra13671.cometrenderer.vertex.format.VertexFormat;
import org.lwjgl.opengl.GL;

public class VertexFormatManager {
    
    private static final VertexFormatBufferUploader formatUploader =
            GL.getCapabilities().GL_ARB_vertex_attrib_binding ?
                    new ARBVertexFormatBufferUploader()
                    :
                    new DefaultVertexFormatBufferUploader();

    
    public static void uploadFormatToBuffer(GpuBuffer vertexBuffer, VertexFormat vertexFormat) {
        if (vertexBuffer.getTarget() != BufferTarget.ARRAY_BUFFER)
            ExceptionPrinter.printAndExit(new WrongGpuBufferTargetException(vertexBuffer.getTarget().glId, BufferTarget.ARRAY_BUFFER.glId));

        formatUploader.applyFormatToBuffer(vertexBuffer, vertexFormat);
    }
}
