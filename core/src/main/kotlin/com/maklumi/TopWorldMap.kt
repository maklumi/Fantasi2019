package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch

class TopWorldMap : Map(MapFactory.MapType.TOP_WORLD, "maps/topworld.tmx") {

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
        mapQuestEntities.forEach { it.update(batch, delta) }
    }

}