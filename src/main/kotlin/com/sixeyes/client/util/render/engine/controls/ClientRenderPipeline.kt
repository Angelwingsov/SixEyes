package com.sixeyes.client.util.render.engine.controls

enum class RectGroup {
    BASIC, TEXTURED
}

enum class ClientRenderPipeline {
    // Отрисовка в 2D
    FIRST_TEXT, FIRST_SPECIAL, FIRST_RECT,
    POPUP_TEXT, POPUP_SPECIAL, POPUP_RECT,
    WINDOW_TEXT, WINDOW_SPECIAL, WINDOW_RECT,
    GUI_TEXT, GUI_SPECIAL, GUI_RECT,
    HUD_TEXT, HUD_SPECIAL, HUD_RECT,
    HIGH, MEDIUM, LOW,

    // Отрисовка в 3D
    WORLD
}