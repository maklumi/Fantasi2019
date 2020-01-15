package com.maklumi.battle

import com.badlogic.gdx.math.MathUtils
import com.maklumi.Entity
import com.maklumi.EntityProperties.*
import com.maklumi.battle.BattleObserver.BattleEvent.*
import com.maklumi.profile.ProfileManager
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
        if (opponent == null) return
        notify(opponent!!, PLAYER_TURN_START)

        val enemyHP = opponent!!.entityConfig.entityProperties[ENTITY_HEALTH_POINTS()].toInt()
        val enemyDP = opponent!!.entityConfig.entityProperties[ENTITY_DEFENSE_POINTS()].toInt()
        val damage = MathUtils.clamp(attackPoint - enemyDP, 0, attackPoint)
//        val damage = 2
        val currentHP = MathUtils.clamp(enemyHP - damage, 0, enemyHP)
        opponent!!.entityConfig.entityProperties.put(ENTITY_HEALTH_POINTS(), currentHP.toString())
        println("Player attacks ${opponent!!.entityConfig.entityID}. $enemyHP HP - $damage = $currentHP HP")
        if (currentHP == 0) {
            notify(opponent!!, OPPONENT_DEFEATED)
        } else {
            opponent!!.entityConfig.entityProperties.put(ENTITY_HIT_DAMAGE_TOTAL(), damage.toString())
            notify(opponent!!, OPPONENT_HIT_DAMAGE)
            notify(opponent!!, PLAYER_TURN_DONE)
        }
    }

    fun playerRuns() {
        notify(opponent!!, PLAYER_RUNNING)
    }

    fun opponentAttacks() {
        if (opponent == null) return

        val ap = opponent!!.entityConfig.entityProperties.get(ENTITY_ATTACK_POINTS.toString()).toInt()
        val damage = MathUtils.clamp(ap - defencePoint, 0, ap)
        val hp = ProfileManager.getProperty("currentPlayerHP") ?: 0
        val hpVal = MathUtils.clamp(hp - damage, 0, hp)
        ProfileManager.setProperty("currentPlayerHP", hpVal)
        notify(opponent!!, PLAYER_HIT_DAMAGE)
        println("${opponent!!.entityConfig.entityID} attacks. Player HP $hp - $damage = $hpVal HP")
        notify(opponent!!, OPPONENT_TURN_DONE)
    }

    fun battleZoneEntered(battleZoneID: String) {
        val entity = MonsterFactory.getRandomMonster(battleZoneID.toInt()) ?: return
        opponent = entity
        notify(entity, OPPONENT_ADDED)
    }

}