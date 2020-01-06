package com.maklumi.dialog

import com.badlogic.gdx.utils.Array as gdxArray

open class ComponentSubject {

    val uiObservers = gdxArray<ComponentObserver>()

    fun notify(value: String, event: ComponentObserver.ComponentEvent) {
        for (observer in uiObservers) {
            observer.onNotify(value, event)
        }
    }

}