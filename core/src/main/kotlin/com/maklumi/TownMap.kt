package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.maklumi.Component.MESSAGE

class TownMap : Map(MapFactory.MapType.TOWN, "maps/town.tmx") {

    private val townGuardWalking = "scripts/town_guard_walking.json"
    private val townBlacksmith = "scripts/town_blacksmith.json"
    private val townInnKeeper = "scripts/town_innkeeper.json"
    private val townMage = "scripts/town_mage.json"
    private val townFolk = "scripts/town_folk.json"

    init {
        // init NPC
        npcStartPositions.forEach { position ->
            val guard = initEntityNPC(position, Entity.getEntityConfig(townGuardWalking))
            mapEntities.add(guard)
        }
        // init other special NPC
        val smith = initEntitySpecial(Entity.getEntityConfig(townBlacksmith))
        val mage = initEntitySpecial(Entity.getEntityConfig(townMage))
        val keeper = initEntitySpecial(Entity.getEntityConfig(townInnKeeper))
        mapEntities.add(smith, mage, keeper)
        // town folks have their configs in one file
        Entity.getEntityConfigs(townFolk)
                .forEach { mapEntities.add(initEntitySpecial(it)) }
    }

    private fun initEntityNPC(position: Vector2, entityConfig: EntityConfig): Entity {
        val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
        entity.apply {
            this.entityConfig = entityConfig
            sendMessage(MESSAGE.LOAD_ANIMATIONS, json.toJson(entityConfig))
            sendMessage(MESSAGE.INIT_START_POSITION, json.toJson(position))
            sendMessage(MESSAGE.INIT_STATE, json.toJson(entityConfig.state))
            sendMessage(MESSAGE.INIT_DIRECTION, json.toJson(entityConfig.direction))
        }
        return entity
    }

    private fun initEntitySpecial(entityConfig: EntityConfig): Entity {
        val position =
                if (specialNPCStartPositions.containsKey(entityConfig.entityID))
                    specialNPCStartPositions[entityConfig.entityID]!!
                else
                    Vector2()

        return initEntityNPC(position, entityConfig)
    }

    override fun updateMapEntities(batch: Batch, delta: Float) {
        // #iterator() cannot be used nested, so use own index
        for (i in 0 until mapEntities.size) {
            mapEntities[i].update(batch, delta)
        }
    }
}