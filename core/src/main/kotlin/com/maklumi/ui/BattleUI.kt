package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.maklumi.AnimationType
import com.maklumi.Entity
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
    private val attackButton = TextButton("Attack", skin)
    private val runButton = TextButton("Run", skin)

    init {
        val table = Table()
        table.add(attackButton).pad(20f, 20f, 20f, 20f)
        table.row()
        table.add(runButton).pad(20f, 20f, 20f, 20f)

        battleState.battleObservers.add(this)
        image.touchable = Touchable.disabled
        add(image).size(160f, 160f).pad(20f, 20f, 20f, 80f)
        add(table)
        pack()

        attackButton.onClick { battleState.playerAttacks() }
        runButton.onClick { battleState.playerRuns() }
    }

    fun battleZoneTriggered(zoneID: String) {
        battleState.battleZoneEntered(zoneID)
    }

    override fun onNotify(entity: Entity, event: BattleEvent) {
        when (event) {
            OPPONENT_ADDED -> image.setAnim(entity.getAnimation(AnimationType.IMMOBILE))
            OPPONENT_DEFEATED -> isVisible = false
            PLAYER_RUNNING -> isVisible = false
        }
    }

}