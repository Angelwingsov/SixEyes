package com.sixeyes.client.util.render.engine.dispatcher

import com.sixeyes.client.render.main.vertex.DrawMode
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import java.util.*

class RenderDispatcher {
    private val queues: Array<ArrayList<RenderBatch>> = Array(ClientRenderPipeline.entries.size) { ArrayList() }
    private val batchPool: ArrayDeque<RenderBatch> = ArrayDeque(32)

    fun getBuilder(
        pipeline: ClientRenderPipeline,
        renderer: Renderable,
        state: Any?
    ): MeshBuilder? {
        val queue = queues[pipeline.ordinal]

        if (queue.isNotEmpty()) {
            val lastBatch = queue[queue.size - 1]

            if (lastBatch.owner === renderer && renderer.isBatchCompatible(lastBatch.state, state)) {
                return lastBatch.builder
            }
        }

        val newBuilder = com.sixeyes.client.render.main.ChromaRenderer.acquireMeshBuilder(
            renderer.drawMode() ?: DrawMode.QUADS,
            renderer.vertexFormat()
        )
        val newBatch = if (batchPool.isNotEmpty()) {
            batchPool.removeLast().apply { set(renderer, newBuilder, state) }
        } else {
            RenderBatch(renderer, newBuilder, state)
        }
        queue.add(newBatch)

        return newBuilder
    }

    fun flushPipelines(vararg pipelines: ClientRenderPipeline) {
        flushPipelinesArray(pipelines)
    }

    fun flushPipelinesArray(pipelines: Array<out ClientRenderPipeline>) {
        for (pipe in pipelines) {
            val queue = queues[pipe.ordinal]
            val size = queue.size
            if (size == 0) continue

            for (i in 0 until size) {
                val batch = queue[i]
                batch.flush()
                if (batchPool.size < 64) {
                    batchPool.addLast(batch)
                }
            }
            queue.clear()
        }
    }

    fun flushAll() {
        for (pipe in ClientRenderPipeline.entries) {
            val queue = queues[pipe.ordinal]
            val size = queue.size
            if (size == 0) continue

            for (i in 0 until size) {
                val batch = queue[i]
                batch.flush()
                if (batchPool.size < 64) {
                    batchPool.addLast(batch)
                }
            }
            queue.clear()
        }
    }
}