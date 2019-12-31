package com.maklumi.profile

import com.badlogic.gdx.utils.Array as gdxArray

open class ProfileSubject {

    val profileObservers = gdxArray<ProfileObserver>()

    protected fun notifyProfileObservers(event: ProfileEvent) {
        profileObservers.forEach { it.onNotify(event) }
    }

}