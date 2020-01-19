package com.maklumi.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Array
import com.maklumi.audio.AudioManager
import com.maklumi.audio.AudioObserver
import com.maklumi.audio.AudioSubject

open class GameScreen : Screen, AudioSubject {

    final override val audioObservers = Array<AudioObserver>()

    init {
        audioObservers.add(AudioManager)
    }

    override fun show() {
    }

    override fun render(delta: Float) {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun hide() {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}