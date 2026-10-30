package com.sixeyes.client.util.render.engine.dispatcher

import com.sixeyes.client.render.main.ChromaRenderer
import com.sixeyes.client.render.main.vertex.DrawMode
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import java.util.ArrayDeque

class RenderDispatcher {
    private val queues: Array<ArrayList<RenderBatch>> = Array(ClientRenderPipeline.entries.size) { ArrayList() }
    private val batchPool: ArrayDeque<RenderBatch> = ArrayDeque(32)
    private var activeMask: Int = 0

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

        val newBuilder = ChromaRenderer.acquireMeshBuilder(
            renderer.drawMode() ?: DrawMode.QUADS,
            renderer.vertexFormat()
        )
        val newBatch = if (batchPool.isNotEmpty()) {
            batchPool.removeLast().apply { set(renderer, newBuilder, state) }
        } else {
            RenderBatch(renderer, newBuilder, state)
        }
        queue.add(newBatch)
        activeMask = activeMask or (1 shl pipeline.ordinal)

        return newBuilder
    }

    fun flushPipelines(vararg pipelines: ClientRenderPipeline) {
        flushPipelinesArray(pipelines)
    }

    fun flushPipelinesArray(pipelines: Array<out ClientRenderPipeline>) {
        if (activeMask == 0) return
        for (pipe in pipelines) {
            val bit = 1 shl pipe.ordinal
            if ((activeMask and bit) != 0) {
                flushQueue(queues[pipe.ordinal])
                activeMask = activeMask and bit.inv()
            }
        }
    }

    fun flushAll() {
        if (activeMask == 0) return
        for (pipe in ClientRenderPipeline.entries) {
            val bit = 1 shl pipe.ordinal
            if ((activeMask and bit) != 0) {
                flushQueue(queues[pipe.ordinal])
            }
        }
        activeMask = 0
    }

    private fun flushQueue(queue: ArrayList<RenderBatch>) {
        val size = queue.size
        if (size == 0) return

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
