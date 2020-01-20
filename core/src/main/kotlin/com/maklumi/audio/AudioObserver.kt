package com.maklumi.audio

interface AudioObserver {

    enum class AudioTypeEvent(val path: String) {
        MUSIC_TITLE("audio/10112013.wav"),
        MUSIC_TOWN("audio/Magic Town_0.mp3"),
        MUSIC_TOPWORLD("audio/n3535n5n335n35nj.ogg"),
        MUSIC_CASTLEDOOM("audio/Dark chamber.mp3"),
        MUSIC_INTRO_CUTSCENE("audio/Takeover_5.mp3"),
        MUSIC_BATTLE("audio/Random Battle.mp3")
    }

    enum class AudioCommand {
        MUSIC_LOAD, MUSIC_PLAY_ONCE, MUSIC_PLAY_LOOP, MUSIC_STOP,
        SOUND_LOAD, SOUND_PLAY_ONCE, SOUND_PLAY_LOOP, SOUND_STOP
    }

    fun onNotify(command: AudioCommand, event: AudioTypeEvent)
}