package com.maklumi.audio

import com.badlogic.gdx.Gdx
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
        }
    }

    private fun playMusic(isLooping: Boolean, fullFilePath: String) {
        if (Utility.isAssetLoaded(fullFilePath)) {
            val music = Utility.getMusicAsset(fullFilePath)
            music?.isLooping = isLooping
            music?.volume = 0.025f
            music?.play()
            queuedMusic[fullFilePath] = music
        } else {
            Gdx.app.debug("AudioManager", "Music not loaded")
            return
        }
    }

    private fun playSound(isLooping: Boolean, fullFilePath: String) {
        if (Utility.isAssetLoaded(fullFilePath)) {
            val sound = Utility.getSoundAsset(fullFilePath)
            val soundId = sound?.play() ?: 1L
            sound?.setLooping(soundId, isLooping)
            queuedSounds[fullFilePath] = sound
        } else {
            Gdx.app.debug("AudioManager", "Sound not loaded")
            return
        }
    }
}