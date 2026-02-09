package com.sixeyes.client.util.render.engine.dispatcher

import com.sixeyes.client.util.render.engine.controls.ClientRenderPipeline
import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.render.main.vertex.mesh.Mesh
import com.sixeyes.client.render.main.vertex.mesh.MeshBuilder
import java.util.*

class RenderDispatcher {
    private val queues: MutableMap<ClientRenderPipeline, MutableList<RenderBatch>> =
        EnumMap(ClientRenderPipeline::class.java)

    init {
        for (pipe in ClientRenderPipeline.entries) {
            queues[pipe] = ArrayList()
        }
    }

    /**
     * Получает MeshBuilder для текущего запроса.
     * Если последний батч в очереди совместим (тот же рендерер и то же состояние), возвращает его билдер.
     * Иначе создает новый батч.
     */
    fun getBuilder(
        pipeline: ClientRenderPipeline,
        renderer: Renderable,
        state: Any?
    ): MeshBuilder? {
        val queue = queues[pipeline]!!

        if (!queue.isEmpty()) {
            val lastBatch: RenderBatch = queue.last()

            if (lastBatch.owner === renderer && renderer.isBatchCompatible(lastBatch.state, state)) {
                return lastBatch.builder
            }
        }

        val newBuilder = Mesh.builder(renderer.drawMode(), renderer.vertexFormat())
        val newBatch = RenderBatch(renderer, newBuilder, state)
        queue.add(newBatch)

        return newBuilder
    }

    fun flushPipelines(vararg pipelines: ClientRenderPipeline) {
        for (pipe in pipelines) {
            val queue: MutableList<RenderBatch> = queues[pipe]!!
            for (batch in queue) {
                batch.flush()
            }
            queue.clear()
        }
    }

    fun flushAll() {
        flushPipelines(*ClientRenderPipeline.entries.toTypedArray())
    }
}