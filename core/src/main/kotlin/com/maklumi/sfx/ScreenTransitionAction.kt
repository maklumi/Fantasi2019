package com.maklumi.sfx

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class ScreenTransitionAction(
        private val type: ScreenTransitionType,
        private val duration: Float
) : Action() {

    enum class ScreenTransitionType { FADE_IN, FADE_OUT }

    override fun act(delta: Float): Boolean {
        if (this.target == null) return false
        target.isVisible = true
        when (type) {
            ScreenTransitionType.FADE_IN -> {
                target.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(duration)))
            }
            ScreenTransitionType.FADE_OUT -> {
                target.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(duration)))
            }
        }
        return true
    }
}