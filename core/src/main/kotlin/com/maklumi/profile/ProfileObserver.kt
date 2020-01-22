package com.maklumi.profile

interface ProfileObserver {
    fun onNotify(event: ProfileEvent)
}

enum class ProfileEvent {
    CLEAR_CURRENT_PROFILE,
    PROFILE_LOADED,
    SAVING_PROFILE
}