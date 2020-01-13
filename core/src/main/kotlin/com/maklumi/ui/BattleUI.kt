package com.maklumi.ui

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.maklumi.AnimationType
import com.maklumi.Entity
import com.maklumi.Utility
import com.maklumi.battle.BattleObserver
import com.maklumi.battle.BattleObserver.BattleEvent.OPPONENT_ADDED
import com.maklumi.battle.BattleState

class BattleUI : Window("BATTLE", Utility.STATUSUI_SKIN, "solidbackground"),
        BattleObserver {

    private val image = AnimatedImage()
    private val battleState = BattleState()

    init {
        battleState.battleObservers.add(this)
        image.touchable = Touchable.disabled
        add(image).size(160f, 160f)
        pack()
    }

    fun battleZoneTriggered(zoneID: String) {
        battleState.battleZoneEntered(zoneID)
    }

    override fun onNotify(entity: Entity, event: BattleObserver.BattleEvent) {
        when (event) {
            OPPONENT_ADDED -> image.setAnim(entity.getAnimation(AnimationType.IMMOBILE))

        }
    }

}