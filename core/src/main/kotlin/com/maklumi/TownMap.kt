package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.Array
import com.maklumi.Component.MESSAGE
import ktx.json.fromJson

class TownMap : Map(MapFactory.MapType.TOWN, "maps/town.tmx") {

    private val townGuardWalking = "scripts/town_guard_walking.json"
    private val mapEntities = Array<Entity>()

    init {
        val entityConfig = json.fromJson<EntityConfig>(Gdx.files.internal(townGuardWalking))
        npcStartPositions.forEach { position ->
            val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
            entity.sendMessage(MESSAGE.LOAD_ANIMATIONS, json.toJson(entityConfig))
            entity.sendMessage(MESSAGE.INIT_START_POSITION, json.toJson(position))

            mapEntities.add(entity)
        }
    }

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
    }
}