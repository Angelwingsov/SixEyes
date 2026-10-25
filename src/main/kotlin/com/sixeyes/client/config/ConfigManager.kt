package com.sixeyes.client.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sixeyes.client.command.CommandManager
import com.sixeyes.client.extensions.mc
import com.sixeyes.module.ModuleManager
import java.io.File

object ConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    val configDirectory: File
        get() {
            val dir = File(mc.gameDirectory, "sixeyes/configs")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return dir
        }

    fun saveConfig(name: String = "default"): Boolean {
        return try {
            val file = File(configDirectory, "$name.json")
            val root = JsonObject()
            root.addProperty("prefix", CommandManager.prefix)

            val modulesObj = JsonObject()
            for (module in ModuleManager.modules) {
                val modObj = JsonObject()
                modObj.addProperty("enabled", module.isEnabled())
                modObj.addProperty("key", module.key)
                modObj.addProperty("expanded", module.isExpanded)

                val settingsObj = JsonObject()
                for (setting in module.settings) {
                    settingsObj.add(setting.name, setting.serialize())
                }
                modObj.add("settings", settingsObj)

                modulesObj.add(module.name, modObj)
            }

            root.addProperty("prefix", CommandManager.prefix)
            root.add("modules", modulesObj)
            file.writeText(gson.toJson(root))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadConfig(name: String = "default"): Boolean {
        return try {
            val file = File(configDirectory, "$name.json")
            if (!file.exists()) return false

            val root = JsonParser.parseString(file.readText()).asJsonObject
            if (root.has("prefix")) {
                CommandManager.prefix = root.get("prefix").asString
            }

            val modulesObj = root.getAsJsonObject("modules") ?: return false

            for (module in ModuleManager.modules) {
                val modObj = modulesObj.getAsJsonObject(module.name) ?: continue

                if (modObj.has("enabled")) {
                    module.setEnabled(modObj.get("enabled").asBoolean)
                }
                if (modObj.has("key")) {
                    module.key = modObj.get("key").asInt
                }
                if (modObj.has("expanded")) {
                    module.isExpanded = modObj.get("expanded").asBoolean
                }

                val settingsObj = modObj.getAsJsonObject("settings") ?: continue
                for (setting in module.settings) {
                    val settingElement = settingsObj.get(setting.name) ?: continue
                    setting.deserialize(settingElement)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteConfig(name: String): Boolean {
        val file = File(configDirectory, "$name.json")
        return if (file.exists()) {
            file.delete()
        } else false
    }

    fun clearConfigs(): Int {
        val files = configDirectory.listFiles { file -> file.isFile && file.name.endsWith(".json") } ?: return 0
        var count = 0
        for (file in files) {
            if (file.delete()) count++
        }
        return count
    }

    fun listConfigs(): List<String> {
        val files = configDirectory.listFiles { file -> file.isFile && file.name.endsWith(".json") }
        return files?.map { it.nameWithoutExtension } ?: emptyList()
    }

    fun openDirectory(): Boolean {
        return try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(configDirectory)
                true
            } else {
                ProcessBuilder("explorer.exe", configDirectory.absolutePath).start()
                true
            }
        } catch (_: Exception) {
            try {
                ProcessBuilder("explorer.exe", configDirectory.absolutePath).start()
                true
            } catch (_: Exception) {
                false
            }
        }
    }
}
