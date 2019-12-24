package com.maklumi

import com.badlogic.gdx.Gdx
import ktx.json.fromJson

object EntityFactory {

    enum class EntityType {
        PLAYER, DEMO_PLAYER, NPC
    }

    private const val PLAYER_CONFIG = "scripts/player.json"

    fun getEntity(entityType: EntityType): Entity {
        return when (entityType) {
            EntityType.PLAYER -> {
                Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
                        .also {
                            val entityConfig = json.fromJson<EntityConfig>(Gdx.files.internal(PLAYER_CONFIG))
                            it.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, json.toJson(entityConfig))
                        }
            }
            EntityType.DEMO_PLAYER -> {
                Entity(NPCInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            }
            EntityType.NPC -> {
                Entity(NPCInputComponent(), NPCPhysicsComponent(), NPCGraphicsComponent())
            }
        }
    }
}
