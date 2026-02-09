package com.sixeyes.client.util.render.engine.dispatcher

import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder

class RenderBatch(
    val owner: Renderable?,
    val builder: MeshBuilder?,
    val state: Any?
) {
    fun flush() {
        val mesh = builder?.buildNullable()
        
        mesh?.let {
            owner?.renderBatch(mesh, state)
        }
    }
}