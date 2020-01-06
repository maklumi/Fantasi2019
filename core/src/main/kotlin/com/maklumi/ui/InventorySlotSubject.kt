package com.maklumi.ui

import com.badlogic.gdx.utils.Array

interface InventorySlotSubject {

    val inventorySlotObservers: Array<InventorySlotObserver>

    fun notify(slot: InventorySlot, event: InventorySlotObserver.SlotEvent) {
        for (observer in inventorySlotObservers) {
            observer.onNotify(slot, event)
        }
    }

}