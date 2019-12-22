package com.maklumi

import com.badlogic.gdx.math.Vector2
import ktx.json.fromJson

class PlayerPhysicsComponent : PhysicsComponent() {

    override fun update(entity: Entity, deltaTime: Float) {
        if (isCollisionWithMapLayer(entity, nextBound) == null
                && currentState == Entity.State.WALKING) {
            setCurrentPosition(entity)
        }

        val portalHit = isCollisionWithPortalLayer(currentBound)
        if (portalHit != null) {
            MapManager.setClosestStartPosition(currentPosition)
            val mapType = MapFactory.MapType.valueOf(portalHit.name)
            MapManager.loadMap(mapType)
        }

        calculateNextPosition(deltaTime)

        // lock and center the camera to player's position
        MapManager.camera.position.set(currentPosition.x, currentPosition.y, 0f)
        MapManager.camera.update()
    }

    override fun receiveMessage(message: String) {
        val string: List<String> = message.split(MESSAGE_TOKEN)
        //for message with 1 pair of object payload
        if (string.size != 2) return
        when {
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.INIT_START_POSITION -> {
                val pos = json.fromJson(string[1]) as Vector2
                currentPosition.set(pos)
                nextPosition.set(pos)
            }
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.CURRENT_STATE -> {
                currentState = json.fromJson(string[1])
            }
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.CURRENT_DIRECTION -> {
                currentDirection = json.fromJson(string[1])
            }
        }
    }
}