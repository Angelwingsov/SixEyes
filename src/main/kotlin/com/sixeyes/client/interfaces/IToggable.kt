package com.sixeyes.client.interfaces

interface IToggable {
    fun isEnabled(): Boolean
    fun setEnabled(value: Boolean)
    fun toggle()
}