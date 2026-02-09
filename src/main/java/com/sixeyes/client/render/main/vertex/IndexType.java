package com.sixeyes.client.render.main.vertex;

import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL11;

@RequiredArgsConstructor
public enum IndexType {
	SHORT(2, GL11.GL_UNSIGNED_SHORT),
	INT(4, GL11.GL_UNSIGNED_INT);

	public final int bytes;
	public final int glId;

	public static IndexType smallestFor(int i) {
		return (i & -65536) != 0 ? INT : SHORT;
	}
}