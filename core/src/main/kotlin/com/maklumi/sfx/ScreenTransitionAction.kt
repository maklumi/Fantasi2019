package com.maklumi.sfx

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class ScreenTransitionAction(
        private var type: ScreenTransitionType = ScreenTransitionType.FADE_IN,
        private var duration: Float = 0f
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

    companion object {
        fun transition(type: ScreenTransitionType, duration: Float): ScreenTransitionAction {
            val action = Actions.action(ScreenTransitionAction::class.java)
            action.type = type
            action.duration = duration
            return action
        }
    }
}