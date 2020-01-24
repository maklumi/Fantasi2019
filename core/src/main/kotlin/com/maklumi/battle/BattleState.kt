package com.maklumi.battle

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Timer
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
    var currentZoneLevel = 1
    private var chanceOfAttack = 25
    private var chanceOfEscape = 40
    private var criticalChance = 90
    private var originalOpponentHP = 0
    private var playerMagicWandAPPoints = 0
    private val playerAttackTask = playerAttackCalculations()
    private val playerMagicTask = playerMagicUseCheckTimer()
    private val opponentAttackTask = opponentAttackCalculations()

    fun resetDefaults() {
        currentZoneLevel = 0
        attackPoint = 0
        defencePoint = 0
        playerMagicWandAPPoints = 0
        playerAttackTask.cancel()
        opponentAttackTask.cancel()
        playerMagicTask.cancel()
    }

    override fun onNotify(value: String, event: InventoryObserver.InventoryEvent) {
        when (event) {
            UPDATED_AP -> {
                attackPoint = value.toInt()
//                println("AttackPoint: $attackPoint")
            }
            UPDATED_DP -> {
                defencePoint = value.toInt()
//                println("DefencePoint: $defencePoint")
            }
            InventoryObserver.InventoryEvent.ITEM_CONSUMED -> {
            }
            InventoryObserver.InventoryEvent.ADD_WAND_AP -> {
                playerMagicWandAPPoints += value.toInt()
            }
            InventoryObserver.InventoryEvent.REMOVE_WAND_AP -> {
                playerMagicWandAPPoints -= value.toInt()
            }
        }
    }

    fun playerAttacks() {
        if (opponent == null) return

        //Check for magic if used in attack; If we don't have enough MP, then return
        val mpVal = ProfileManager.getProperty("currentPlayerMP") ?: 0
        notify(opponent!!, PLAYER_TURN_START)

        when {
            playerMagicWandAPPoints == 0 -> {
                if (!playerAttackTask.isScheduled)
                    Timer.schedule(playerAttackTask, 1f)
            }
            playerMagicWandAPPoints > mpVal -> {
                notify(opponent!!, PLAYER_TURN_DONE)
                return
            }
            else -> {
                if (!playerMagicTask.isScheduled)
                    Timer.schedule(playerMagicTask, 0.5f)
                if (!playerAttackTask.isScheduled)
                    Timer.schedule(playerAttackTask, 1f)
            }
        }
    }

    private fun playerMagicUseCheckTimer(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                var mpVal = ProfileManager.getProperty("currentPlayerMP") ?: 0
                mpVal -= playerMagicWandAPPoints
                ProfileManager.setProperty("currentPlayerMP", mpVal)
                notify(opponent!!, PLAYER_USED_MAGIC)
            }
        }
    }

    fun playerRuns() {
        val threshold = MathUtils.random(1, 100)
        when {
            chanceOfEscape > threshold -> opponent?.let { notify(opponent!!, PLAYER_RUNNING) }
            threshold > criticalChance -> opponentAttacks()
        }
    }

    fun isOpponentReady(): Boolean {
        val randomVal = MathUtils.random(1, 100)
//        println("BattleState65: CHANCE OF ATTACK: $chanceOfAttack randomval: $randomVal")
        return if (chanceOfAttack > randomVal) {
            battleZoneEntered()
            true
        } else {
            false
        }
    }

    fun opponentAttacks() {
        if (opponent == null) return

        val opponentHP = opponent!!.entityConfig.entityProperties[ENTITY_HEALTH_POINTS()].toInt()
        if (opponentHP <= 0) {
            notify(opponent!!, OPPONENT_TURN_DONE)
            return
        }
        if (!opponentAttackTask.isScheduled)
            Timer.schedule(opponentAttackTask, 1f)
    }

    fun battleZoneEntered() {
        val entity = MonsterFactory.getRandomMonster(currentZoneLevel) ?: return
        opponent = entity
        originalOpponentHP = opponent!!.entityConfig.entityProperties[ENTITY_HEALTH_POINTS()].toInt()
        notify(entity, OPPONENT_ADDED)
    }

    private fun playerAttackCalculations(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                val enemyHP = opponent!!.entityConfig.entityProperties[ENTITY_HEALTH_POINTS()].toInt()
                val enemyDP = opponent!!.entityConfig.entityProperties[ENTITY_DEFENSE_POINTS()].toInt()
                val damage = MathUtils.clamp(attackPoint - enemyDP, 0, attackPoint)
                val currentHP = MathUtils.clamp(enemyHP - damage, 0, enemyHP)
                opponent!!.entityConfig.entityProperties.put(ENTITY_HEALTH_POINTS(), currentHP.toString())
//        println("BattleState43: Player attacks ${opponent!!.entityConfig.entityID}. $enemyHP HP - $damage = $currentHP HP")
                if (currentHP == 0) {
                    if (damage > 0) notify(opponent!!, OPPONENT_HIT_DAMAGE)
                    notify(opponent!!, OPPONENT_DEFEATED)
                    opponent!!.entityConfig.entityProperties.put(ENTITY_HEALTH_POINTS(), originalOpponentHP.toString())
                    opponent = null
                } else {
                    opponent!!.entityConfig.entityProperties.put(ENTITY_HIT_DAMAGE_TOTAL(), damage.toString())
                    if (damage > 0) notify(opponent!!, OPPONENT_HIT_DAMAGE)
                    notify(opponent!!, PLAYER_TURN_DONE)
                }
            }
        }
    }

    private fun opponentAttackCalculations(): Timer.Task {
        return object : Timer.Task() {
            override fun run() {
                val ap = opponent!!.entityConfig.entityProperties.get(ENTITY_ATTACK_POINTS.toString()).toInt()
                val damage = MathUtils.clamp(ap - defencePoint, 0, ap)
                val hp = ProfileManager.getProperty("currentPlayerHP") ?: 0
                val hpVal = MathUtils.clamp(hp - damage, 0, hp)
                ProfileManager.setProperty("currentPlayerHP", hpVal)
                if (damage > 0) notify(opponent!!, PLAYER_HIT_DAMAGE)
//        println("BattleState82: ${opponent!!.entityConfig.entityID} attacks. Player HP $hp - $damage = $hpVal HP")
                notify(opponent!!, OPPONENT_TURN_DONE)
            }
        }
    }
}