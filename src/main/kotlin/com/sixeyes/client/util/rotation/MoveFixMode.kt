package com.sixeyes.client.util.rotation

enum class MoveFixMode(val displayName: String) {
    SILENT("Silent"),
    STRICT("Strict"),
    FREE("Free"),
    OFF("Off");

    companion object {
        fun fromName(name: String): MoveFixMode {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) } ?: SILENT
        }
    }
}
