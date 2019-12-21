package com.maklumi

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.json.fromJson

class PlayerPhysicsComponent : PhysicsComponent() {

    private val nextBound: Rectangle
        get() = Rectangle(nextPosition.x, nextPosition.y, 16f, 8f)

    override fun update(entity: Entity, deltaTime: Float) {
        if (isCollisionWithMapLayer(entity, nextBound) == null
                && currentState == Entity.State.WALKING) {
            setCurrentPosition(entity)
        }

        isCollisionWithPortalLayer(currentBound)

        calculateNextPosition(deltaTime)

        // lock and center the camera to player's position
        MapManager.camera.position.set(currentPosition.x, currentPosition.y, 0f)
        MapManager.camera.update()
    }

    private fun isCollisionWithPortalLayer(rect: Rectangle) {
        val mapLayer = MapManager.portalLayer ?: return

        //Convert rectangle (in world unit) to mapLayer coordinates (in pixels)
        temp.setPosition(rect.x / MapManager.unitScale, rect.y / MapManager.unitScale)
        temp.setSize(rect.width, rect.height)

        val portalHit = mapLayer.objects.firstOrNull {
            temp.overlaps((it as RectangleMapObject).rectangle)
        }

        if (portalHit != null) {
            MapManager.setClosestStartPosition(currentPosition)
            MapManager.loadMap(portalHit.name)
        }
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