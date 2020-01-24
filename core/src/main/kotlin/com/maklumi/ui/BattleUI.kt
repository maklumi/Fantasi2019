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
import com.maklumi.sfx.ShakeCamera
import ktx.actors.onClick

class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"),
        BattleObserver {

    private val image = AnimatedImage()
    val battleState = BattleState()
    private val attackButton = TextButton("Attack", skin, "inventory")
    private val runButton = TextButton("Run", skin, "inventory")
    private val damageLabel = Label("", skin)
    private val shakeCamera = ShakeCamera(0f, 0f, 30f)

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
    private var battleTimer = 0f
    private val waitSecond = 2f

    fun isBattleReady(): Boolean {
        return if (battleTimer > waitSecond) {
            battleTimer = 0f
            return battleState.isOpponentReady()
        } else {
            false
        }
    }

    fun battleZoneTriggered() {
        battleState.battleZoneEntered()
    }

    override fun onNotify(entity: Entity, event: BattleEvent) {
        when (event) {
            OPPONENT_ADDED -> {
                image.entity = entity // always set entity before setAnim
                image.setAnim(AnimationType.IMMOBILE)
                titleLabel.setText("Level ${battleState.currentZoneLevel}. ${entity.entityConfig.entityID}")
            }
            OPPONENT_DEFEATED -> {
                battleTimer = 0f
                damageLabel.isVisible = false
                damageLabel.y = damageLabelStartY
            }
            PLAYER_RUNNING -> {
                battleTimer = 0f
            }
            OPPONENT_HIT_DAMAGE -> {
                val damage = entity.entityConfig.entityProperties[EntityProperties.ENTITY_HIT_DAMAGE_TOTAL()]
                damageLabel.setText(damage)
                damageLabel.y = damageLabelStartY
                damageLabel.isVisible = true
                shakeCamera.oriPosition.set(image.x, image.y)
                shakeCamera.shouldShake = true
            }
            OPPONENT_TURN_DONE -> {
                runButton.isDisabled = false
                runButton.touchable = Touchable.enabled
                attackButton.isDisabled = false
                attackButton.touchable = Touchable.enabled
            }
            PLAYER_TURN_START -> {
                runButton.isDisabled = true
                runButton.touchable = Touchable.disabled
                attackButton.isDisabled = false
                attackButton.touchable = Touchable.enabled
            }
            PLAYER_HIT_DAMAGE -> {
            }
            PLAYER_TURN_DONE -> {
                battleState.opponentAttacks()
            }
            PLAYER_USED_MAGIC -> {
            }
        }
    }

    override fun act(delta: Float) {
        battleTimer = (battleTimer + delta) % 60
        if (damageLabel.isVisible) {
            damageLabel.y = damageLabel.y + 3
            if (damageLabel.y > stage.height) damageLabel.isVisible = false
        }
        if (shakeCamera.shouldShake) {
            image.x = shakeCamera.position.x
            image.y = shakeCamera.position.y
        }
        super.act(delta)
    }
}