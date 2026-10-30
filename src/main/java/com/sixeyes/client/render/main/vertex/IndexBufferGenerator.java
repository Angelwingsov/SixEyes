package com.sixeyes.client.render.main.vertex;

import com.sixeyes.client.render.main.buffer.BufferTarget;
import com.sixeyes.client.render.main.buffer.BufferUsage;
import com.sixeyes.client.render.main.buffer.GpuBuffer;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Mth;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public final class IndexBufferGenerator {
	private final int vertexCountInShape;
	private final int vertexCountInTriangulated;
	private final Triangulator triangulator;

	private GpuBuffer indexBuffer;
	public IndexType indexType = IndexType.SHORT;
	private int size;

	public boolean isLargeEnough(int requiredSize) {
		return requiredSize <= this.size;
	}

	public GpuBuffer getIndexBuffer(int requiredSize, boolean standalone) {
		if (standalone) {
			return generateIndexBuffer(requiredSize);
		} else {
			if (!this.isLargeEnough(requiredSize)) {
				if (this.indexBuffer != null)
					this.indexBuffer.close();

				int targetCapacity = Math.max(requiredSize * 2, 24576);
				this.indexBuffer = generateIndexBuffer(targetCapacity);
				this.size = targetCapacity;
			}
			return this.indexBuffer;
		}
	}

	private GpuBuffer generateIndexBuffer(int requiredSize) {
		requiredSize = Mth.roundToward(requiredSize, this.vertexCountInTriangulated);
		int i = requiredSize / this.vertexCountInTriangulated;
		IndexType indexType = IndexType.smallestFor(
				i * this.vertexCountInShape
		);
		ByteBuffer byteBuffer = MemoryUtil.memAlloc(
				Mth.roundToward(requiredSize * indexType.bytes, 4)
		);

		try {
			this.indexType = indexType;
			if (this.indexType == IndexType.SHORT) {
				IntConsumer consumer = index -> byteBuffer.putShort((short) index);
				for (int l = 0; l < requiredSize; l += this.vertexCountInTriangulated) {
					int base = l * this.vertexCountInShape / this.vertexCountInTriangulated;
					this.triangulator.accept(consumer, base);
				}
			} else {
				IntConsumer consumer = byteBuffer::putInt;
				for (int l = 0; l < requiredSize; l += this.vertexCountInTriangulated) {
					int base = l * this.vertexCountInShape / this.vertexCountInTriangulated;
					this.triangulator.accept(consumer, base);
				}
			}

			byteBuffer.flip();

			return new GpuBuffer(byteBuffer, BufferUsage.STATIC_DRAW, BufferTarget.ELEMENT_ARRAY_BUFFER);
		} finally {
			MemoryUtil.memFree(byteBuffer);
		}
	}
}
