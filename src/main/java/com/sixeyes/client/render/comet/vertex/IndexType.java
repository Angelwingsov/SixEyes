package com.sixeyes.client.render.comet.vertex;

import org.lwjgl.opengl.GL11;

public enum IndexType {
	SHORT(2, GL11.GL_UNSIGNED_SHORT),
	INT(4, GL11.GL_UNSIGNED_INT);


	public final int bytes;

	public final int glId;


	IndexType(int bytes, int glId) {
		this.bytes = bytes;
		this.glId = glId;
	}


	public static IndexType smallestFor(int i) {
		return (i & -65536) != 0 ? INT : SHORT;
	}
}

