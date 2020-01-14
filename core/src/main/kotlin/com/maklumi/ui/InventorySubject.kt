package com.maklumi.ui

import com.badlogic.gdx.utils.Array as gdxArray

interface InventorySubject {

    val inventoryObservers: gdxArray<InventoryObserver>

    fun notify(value: String, event: InventoryObserver.InventoryEvent) {
        inventoryObservers.forEach { it.onNotify(value, event) }
    }

}