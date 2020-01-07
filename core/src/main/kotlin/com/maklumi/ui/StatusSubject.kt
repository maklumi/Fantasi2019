package com.maklumi.ui

import com.badlogic.gdx.utils.Array as gdxArray

interface StatusSubject {

    val statusObservers: gdxArray<StatusObserver>

    fun notify(value: Int, event: StatusObserver.StatusEvent){
        statusObservers.forEach { it.onNotify(value, event) }
    }

}