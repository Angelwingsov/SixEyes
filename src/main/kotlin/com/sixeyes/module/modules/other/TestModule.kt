package com.sixeyes.module.modules.other

import com.sixeyes.client.extensions.OTHER
import com.sixeyes.client.util.other.color.Color4b
import com.sixeyes.module.Module

object TestModule : Module(
    name = "Test",
    category = OTHER,
    description = "Material 3 showcase module"
) {
    val theSwitch by boolean("The Switch", true)
    val colorPicker by color("Color Picker", Color4b(140, 60, 255, 255), description = "Material 3 color picker setting")
    val select by multiMode("Select", setOf("Sel 1", "Sel 3", "Sel 4"), listOf("Sel 1", "Sel 2", "Sel 3", "Sel 4"))
    val modes by multiMode("Modes", setOf("Default", "Aggressive"), listOf("Default", "Aggressive", "Legit"))
    val group by mode("Group", "Gp 3", listOf("Gp 1", "Gp 2", "Gp 3"))
}
