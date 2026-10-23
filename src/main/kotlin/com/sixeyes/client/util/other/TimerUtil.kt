package com.sixeyes.client.util.other

class TimerUtil {
    private var time: Long = System.currentTimeMillis()

    fun reset() {
        time = System.currentTimeMillis()
    }

    fun isComplete(ms: Long): Boolean {
        return System.currentTimeMillis() - time >= ms
    }

    fun hasElapsed(ms: Long, reset: Boolean = false): Boolean {
        if (System.currentTimeMillis() - time >= ms) {
            if (reset) reset()
            return true
        }
        return false
    }

    fun elapsed(): Long = System.currentTimeMillis() - time
}
