package com.maklumi

import com.maklumi.audio.AudioObserver.AudioCommand
import com.maklumi.audio.AudioObserver.AudioTypeEvent
import com.maklumi.sfx.ParticleEffectFactory
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, "maps/castle_of_doom.tmx") {

    init {
        getParticleEffectSpawnPositions(CANDLE_FIRE).forEach { position ->
            mapParticleEffects.add(ParticleEffectFactory.get(CANDLE_FIRE, position))
        }
    }

    override fun playMusic() {
        notify(AudioCommand.MUSIC_LOAD, AudioTypeEvent.MUSIC_CASTLEDOOM)
        notify(AudioCommand.MUSIC_PLAY_LOOP, AudioTypeEvent.MUSIC_CASTLEDOOM)
    }

    override fun stopMusic() {
        notify(AudioCommand.MUSIC_STOP, AudioTypeEvent.MUSIC_CASTLEDOOM)
    }
}