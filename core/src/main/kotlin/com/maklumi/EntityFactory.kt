package com.maklumi

import com.badlogic.gdx.Gdx
import ktx.json.fromJson
import java.util.*
import com.badlogic.gdx.utils.Array as gdxArray

object EntityFactory {

    enum class EntityType {
        PLAYER, PLAYER_PUPPET, NPC
    }

    enum class EntityName {
        PLAYER_PUPPET,
        TOWN_GUARD_WALKING,
        TOWN_BLACKSMITH,
        TOWN_MAGE,
        TOWN_INNKEEPER,
        TOWN_FOLK1, TOWN_FOLK2, TOWN_FOLK3, TOWN_FOLK4, TOWN_FOLK5,
        TOWN_FOLK6, TOWN_FOLK7, TOWN_FOLK8, TOWN_FOLK9, TOWN_FOLK10,
        TOWN_FOLK11, TOWN_FOLK12, TOWN_FOLK13, TOWN_FOLK14, TOWN_FOLK15,
        FIRE;

        operator fun invoke(): String = this.toString()
    }

    const val PLAYER_CONFIG = "scripts/player.json"
    private const val TOWN_GUARD_WALKING_CONFIG = "scripts/town_guard_walking.json"
    private const val TOWN_BLACKSMITH_CONFIG = "scripts/town_blacksmith.json"
    private const val TOWN_MAGE_CONFIG = "scripts/town_mage.json"
    private const val TOWN_INNKEEPER_CONFIG = "scripts/town_innkeeper.json"
    private const val TOWN_FOLK_CONFIGS = "scripts/town_folk.json"
    private const val ENVIRONMENTAL_ENTITY_CONFIGS = "scripts/environmental_entities.json"


    private val configTable = Hashtable<String, EntityConfig>()

    init {
        val townFolkConfigs: gdxArray<EntityConfig> = Entity.getEntityConfigs(TOWN_FOLK_CONFIGS)
        townFolkConfigs.forEach { config ->
            configTable[config.entityID] = config
        }

        val environmentalEntityConfigs = Entity.getEntityConfigs(ENVIRONMENTAL_ENTITY_CONFIGS)
        environmentalEntityConfigs.forEach { config ->
            configTable[config.entityID] = config
        }

        configTable[EntityName.TOWN_GUARD_WALKING()] = Entity.loadEntityConfigBy(TOWN_GUARD_WALKING_CONFIG)
        configTable[EntityName.TOWN_BLACKSMITH()] = Entity.loadEntityConfigBy(TOWN_BLACKSMITH_CONFIG)
        configTable[EntityName.TOWN_MAGE()] = Entity.loadEntityConfigBy(TOWN_MAGE_CONFIG)
        configTable[EntityName.TOWN_INNKEEPER()] = Entity.loadEntityConfigBy(TOWN_INNKEEPER_CONFIG)
        configTable[EntityName.PLAYER_PUPPET()] = Entity.loadEntityConfigBy(PLAYER_CONFIG)
    }

    fun getEntity(entityType: EntityType): Entity {
        return when (entityType) {
            EntityType.PLAYER -> {
                Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
                        .also {
                            val entityConfig = json.fromJson<EntityConfig>(Gdx.files.internal(PLAYER_CONFIG))
                            it.entityConfig = entityConfig
                            it.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entityConfig))
                        }
            }
            EntityType.PLAYER_PUPPET -> {
                Entity(NPCInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            }
            EntityType.NPC -> {
                Entity(NPCInputComponent(), NPCPhysicsComponent(), NPCGraphicsComponent())
            }
        }
    }

    fun getEntityByName(entityName: EntityName): Entity {
        val config = EntityConfig(configTable[entityName.toString()]!!)
        return Entity.initEntity(config)
    }
}
