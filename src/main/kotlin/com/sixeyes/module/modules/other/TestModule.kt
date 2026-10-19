package com.sixeyes.module.modules.other

import com.sixeyes.client.extensions.OTHER
import com.sixeyes.module.Module

object TestModule : Module(
    name = "Test",
    category = OTHER,
    description = "Material 3 showcase module"
) {
    val theSwitch = boolean("The Switch", true)
    val select = multiMode("Select", setOf("Sel 1", "Sel 3", "Sel 4"), listOf("Sel 1", "Sel 2", "Sel 3", "Sel 4"))
    val modes = multiMode("Modes", setOf("Default", "Aggressive"), listOf("Default", "Aggressive", "Legit"))
    val group = mode("Group", "Gp 3", listOf("Gp 1", "Gp 2", "Gp 3"))
}
