package com.maklumi.battle

import com.maklumi.Entity

interface BattleObserver {
    enum class BattleEvent {
        OPPONENT_ADDED,
        OPPONENT_HIT_DAMAGE, OPPONENT_TURN_DONE, OPPONENT_DEFEATED,
        PLAYER_TURN_START, PLAYER_HIT_DAMAGE, PLAYER_TURN_DONE, PLAYER_RUNNING,
        PLAYER_USED_MAGIC,
    }

    fun onNotify(entity: Entity, event: BattleEvent)
}