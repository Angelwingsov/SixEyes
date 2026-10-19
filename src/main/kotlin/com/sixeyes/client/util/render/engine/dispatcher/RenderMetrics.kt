package com.sixeyes.client.util.render.engine.dispatcher

import com.sixeyes.client.render.main.buffer.GpuBufferPool

object RenderMetrics {
    var enabled: Boolean = false

    @Volatile var frameDrawCalls: Int = 0
    @Volatile var frameFlushes: Int = 0
    @Volatile var frameVertices: Int = 0
    @Volatile var frameIndices: Int = 0
    @Volatile var frameProgramSwitches: Int = 0

    @Volatile var lastDrawCalls: Int = 0
    @Volatile var lastFlushes: Int = 0
    @Volatile var lastVertices: Int = 0
    @Volatile var lastIndices: Int = 0
    @Volatile var lastProgramSwitches: Int = 0
    @Volatile var lastVboHits: Int = 0
    @Volatile var lastVboMisses: Int = 0

    fun onFrameStart() {
        if (!enabled) return
        lastDrawCalls = frameDrawCalls
        lastFlushes = frameFlushes
        lastVertices = frameVertices
        lastIndices = frameIndices
        lastProgramSwitches = frameProgramSwitches
        lastVboHits = GpuBufferPool.poolHits
        lastVboMisses = GpuBufferPool.poolMisses

        frameDrawCalls = 0
        frameFlushes = 0
        frameVertices = 0
        frameIndices = 0
        frameProgramSwitches = 0
    }

    fun onDrawCall(vertices: Int, indices: Int) {
        if (!enabled) return
        frameDrawCalls++
        frameVertices += vertices
        frameIndices += indices
    }

    fun onFlush() {
        if (!enabled) return
        frameFlushes++
    }

    fun onProgramSwitch() {
        if (!enabled) return
        frameProgramSwitches++
    }

    fun summary(): String {
        return "Draws: $lastDrawCalls, Flushes: $lastFlushes, Verts: $lastVertices, Indices: $lastIndices, ProgramSwitches: $lastProgramSwitches, VBO Reuses: $lastVboHits, VBO News: $lastVboMisses"
    }
}
