package com.maklumi.dialog

interface ComponentObserver {

    fun onNotify(value: String, event: ComponentEvent)

    enum class ComponentEvent {
        LOAD_CONVERSATION,
        SHOW_CONVERSATION,
        HIDE_CONVERSATION,
        QUEST_LOCATION_DISCOVERED,
        ENEMY_SPAWN_LOCATION_CHANGED,
        PLAYER_HAS_MOVED,
    }
}