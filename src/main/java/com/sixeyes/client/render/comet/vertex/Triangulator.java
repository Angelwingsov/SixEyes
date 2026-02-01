package com.sixeyes.client.render.comet.vertex;

import it.unimi.dsi.fastutil.ints.IntConsumer;

public interface Triangulator {

	void accept(IntConsumer indexConsumer, int firstVertexIndex);
}

