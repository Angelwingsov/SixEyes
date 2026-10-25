package com.sixeyes

import com.sixeyes.client.command.CommandManager
import com.sixeyes.client.config.ConfigManager
import com.sixeyes.client.listener.ListenerManager
import com.sixeyes.client.util.render.VulkanShapePipelines
import com.sixeyes.client.util.render.font.VulkanFontPipelines
import com.sixeyes.module.ModuleManager
import net.fabricmc.api.ClientModInitializer

object SixEyes : ClientModInitializer {
	override fun onInitializeClient() {
		VulkanFontPipelines.init()
		VulkanShapePipelines.init()
		ListenerManager.load()
		ModuleManager.load()
		CommandManager.load()
		ConfigManager.loadConfig("default")

		Runtime.getRuntime().addShutdownHook(Thread {
			ConfigManager.saveConfig("default")
		})
	}
}
