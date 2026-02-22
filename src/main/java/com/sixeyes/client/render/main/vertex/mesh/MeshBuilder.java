package com.sixeyes.client.render.main.vertex.mesh;

import com.sixeyes.client.render.main.exceptions.ExceptionPrinter;
import com.sixeyes.client.render.main.exceptions.impl.vertex.BadVertexStructureException;
import com.sixeyes.client.render.main.exceptions.impl.vertex.IllegalMeshBuilderStateException;
import com.sixeyes.client.render.main.exceptions.impl.vertex.VertexOverflowException;
import com.sixeyes.client.render.main.vertex.DrawMode;
import com.sixeyes.client.render.main.vertex.element.VertexElement;
import com.sixeyes.client.render.main.vertex.element.VertexElementType;
import com.sixeyes.client.render.main.vertex.format.VertexFormat;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.stream.Collectors;

public class MeshBuilder implements IMeshBuilder<MeshBuilder, Mesh> {
    public static final int MAX_VERTICES = 16777215;

    public ByteBufferBuilder bufferAllocator;
    private DrawMode drawMode;
    private VertexFormat vertexFormat;
    
    private int vertexSize;
    private int[] elementOffsets;
    private int requiredMask;
    
    private boolean closeAllocatorAfterBuild;
    private long vertexPointer = -1L;
    private int vertexCount;
    private int currentMask;
    
    private boolean closed = false;
    
    public MeshBuilder(ByteBufferBuilder bufferAllocator, DrawMode drawMode, VertexFormat vertexFormat, boolean closeAllocatorAfterBuild) {
        this.bufferAllocator = bufferAllocator;
        this.drawMode = drawMode;
        this.vertexFormat = vertexFormat;
        this.vertexSize = vertexFormat.vertexSize;
        this.elementOffsets = vertexFormat.elementOffsets;
        this.requiredMask = vertexFormat.elementsMask & ~1;
        this.closeAllocatorAfterBuild = closeAllocatorAfterBuild;
    }

    public MeshBuilder set(ByteBufferBuilder bufferAllocator, DrawMode drawMode, VertexFormat vertexFormat, boolean closeAllocatorAfterBuild) {
        this.bufferAllocator = bufferAllocator;
        this.drawMode = drawMode;
        this.vertexFormat = vertexFormat;
        this.vertexSize = vertexFormat.vertexSize;
        this.elementOffsets = vertexFormat.elementOffsets;
        this.requiredMask = vertexFormat.elementsMask & ~1;
        this.closeAllocatorAfterBuild = closeAllocatorAfterBuild;

        this.closed = false;
        this.vertexCount = 0;
        this.vertexPointer = -1L;
        this.currentMask = 0;

        return this;
    }
    
    private void ensureBuilding() {
        if (this.closed) {
            ExceptionPrinter.printAndExit(new IllegalMeshBuilderStateException(
                    "Attempt to interact with MeshBuilder, which does not building.",
                    new String[]{
                            "You are trying to use MeshBuilder after it has been built."
                    },
                    new String[]{
                            "Check your build or usage MeshBuilder method and fix it."
                    }
            ));
        }
    }

    @Override
    public Mesh buildNullable() {
        this.ensureBuilding();
        this.endVertex();
        Mesh builtBuffer = this.build();
        bufferAllocator.close();
        this.closed = true;
        this.vertexPointer = -1L;
        return builtBuffer;
    }

    @Override
    public Mesh buildOrThrow() {
        Mesh builtBuffer = this.buildNullable();
        if (builtBuffer == null) {
            ExceptionPrinter.printAndExit(new IllegalMeshBuilderStateException(
                    "MeshBuilder was empty.",
                    new String[]{
                            "You haven't built any vertices in MeshBuilder and called MeshBuilder build via the 'buildThrowable' method, which throw an exception about the MeshBuilder being empty."
                    },
                    new String[]{
                            "If your rendering method assumes an empty MeshBuilder, call the builder via the 'buildNullable' method. If not, check your MeshBuilder method and fix it."
                    }
            ));
            return null;
        } else {
            return builtBuffer;
        }
    }
    
    private Mesh build() {
        if (this.vertexCount == 0) {
            return null;
        } else {
            ByteBufferBuilder.Result closeableBuffer = this.bufferAllocator.build();
            if (closeableBuffer == null) {
                return null;
            } else {
                int i = this.drawMode.indexCountFunction().apply(this.vertexCount);
                return new Mesh(closeableBuffer.byteBuffer(), this.vertexFormat, this.vertexCount, i, this.drawMode, () -> {
                    if (this.closeAllocatorAfterBuild)
                        this.bufferAllocator.close();
                });
            }
        }
    }

    private long beginVertex() {
        this.ensureBuilding();
        this.endVertex();
        if (this.vertexCount >= MAX_VERTICES) {
            ExceptionPrinter.printAndExit(new VertexOverflowException());
            return -1L;
        } else {
            this.vertexCount++;
            long l = this.bufferAllocator.reserve(this.vertexSize);
            this.vertexPointer = l;
            return l;
        }
    }

    private void endVertex() {
        if (this.vertexCount != 0 && this.currentMask != 0) {
            String string = vertexFormat.getElementsFromMask(this.currentMask).map(this.vertexFormat::getVertexElementName).collect(Collectors.joining(", "));
            ExceptionPrinter.printAndExit(new BadVertexStructureException(string));
        }
    }

    private long beginElement(VertexElement element) {
        int i = this.currentMask;
        int j = i & ~element.mask();
        if (j == i) {
            return -1L;
        } else {
            this.currentMask = j;
            long l = this.vertexPointer;
            if (l == -1L) {
                ExceptionPrinter.printAndExit(new IllegalMeshBuilderStateException(
                        "Not currently building vertex.",
                        new String[]{
                                "You are trying to add data to vertex that has already been built."
                        },
                        new String[]{
                                "Check your vertex building method and fix it."
                        }
                ));
                return -1L;
            } else {
                return l + this.elementOffsets[element.getId()];
            }
        }
    }

    @Override
    public MeshBuilder vertex(float x, float y, float z) {
        long l = this.beginVertex() + this.elementOffsets[0];
        this.currentMask = this.requiredMask;
        MemoryUtil.memPutFloat(l, x);
        MemoryUtil.memPutFloat(l + 4L, y);
        MemoryUtil.memPutFloat(l + 8L, z);
        return this;
    }

    private final Vector3f tempVector = new Vector3f();

    @Override
    public MeshBuilder vertex(Matrix4f matrix4f, float x, float y, float z) {
        matrix4f.transformPosition(x, y, z, tempVector);
        return vertex(tempVector.x, tempVector.y, tempVector.z);
    }

    @Override
    public <T> MeshBuilder element(String name, VertexElementType<T> elementType, T... values) {
        return element(vertexFormat.getVertexElement(name), elementType, values);
    }

    public <T> MeshBuilder element(VertexElement element, VertexElementType<T> elementType, T... values) {
        long pointer = beginElement(element);
        if (pointer != -1L)
            elementType.uploadConsumer().accept(pointer, values);

        return this;
    }

    public MeshBuilder element2f(VertexElement element, float v0, float v1) {
        long pointer = beginElement(element);
        if (pointer != -1L) {
            MemoryUtil.memPutFloat(pointer, v0);
            MemoryUtil.memPutFloat(pointer + 4L, v1);
        }
        return this;
    }

    public MeshBuilder element3f(VertexElement element, float v0, float v1, float v2) {
        long pointer = beginElement(element);
        if (pointer != -1L) {
            MemoryUtil.memPutFloat(pointer, v0);
            MemoryUtil.memPutFloat(pointer + 4L, v1);
            MemoryUtil.memPutFloat(pointer + 8L, v2);
        }
        return this;
    }

    public MeshBuilder element4f(VertexElement element, float v0, float v1, float v2, float v3) {
        long pointer = beginElement(element);
        if (pointer != -1L) {
            MemoryUtil.memPutFloat(pointer, v0);
            MemoryUtil.memPutFloat(pointer + 4L, v1);
            MemoryUtil.memPutFloat(pointer + 8L, v2);
            MemoryUtil.memPutFloat(pointer + 12L, v3);
        }
        return this;
    }
}
