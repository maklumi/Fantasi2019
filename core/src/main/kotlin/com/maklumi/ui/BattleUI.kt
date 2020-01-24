package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
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
import com.maklumi.sfx.ParticleEffectFactory
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.WAND_ATTACK
import com.maklumi.sfx.ShakeCamera
import ktx.actors.onClick
import com.badlogic.gdx.utils.Array as gdxArray

class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"),
        BattleObserver {

    private val image = AnimatedImage()
    val battleState = BattleState()
    private val attackButton = TextButton("Attack", skin, "inventory")
    private val runButton = TextButton("Run", skin, "inventory")
    private val damageLabel = Label("", skin)
    private val shakeCamera = ShakeCamera(0f, 0f, 30f)
    private val enemyWidth = 160f
    private val enemyHeight = 160f
    private val effects = gdxArray<ParticleEffect>()

    init {
        val table = Table()
        table.add(attackButton).pad(20f, 20f, 20f, 20f)
        table.row()
        table.add(runButton).pad(20f, 20f, 20f, 20f)

        battleState.battleObservers.add(this)
        image.touchable = Touchable.disabled
        add(damageLabel).align(Align.left).padLeft(80f).row()
        add(image).size(enemyWidth, enemyHeight).pad(20f, 20f, 20f, enemyWidth / 2)
        damageLabel.isVisible = false
        damageLabel.toFront()
        add(table)
        pack()

        attackButton.onClick { battleState.playerAttacks() }
        runButton.onClick { battleState.playerRuns() }
    }

    private var damageLabelStartY = damageLabel.y + enemyHeight
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
                val x = image.x + enemyWidth / 2
                val y = image.y + enemyHeight / 2
                effects.add(ParticleEffectFactory.get(WAND_ATTACK, Vector2(x, y)))
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
        for (i in 0 until effects.size) {
            val effect = effects.get(i) ?: continue
            if (effect.isComplete) {
                effects.removeIndex(i)
                effect.dispose()
            } else {
                effect.update(delta)
            }
        }
        super.act(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        //Draw the particles last
        effects.forEach { it.draw(batch) }
    }

}