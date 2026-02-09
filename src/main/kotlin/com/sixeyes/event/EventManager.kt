package com.sixeyes.event

import sweetie.evaware.flora.Flora
import sweetie.evaware.flora.FloraAutomation

object EventManager {
    fun post(any: Any) {
        Flora.post(any)
    }

    fun register(any: Any) {
        FloraAutomation.register(any)
    }

    fun unregister(any: Any) {
        FloraAutomation.unregister(any)
    }
}