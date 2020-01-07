package com.maklumi.ui

interface StatusObserver {

    fun onNotify(value: Int, event: StatusEvent)

    enum class StatusEvent {
        UPDATED_GP,
        UPDATED_LEVEL,
        UPDATED_HP,
        UPDATED_MP,
        UPDATED_XP
    }
}