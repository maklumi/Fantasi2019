package com.maklumi.battle

import com.maklumi.Entity

interface BattleObserver {
    enum class BattleEvent {
        OPPONENT_ADDED,
        OPPONENT_DEFEATED,
        PLAYER_RUNNING,
    }

    fun onNotify(entity: Entity, event: BattleEvent)
}