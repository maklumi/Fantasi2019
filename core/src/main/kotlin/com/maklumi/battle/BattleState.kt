package com.maklumi.battle

import com.maklumi.battle.MonsterFactory.MonsterEntityType.MONSTER001
import com.maklumi.battle.MonsterFactory.getMonster

class BattleState : BattleSubject() {

    fun battleZoneEntered(battleZoneID: String) {
        val entity = when (battleZoneID.toInt()) {
            1 -> getMonster(MONSTER001)
            else -> null
        } ?: return

        notify(entity, BattleObserver.BattleEvent.OPPONENT_ADDED)
    }

}