package com.sixeyes.client.render.comet.vertex.format.uploader;

import com.sixeyes.client.render.comet.buffer.BufferTarget;
import com.sixeyes.client.render.comet.buffer.GpuBuffer;
import com.sixeyes.client.render.comet.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.comet.exceptions.impl.WrongGpuBufferTargetException;
import com.sixeyes.client.render.comet.vertex.format.VertexFormat;
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


