package com.sixeyes.client.interfaces

interface Toggable {
    fun isEnabled(): Boolean
    fun setEnabled(value: Boolean)
    fun toggle()
}