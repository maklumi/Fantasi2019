package com.maklumi.ui

interface InventoryObserver {

    enum class InventoryEvent {
        UPDATED_AP,
        UPDATED_DP,
    }

    fun onNotify(value: String, event: InventoryEvent)

}