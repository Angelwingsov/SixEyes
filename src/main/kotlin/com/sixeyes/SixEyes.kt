package com.sixeyes

import com.sixeyes.client.listener.ListenerManager
import com.sixeyes.module.ModuleManager
import net.fabricmc.api.ClientModInitializer

object SixEyes : ClientModInitializer {
	override fun onInitializeClient() {
		ListenerManager.load()
		ModuleManager.load()
	}
}