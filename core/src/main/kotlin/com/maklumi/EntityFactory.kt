package com.maklumi

object EntityFactory {

    enum class EntityType {
        PLAYER
    }

    fun getEntity(entityType: EntityType): Entity {
        when (entityType) {
            EntityType.PLAYER -> {
                return Entity(PlayerInputComponent(), PlayerPhysicsComponent(), PlayerGraphicsComponent())
            }
        }
    }
}
