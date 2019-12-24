package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array

class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, "maps/topworld.tmx") {

    private val mapEntities = Array<Entity>()

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
    }

}