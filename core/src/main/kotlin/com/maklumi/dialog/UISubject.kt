package com.maklumi.dialog

import com.badlogic.gdx.utils.Array as gdxArray

open class UISubject {

    val uiObservers = gdxArray<UIObserver>()

    fun notify(value: String, event: UIObserver.UIEvent) {
        for (observer in uiObservers) {
            observer.onNotify(value, event)
        }
    }

}