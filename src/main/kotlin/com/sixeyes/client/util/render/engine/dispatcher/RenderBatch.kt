package com.sixeyes.client.util.render.engine.dispatcher

import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder

class RenderBatch(
    var owner: Renderable? = null,
    var builder: MeshBuilder? = null,
    var state: Any? = null
) {
    fun set(owner: Renderable?, builder: MeshBuilder?, state: Any?) {
        this.owner = owner
        this.builder = builder
        this.state = state
    }

    fun flush() {
        RenderMetrics.onFlush()
        try {
            val mesh = builder?.buildNullable()
            mesh?.let {
                owner?.renderBatch(mesh, state)
            }
        } finally {
            builder?.let {
                ChromaRenderer.releaseMeshBuilder(it)
            }
            this.owner = null
            this.builder = null
            this.state = null
        }
    }
}