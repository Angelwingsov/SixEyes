package com.sixeyes.event.events

import com.sixeyes.event.types.EventCancellable
import net.minecraft.client.gui.screens.Screen

class ScreenCloseEvent(
    val screen: Screen
) : EventCancellable()
