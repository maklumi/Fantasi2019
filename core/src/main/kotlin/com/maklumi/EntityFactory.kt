package com.maklumi

object EntityFactory {

    enum class EntityType {
        PLAYER, DEMO_PLAYER
    }

    fun getEntity(entityType: EntityType): Entity {
        return when (entityType) {
            EntityType.PLAYER -> {
                Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            }
            EntityType.DEMO_PLAYER -> {
                Entity(NPCInputComponent(), NPCPhysicsComponent(), NPCGraphicsComponent())
            }
        }
    }
}
