package com.sixeyes.module.setting

class MultiModeSetting(
    name: String,
    defaultValue: Set<String>,
    val modes: List<String>,
    description: String = "",
    isVisible: () -> Boolean = { true }
) : Setting<Set<String>>(name, defaultValue, defaultValue, description, isVisible) {

    fun isSelected(m: String): Boolean = value.contains(m)

    fun toggle(m: String) {
        val newSet = value.toMutableSet()
        if (newSet.contains(m)) {
            newSet.remove(m)
        } else {
            newSet.add(m)
        }
        set(newSet)
    }
}
