package com.maklumi.battle

import com.badlogic.gdx.math.MathUtils
import com.maklumi.Entity
import com.maklumi.EntityProperties.ENTITY_DEFENSE_POINTS
import com.maklumi.EntityProperties.ENTITY_HEALTH_POINTS
import com.maklumi.battle.BattleObserver.BattleEvent.*
import com.maklumi.ui.InventoryObserver
import com.maklumi.ui.InventoryObserver.InventoryEvent.UPDATED_AP
import com.maklumi.ui.InventoryObserver.InventoryEvent.UPDATED_DP

class BattleState : BattleSubject(), InventoryObserver {
    private var attackPoint = 0
    private var defencePoint = 0
    private var opponent: Entity? = null

    override fun onNotify(value: String, event: InventoryObserver.InventoryEvent) {
        when (event) {
            UPDATED_AP -> {
                attackPoint = value.toInt()
                println("AttackPoint: $attackPoint")
            }
            UPDATED_DP -> {
                defencePoint = value.toInt()
                println("DefencePoint: $defencePoint")
            }
        }
    }

    fun playerAttacks() {
        val enemyHP = opponent!!.entityConfig.entityProperties[ENTITY_HEALTH_POINTS()].toInt()
        val enemyDP = opponent!!.entityConfig.entityProperties[ENTITY_DEFENSE_POINTS()].toInt()
        val damage = MathUtils.clamp(attackPoint - enemyDP, 0, attackPoint)
        val currentHP = MathUtils.clamp(enemyHP - damage, 0, enemyHP)
        opponent!!.entityConfig.entityProperties.put(ENTITY_HEALTH_POINTS(), currentHP.toString())
        println("Player attacks ${opponent!!.entityConfig.entityID}. $enemyHP HP - $damage = $currentHP HP")
        if (currentHP == 0) notify(opponent!!, OPPONENT_DEFEATED)
    }

    fun playerRuns() {
        notify(opponent!!, PLAYER_RUNNING)
    }

    fun battleZoneEntered(battleZoneID: String) {
        val entity = MonsterFactory.getRandomMonster(battleZoneID.toInt()) ?: return
        opponent = entity
        notify(entity, OPPONENT_ADDED)
    }

}