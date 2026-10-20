package com.sixeyes.client.render.main;

import com.sixeyes.client.render.main.buffer.BufferTarget;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.WrongGpuBufferTargetException;
import com.sixeyes.client.render.main.vertex.DrawMode;
import com.sixeyes.client.render.main.vertex.IndexBufferGenerator;
import com.sixeyes.client.render.main.vertex.format.uploader.VertexFormatManager;
import com.sixeyes.client.render.main.vertex.mesh.IMesh;
import com.mojang.blaze3d.vertex.MeshData;
import com.sixeyes.client.util.render.engine.dispatcher.RenderMetrics;
import org.lwjgl.opengl.GL11;

import java.util.function.BiConsumer;

public final class BufferRenderers {
    public static final BiConsumer<MeshData, Boolean> MINECRAFT_BUFFER = (builtBuffer, close) -> {
        if (close)
            builtBuffer.close();
        throw new UnsupportedOperationException("MINECRAFT_BUFFER is not supported in 26.3, use CUSTOM_BUFFER");
    };

    public static final BiConsumer<IMesh, Boolean> CUSTOM_BUFFER = (mesh, close) -> {
        int indexCount = mesh.getIndexCount();
        int vertexCount = mesh.getVertexCount();
        DrawMode drawMode = mesh.getDrawMode();

        if (vertexCount > 0) {
            RenderMetrics.INSTANCE.onDrawCall(vertexCount, indexCount);
            VertexFormatManager.uploadFormatToBuffer(mesh.getVertexBuffer(), mesh.getVertexFormat());

            if (drawMode.useIndexBuffer()) {

                IndexBufferGenerator indexBufferGenerator = mesh.getDrawMode().indexBufferGenerator();

                com.sixeyes.client.render.main.buffer.GpuBuffer indexBuffer = mesh.getIndexBuffer();
                if (indexBuffer.target != BufferTarget.ELEMENT_ARRAY_BUFFER)
                    ExceptionPrinter.printAndExit(new WrongGpuBufferTargetException(indexBuffer.target.glId, BufferTarget.ELEMENT_ARRAY_BUFFER.glId));
                indexBuffer.bind();

                GL11.glDrawElements(drawMode.glId(), indexCount, indexBufferGenerator.indexType.glId, 0);
            } else
                GL11.glDrawArrays(drawMode.glId(), 0, vertexCount);
        }
        if (close)
            mesh.close();
    };
}
