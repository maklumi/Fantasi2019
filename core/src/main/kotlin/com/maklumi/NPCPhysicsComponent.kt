package com.maklumi

import com.badlogic.gdx.math.Vector2
import com.maklumi.Component.MESSAGE
import ktx.json.fromJson
import kotlin.random.Random

class NPCPhysicsComponent : PhysicsComponent() {

    override val velocity: Vector2 = Vector2(1f, 1f).scl(Random.nextInt(1, 5).toFloat())

    override fun update(entity: Entity, deltaTime: Float) {
        if (currentState == Entity.State.IMMOBILE) return

        if (isCollisionWithMapLayer(entity, nextBound) == null
                && isCollisionWithPortalLayer(nextBound) == null
                && !isCollisionWithMapEntities(entity)
                && currentState == Entity.State.WALKING) {
            setCurrentPosition(entity)
        }

        calculateNextPosition(deltaTime)
    }

    override fun receiveMessage(message: String) {
        val string: List<String> = message.split(MESSAGE_TOKEN)

        if (string.size != 2) return
        when (MESSAGE.valueOf(string[0])) {
            MESSAGE.INIT_START_POSITION -> {
                val pos = json.fromJson(string[1]) as Vector2
                currentPosition.set(pos)
                nextPosition.set(pos)
            }
            MESSAGE.CURRENT_STATE -> {
                currentState = json.fromJson(string[1])
            }
            MESSAGE.CURRENT_DIRECTION -> {
                currentDirection = json.fromJson(string[1])
            }
            else -> {
            }
        }
    }

    override fun isCollisionWithMapEntities(entity: Entity): Boolean {
        return super.isCollisionWithMapEntities(entity) or
                isCollisionWithPlayer(entity)
    }
}