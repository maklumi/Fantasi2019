package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.maklumi.AnimationType
import com.maklumi.Entity
import com.maklumi.EntityProperties
import com.maklumi.Utility
import com.maklumi.battle.BattleObserver
import com.maklumi.battle.BattleObserver.BattleEvent
import com.maklumi.battle.BattleObserver.BattleEvent.*
import com.maklumi.battle.BattleState
import ktx.actors.onClick

class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"),
        BattleObserver {

    private val image = AnimatedImage()
    val battleState = BattleState()
    private val attackButton = TextButton("Attack", skin, "inventory")
    private val runButton = TextButton("Run", skin)
    private val damageLabel = Label("", skin)

    init {
        val table = Table()
        table.add(attackButton).pad(20f, 20f, 20f, 20f)
        table.row()
        table.add(runButton).pad(20f, 20f, 20f, 20f)

        battleState.battleObservers.add(this)
        image.touchable = Touchable.disabled
        add(damageLabel).align(Align.left).padLeft(80f).row()
        add(image).size(160f, 160f).pad(20f, 20f, 20f, 80f)
        damageLabel.isVisible = false
        damageLabel.toFront()
        add(table)
        pack()

        attackButton.onClick { battleState.playerAttacks() }
        runButton.onClick { battleState.playerRuns() }
    }

    private var damageLabelStartY = damageLabel.y + 300f
    private var currentBattleZone = 0

    fun battleZoneTriggered(zoneID: String) {
        currentBattleZone = zoneID.toInt()
        battleState.battleZoneEntered(zoneID)
    }

    override fun onNotify(entity: Entity, event: BattleEvent) {
        when (event) {
            OPPONENT_ADDED -> {
                image.setAnim(entity.getAnimation(AnimationType.IMMOBILE))
                titleLabel.setText("Level $currentBattleZone. ${entity.entityConfig.entityID}")
            }
            OPPONENT_DEFEATED -> {
                damageLabel.isVisible = false
                damageLabel.y = damageLabelStartY
            }
            PLAYER_RUNNING -> {
            }
            OPPONENT_HIT_DAMAGE -> {
                val damage = entity.entityConfig.entityProperties[EntityProperties.ENTITY_HIT_DAMAGE_TOTAL()]
                damageLabel.setText(damage)
                damageLabel.y = damageLabelStartY
                damageLabel.isVisible = true
            }
            OPPONENT_TURN_DONE -> {
                attackButton.isDisabled = false
                attackButton.touchable = Touchable.enabled
            }
            PLAYER_TURN_START -> {
                attackButton.isDisabled = true
                attackButton.touchable = Touchable.disabled
            }
            PLAYER_HIT_DAMAGE -> {
            }
            PLAYER_TURN_DONE -> {
                battleState.opponentAttacks()
            }
        }
    }

    override fun act(delta: Float) {
        if (damageLabel.isVisible) {
            damageLabel.y = damageLabel.y + 3
            if (damageLabel.y > stage.height) damageLabel.isVisible = false
        }
        super.act(delta)
    }
}