package com.maklumi.ui

interface InventorySlotObserver {

    fun onNotify(slot: InventorySlot, event: SlotEvent)

    enum class SlotEvent {
        ADDED_ITEM,
        REMOVED_ITEM
    }
}