package com.maklumi.audio

import com.badlogic.gdx.utils.Array

interface AudioSubject {

    val audioObservers: Array<AudioObserver>

    fun notify(command: AudioObserver.AudioCommand, event: AudioObserver.AudioTypeEvent) {
        val array = Array.ArrayIterable(audioObservers)
        for (observer in array) {
            observer.onNotify(command, event)
        }
    }

}