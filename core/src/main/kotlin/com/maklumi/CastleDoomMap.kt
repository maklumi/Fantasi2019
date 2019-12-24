package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array

class CastleDoomMap : Map(MapFactory.MapType.CASTLE_OF_DOOM, "maps/castle_of_doom.tmx") {

    private val mapEntities = Array<Entity>()

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
    }

}