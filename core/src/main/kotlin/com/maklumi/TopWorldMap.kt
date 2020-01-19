package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TOPWORLD

class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, "maps/topworld.tmx") {

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
        mapQuestEntities.forEach { it.update(batch, delta) }
    }

    override fun playMusic() {
        notify(MUSIC_LOAD, MUSIC_TOPWORLD)
        notify(MUSIC_PLAY_LOOP, MUSIC_TOPWORLD)
    }

    override fun stopMusic() {
        notify(MUSIC_STOP, MUSIC_TOPWORLD)
    }
}