package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.maklumi.Component.MESSAGE
import ktx.json.fromJson

class TownMap : Map(MapFactory.MapType.TOWN, "maps/town.tmx") {

    private val townGuardWalking = "scripts/town_guard_walking.json"
    private val townBlacksmith = "scripts/town_blacksmith.json"
    private val townInnKeeper = "scripts/town_innkeeper.json"
    private val townMage = "scripts/town_mage.json"

    private val mapEntities = Array<Entity>()

    init {
        // init NPC
        npcStartPositions.forEach { position ->
            val guard = initEntityNPC(position, townGuardWalking)
            mapEntities.add(guard)
        }
        // init other special NPC
        val smith = initEntitySpecial("TOWN_BLACKSMITH", townBlacksmith)
        val mage = initEntitySpecial("TOWN_MAGE", townMage)
        val keeper = initEntitySpecial("TOWN_INNKEEPER", townInnKeeper)
        mapEntities.add(smith, mage, keeper)
    }

    private fun initEntityNPC(position: Vector2, configFile: String): Entity {
        val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
        val entityConfig = json.fromJson<EntityConfig>(Gdx.files.internal(configFile))
        entity.apply {
            sendMessage(MESSAGE.LOAD_ANIMATIONS, json.toJson(entityConfig))
            sendMessage(MESSAGE.INIT_START_POSITION, json.toJson(position))
            sendMessage(MESSAGE.INIT_STATE, json.toJson(entityConfig.state))
            sendMessage(MESSAGE.INIT_DIRECTION, json.toJson(entityConfig.direction))
        }
        return entity
    }

    private fun initEntitySpecial(positionName: String, configFile: String): Entity {
        val position =
                if (specialNPCStartPositions.containsKey(positionName))
                    specialNPCStartPositions[positionName]!!
                else
                    Vector2()

        return initEntityNPC(position, configFile)
    }

    override fun updateMapEntities(batch: Batch, delta: Float) {
        mapEntities.forEach { it.update(batch, delta) }
    }
}