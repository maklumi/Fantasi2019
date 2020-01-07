package com.maklumi.ui

import com.badlogic.gdx.utils.Array as gdxArray

interface StoreInventorySubject {

    val storeInventoryObservers: gdxArray<StoreInventoryObserver>

    fun notify(value: Int, event: StoreInventoryObserver.StoreInventoryEvent) {
        for (observer in storeInventoryObservers) {
            observer.onNotify(value, event)
        }
    }

}