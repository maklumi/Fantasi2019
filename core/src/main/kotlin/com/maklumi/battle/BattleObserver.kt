package com.maklumi.battle

import com.maklumi.Entity

interface BattleObserver {
    enum class BattleEvent {
        OPPONENT_ADDED,
    }

    fun onNotify(entity: Entity, event: BattleEvent)
}