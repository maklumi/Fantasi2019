package com.maklumi.dialog

interface UIObserver {

    fun onNotify(value: String, event: UIEvent)

    enum class UIEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION
    }
}