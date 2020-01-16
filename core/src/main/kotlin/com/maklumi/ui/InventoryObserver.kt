package com.maklumi.ui

interface InventoryObserver {

    enum class InventoryEvent {
        UPDATED_AP,
        UPDATED_DP,
        ITEM_CONSUMED,
        ADD_WAND_AP,
        REMOVE_WAND_AP,
    }

    fun onNotify(value: String, event: InventoryEvent)

}