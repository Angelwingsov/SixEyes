package com.sixeyes.client.util.render.engine.controls

import com.sixeyes.client.util.render.engine.Renderable
import com.sixeyes.client.util.render.engine.dispatcher.RenderDispatcher

object LayerControl {
    private val renderers: MutableList<Renderable> = ArrayList()
    private val dispatcher = RenderDispatcher()

    fun loadShaders(vararg renderable: Renderable) {
        renderers.addAll(renderable)
    }

    fun loadRender() {
        renderers.forEach { it.load() }
    }

    fun flushAll() {
        dispatcher.flushAll()
    }

    fun flushPipelines(vararg pipelines: ClientRenderPipeline) {
        dispatcher.flushPipelines(*pipelines)
    }
}