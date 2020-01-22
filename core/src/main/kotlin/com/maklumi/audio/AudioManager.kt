package com.maklumi.audio

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.maklumi.Utility
import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent
import java.util.*

object AudioManager : AudioObserver {

    private val queuedMusic = Hashtable<String, Music>()
    private val queuedSounds = Hashtable<String, Sound>()

    override fun onNotify(command: AudioObserver.AudioCommand, event: AudioTypeEvent) {
        val filePath = event.path
        when (command) {
            MUSIC_LOAD -> Utility.loadMusicAsset(filePath)
            MUSIC_PLAY_ONCE -> playMusic(false, filePath)
            MUSIC_PLAY_LOOP -> playMusic(true, filePath)
            MUSIC_STOP -> queuedMusic[filePath]?.stop()
            SOUND_LOAD -> Utility.loadSoundAsset(filePath)
            SOUND_PLAY_LOOP -> playSound(true, filePath)
            SOUND_PLAY_ONCE -> playSound(false, filePath)
            SOUND_STOP -> queuedSounds[filePath]?.stop()
            MUSIC_STOP_ALL -> queuedMusic.values.forEach(Music::stop)
        }
    }

    private fun playMusic(isLooping: Boolean, fullFilePath: String) {
        var music = queuedMusic[fullFilePath]
        if (music == null) {
            if (Utility.isAssetLoaded(fullFilePath)) {
                music = Utility.getMusicAsset(fullFilePath)
                queuedMusic[fullFilePath] = music
            } else {
                println("AudioManager37: Music not loaded")
                return
            }
        }
        music!!.isLooping = isLooping
        music.volume = 0.02f
        music.play()
    }

    private fun playSound(isLooping: Boolean, fullFilePath: String) {
        var sound = queuedSounds[fullFilePath]
        if (sound == null) {
            if (Utility.isAssetLoaded(fullFilePath)) {
                sound = Utility.getSoundAsset(fullFilePath)
                queuedSounds[fullFilePath] = sound
            } else {
                println("AudioManager53: Sound not loaded")
                return
            }
        }
        val soundId = sound!!.play(0.02f)
        sound.setLooping(soundId, isLooping)
    }

    fun dispose() {
        queuedMusic.values.forEach { it.dispose() }
        queuedSounds.values.forEach { it.dispose() }
    }
}