package com.maklumi.profile

interface ProfileObserver {
    fun onNotify(event: ProfileEvent)
}

enum class ProfileEvent {
    PROFILE_LOADED,
    SAVING_PROFILE
}