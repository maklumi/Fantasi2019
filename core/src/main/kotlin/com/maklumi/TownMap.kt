package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.maklumi.Component.MESSAGE.INIT_START_POSITION
import com.maklumi.EntityFactory.EntityName.TOWN_GUARD_WALKING

class TownMap : Map(MapFactory.MapType.TOWN, "maps/town.tmx") {

    init {
        // init NPC
        npcStartPositions.forEach { position ->
            val guard = EntityFactory.getEntityByName(TOWN_GUARD_WALKING)
            guard.sendMessage(INIT_START_POSITION, json.toJson(position))
            mapEntities.add(guard)
        }
        // init other special NPC
        // except player puppet and town guard
        val others = EntityFactory.EntityName.values().drop(2)
        others.forEach { name ->
            val folk = EntityFactory.getEntityByName(name)
            initSpecialEntityPosition(folk)
            mapEntities.add(folk)
        }
    }

    override fun updateMapEntities(batch: Batch, delta: Float) {
        // #iterator() cannot be used nested, so use own index
        for (i in 0 until mapEntities.size) {
            mapEntities[i].update(batch, delta)
        }
        mapQuestEntities.forEach {
            it.update(batch, delta)
        }
    }

    private fun initSpecialEntityPosition(entity: Entity) {
        val position =
                if (specialNPCStartPositions.containsKey(entity.entityConfig.entityID))
                    specialNPCStartPositions[entity.entityConfig.entityID]!!
                else
                    Vector2()
        entity.sendMessage(INIT_START_POSITION, json.toJson(position))
    }
}