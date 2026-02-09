package com.sixeyes

import com.sixeyes.client.util.render.TextureLoader
import com.sixeyes.module.ModuleManager
import net.fabricmc.api.ClientModInitializer

object SixEyes : ClientModInitializer {
	// TODO: Material 3 Design for client. Font: Google Sans Flex (https://m3.material.io/)

	override fun onInitializeClient() {
		ModuleManager.load()

		registerTextures()
	}

	fun registerTextures() {
		//TextureLoader.register(name, path)
	}
}