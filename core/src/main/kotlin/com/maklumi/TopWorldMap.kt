package com.maklumi

import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TOPWORLD
import com.maklumi.sfx.ParticleEffectFactory
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE

class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, "maps/topworld.tmx") {

    init {
        getParticleEffectSpawnPositions(LANTERN_FIRE).forEach { position ->
            mapParticleEffects.add(ParticleEffectFactory.get(ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE, position))
        }
    }

    override fun playMusic() {
        notify(MUSIC_LOAD, MUSIC_TOPWORLD)
        notify(MUSIC_PLAY_LOOP, MUSIC_TOPWORLD)
    }

    override fun stopMusic() {
        notify(MUSIC_STOP, MUSIC_TOPWORLD)
    }
}