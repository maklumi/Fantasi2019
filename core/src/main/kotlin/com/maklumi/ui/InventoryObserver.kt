package com.maklumi.ui

interface InventoryObserver {

    enum class InventoryEvent {
        UPDATED_AP,
        UPDATED_DP,
        ITEM_CONSUMED,
    }

    fun onNotify(value: String, event: InventoryEvent)

}