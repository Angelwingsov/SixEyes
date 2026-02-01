package com.ferra13671.cometrenderer.vertex;

import com.ferra13671.cometrenderer.buffer.BufferTarget;
import com.ferra13671.cometrenderer.buffer.BufferUsage;
import com.ferra13671.cometrenderer.buffer.GpuBuffer;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public final class IndexBufferGenerator {


	private final int vertexCountInShape;

	private final int vertexCountInTriangulated;

	private final Triangulator triangulator;

	private GpuBuffer indexBuffer;

	private IndexType indexType = IndexType.SHORT;

	private int size;

	public IndexBufferGenerator(int vertexCountInShape, int vertexCountInTriangulated, Triangulator triangulator) {
		this.vertexCountInShape = vertexCountInShape;
		this.vertexCountInTriangulated = vertexCountInTriangulated;
		this.triangulator = triangulator;
	}


	public boolean isLargeEnough(int requiredSize) {
		return requiredSize <= this.size;
	}


	public GpuBuffer getIndexBuffer(int requiredSize, boolean standalone) {
		if (standalone)
			return generateIndexBuffer(requiredSize);
		else {
			if (!this.isLargeEnough(requiredSize)) {
				if (this.indexBuffer != null)
					this.indexBuffer.close();

				this.indexBuffer = generateIndexBuffer(requiredSize);
				this.size = requiredSize;
			}
			return this.indexBuffer;
		}
	}

	private GpuBuffer generateIndexBuffer(int requiredSize) {
		requiredSize = MathHelper.roundUpToMultiple(requiredSize * 2, this.vertexCountInTriangulated);
		int i = requiredSize / this.vertexCountInTriangulated;
		IndexType indexType = IndexType.smallestFor(
				i * this.vertexCountInShape
		);
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(
				MathHelper.roundUpToMultiple(requiredSize * indexType.bytes, 4)
		);

		try {
			this.indexType = indexType;
			IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);

			for (int l = 0; l < requiredSize; l += this.vertexCountInTriangulated)
				this.triangulator.accept(intConsumer, l * this.vertexCountInShape / this.vertexCountInTriangulated);

			byteBuffer.flip();

			return new GpuBuffer(byteBuffer, BufferUsage.STATIC_DRAW, BufferTarget.ELEMENT_ARRAY_BUFFER);
		} finally {
			MemoryUtil.memFree(byteBuffer);
		}
	}

	private IntConsumer getIndexConsumer(ByteBuffer indexBuffer) {
		if (this.indexType == IndexType.SHORT)
			return index -> indexBuffer.putShort((short) index);
		else
			return indexBuffer::putInt;
	}


	public IndexType getIndexType() {
		return this.indexType;
	}
}