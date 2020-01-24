package com.maklumi

import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TOPWORLD
import com.maklumi.sfx.ParticleEffectFactory
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.LAVA_SMOKE

class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, "maps/topworld.tmx") {

    init {
        getParticleEffectSpawnPositions(LANTERN_FIRE).forEach { position ->
            mapParticleEffects.add(ParticleEffectFactory.get(LANTERN_FIRE, position))
        }

        getParticleEffectSpawnPositions(LAVA_SMOKE).forEach { position ->
            mapParticleEffects.add(ParticleEffectFactory.get(LAVA_SMOKE, position))
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