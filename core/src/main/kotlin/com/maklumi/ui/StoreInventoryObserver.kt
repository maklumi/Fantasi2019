package com.maklumi.ui

interface StoreInventoryObserver {

    fun onNotify(value: String, event: StoreInventoryEvent)

    enum class StoreInventoryEvent {
        PLAYER_GP_TOTAL_UPDATED,
        PLAYER_INVENTORY_UPDATED
    }

}