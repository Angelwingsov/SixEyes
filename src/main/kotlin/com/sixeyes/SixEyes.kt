package com.sixeyes

import com.sixeyes.client.listener.ListenerManager
import com.sixeyes.client.util.render.font.VulkanFontPipelines
import com.sixeyes.module.ModuleManager
import net.fabricmc.api.ClientModInitializer

object SixEyes : ClientModInitializer {
	override fun onInitializeClient() {
		VulkanFontPipelines.init()
		ListenerManager.load()
		ModuleManager.load()
	}
}