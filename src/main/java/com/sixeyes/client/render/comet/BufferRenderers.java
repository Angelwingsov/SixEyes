package com.sixeyes.client.render.comet;

import com.sixeyes.client.render.comet.buffer.BufferTarget;
import com.sixeyes.client.render.comet.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.comet.exceptions.impl.WrongGpuBufferTargetException;
import com.sixeyes.client.render.comet.vertex.DrawMode;
import com.sixeyes.client.render.comet.vertex.IndexBufferGenerator;
import com.sixeyes.client.render.comet.vertex.format.uploader.VertexFormatManager;
import com.sixeyes.client.render.comet.vertex.mesh.IMesh;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.render.BuiltBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.util.function.BiConsumer;

public final class BufferRenderers {

    public static final BiConsumer<BuiltBuffer, Boolean> MINECRAFT_BUFFER = (builtBuffer, close) -> {
        BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();

        if (drawParameters.indexCount() > 0) {
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(drawParameters.mode());

            GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "CometRenderer vertex mesh", 40, builtBuffer.getBuffer());
            GpuBuffer indexBuffer = shapeIndexBuffer.getIndexBuffer(drawParameters.indexCount());
            VertexFormat.IndexType indexType = shapeIndexBuffer.getIndexType();

            ((GlBackend) RenderSystem.getDevice()).getVertexBufferManager().setupBuffer(
                    drawParameters.format(),
                    (GlGpuBuffer) vertexBuffer
            );
            GL15.glBindBuffer(GlConst.GL_ELEMENT_ARRAY_BUFFER, CometRenderer.getBufferIdGetter().apply((GlGpuBuffer) indexBuffer));
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
    public static final BiConsumer<IMesh, Boolean> COMET_BUFFER = (mesh, close) -> {
        int indexCount = mesh.getIndexCount();
        int vertexCount = mesh.getVertexCount();
        DrawMode drawMode = mesh.getDrawMode();

        if (vertexCount > 0) {
            VertexFormatManager.uploadFormatToBuffer(mesh.getVertexBuffer(), mesh.getVertexFormat());

            if (drawMode.useIndexBuffer()) {

                IndexBufferGenerator indexBufferGenerator = mesh.getDrawMode().indexBufferGenerator();

                com.sixeyes.client.render.comet.buffer.GpuBuffer indexBuffer = mesh.getIndexBuffer();
                if (indexBuffer.getTarget() != BufferTarget.ELEMENT_ARRAY_BUFFER)
                    ExceptionPrinter.printAndExit(new WrongGpuBufferTargetException(indexBuffer.getTarget().glId, BufferTarget.ELEMENT_ARRAY_BUFFER.glId));
                indexBuffer.bind();

                GL11.glDrawElements(drawMode.glId(), indexCount, indexBufferGenerator.getIndexType().glId, 0);
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


