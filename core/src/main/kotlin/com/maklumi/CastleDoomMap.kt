package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.maklumi.audio.AudioObserver.AudioCommand
import com.maklumi.audio.AudioObserver.AudioTypeEvent

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, "maps/castle_of_doom.tmx") {

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
        mapQuestEntities.forEach { it.update(batch, delta) }
    }

    override fun playMusic() {
        notify(AudioCommand.MUSIC_LOAD, AudioTypeEvent.MUSIC_CASTLEDOOM)
        notify(AudioCommand.MUSIC_PLAY_LOOP, AudioTypeEvent.MUSIC_CASTLEDOOM)
    }

    override fun stopMusic() {
        notify(AudioCommand.MUSIC_STOP, AudioTypeEvent.MUSIC_CASTLEDOOM)
    }
}