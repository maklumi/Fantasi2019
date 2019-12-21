package com.maklumi

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.maklumi.MapManager.camera
import ktx.json.fromJson

class PhysicsComponent : Component {

    private val velocity = Vector2(4f, 4f)
    private val nextPosition = Vector2()
    private val temp = Rectangle()
    private val currentPosition = Vector2()
    private var currentState = Entity.State.IDLE
    private var currentDirection = Entity.Direction.DOWN

    val currentBound: Rectangle
        get() = Rectangle(currentPosition.x, currentPosition.y, 16f, 8f)
    private val nextBound: Rectangle
        get() = Rectangle(nextPosition.x, nextPosition.y, 16f, 8f)


    private fun calculateNextPosition(deltaTime: Float) {
        if (deltaTime == 0f) return // don't know why, else velocity become NaN
        var tempX = currentPosition.x
        var tempY = currentPosition.y
        nextPosition.set(tempX, tempY)

        velocity.scl(deltaTime)

        when (currentDirection) {
            Entity.Direction.LEFT -> tempX -= velocity.x
            Entity.Direction.RIGHT -> tempX += velocity.x
            Entity.Direction.UP -> tempY += velocity.y
            Entity.Direction.DOWN -> tempY -= velocity.y
        }

        nextPosition.set(tempX, tempY)

        velocity.scl(1 / deltaTime)
    }

    private fun setCurrentPosition(entity: Entity) {
        currentPosition.set(nextPosition)
        entity.sendMessage(Component.MESSAGE.CURRENT_POSITION, json.toJson(currentPosition))
    }

    fun update(entity: Entity, deltaTime: Float) {
        if (isCollisionWithMapLayer(MapManager.collisionLayer, nextBound) == null
                && currentState == Entity.State.WALKING) {
            setCurrentPosition(entity)
        }

        isCollisionWithPortalLayer(currentBound)

        calculateNextPosition(deltaTime)

        // lock and center the camera to player's position
        camera.position.set(currentPosition.x, currentPosition.y, 0f)
        camera.update()
    }

    private fun isCollisionWithMapLayer(mapLayer: MapLayer?, rect: Rectangle): MapObject? {
        if (mapLayer == null) return null

        //Convert rectangle (in world unit) to mapLayer coordinates (in pixels)
        temp.setPosition(rect.x / MapManager.unitScale, rect.y / MapManager.unitScale)
        temp.setSize(rect.width, rect.height)

        return mapLayer.objects.firstOrNull {
            temp.overlaps((it as RectangleMapObject).rectangle)
        }
    }

    private fun isCollisionWithPortalLayer(rect: Rectangle) {
        val portalHit = isCollisionWithMapLayer(MapManager.portalLayer, rect)

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