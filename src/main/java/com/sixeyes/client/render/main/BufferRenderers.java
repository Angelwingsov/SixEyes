package com.sixeyes.client.render.main;

import com.sixeyes.client.render.main.buffer.BufferTarget;
import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.WrongGpuBufferTargetException;
import com.sixeyes.client.render.main.vertex.DrawMode;
import com.sixeyes.client.render.main.vertex.IndexBufferGenerator;
import com.sixeyes.client.render.main.vertex.format.uploader.VertexFormatManager;
import com.sixeyes.client.render.main.vertex.mesh.IMesh;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.function.BiConsumer;

public final class BufferRenderers {
    public static final BiConsumer<MeshData, Boolean> MINECRAFT_BUFFER = (builtBuffer, close) -> {
        MeshData.DrawState drawParameters = builtBuffer.drawState();

        if (drawParameters.indexCount() > 0) {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(drawParameters.mode());

            GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Renderer vertex mesh", 40, builtBuffer.vertexBuffer());
            GpuBuffer indexBuffer = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            VertexFormat.IndexType indexType = shapeIndexBuffer.type();

            ((GlDevice) RenderSystem.getDevice()).vertexArrayCache().bindVertexArray(
                    drawParameters.format(),
                    (GlBuffer) vertexBuffer
            );
            GL15.glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, ChromaRenderer.bufferIdGetter.apply((GlBuffer) indexBuffer));
            drawIndexed(
                    drawParameters.indexCount(),
                    GlConst.toGl(drawParameters.mode()),
                    GlConst.toGl(indexType)
            );

            vertexBuffer.close();
        }
        if (close)
            builtBuffer.close();
    };

    public static final BiConsumer<IMesh, Boolean> CUSTOM_BUFFER = (mesh, close) -> {
        int indexCount = mesh.getIndexCount();
        int vertexCount = mesh.getVertexCount();
        DrawMode drawMode = mesh.getDrawMode();

        if (vertexCount > 0) {
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

    private static void drawIndexed(int count, int drawMode, int indexType) {
        GL11.glDrawElements(drawMode, count, indexType, 0);
    }
}
