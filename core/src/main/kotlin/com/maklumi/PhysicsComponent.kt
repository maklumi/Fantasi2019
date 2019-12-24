package com.maklumi

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

abstract class PhysicsComponent : Component {

    private val velocity = Vector2(4f, 4f)
    protected val nextPosition = Vector2()
    protected val currentPosition = Vector2()
    protected var currentState = Entity.State.IDLE
    protected var currentDirection = Entity.Direction.DOWN
    val currentBound: Rectangle
        get() = Rectangle(currentPosition.x, currentPosition.y, 16f, 8f)
    protected val nextBound: Rectangle
        get() = Rectangle(nextPosition.x, nextPosition.y, 16f, 8f)
    private val temp = Rectangle()


    abstract fun update(entity: Entity, deltaTime: Float)

    protected fun isCollisionWithMapLayer(entity: Entity, rect: Rectangle): MapObject? {
        val mapLayer = MapManager.collisionLayer ?: return null

        //Convert rectangle (in world unit) to mapLayer coordinates (in pixels)
        temp.setPosition(rect.x / MapManager.unitScale, rect.y / MapManager.unitScale)
        temp.setSize(rect.width, rect.height)

        return mapLayer.objects.firstOrNull {
            temp.overlaps((it as RectangleMapObject).rectangle)
        }.also {
            if (it != null) {
                entity.sendMessage(Component.MESSAGE.COLLISION_WITH_MAP)
            }
        }
    }

    protected fun isCollisionWithPortalLayer(rect: Rectangle): MapObject? {
        val mapLayer = MapManager.portalLayer ?: return null

        //Convert rectangle (in world unit) to mapLayer coordinates (in pixels)
        temp.setPosition(rect.x / MapManager.unitScale, rect.y / MapManager.unitScale)
        temp.setSize(rect.width, rect.height)

        return mapLayer.objects.firstOrNull {
            temp.overlaps((it as RectangleMapObject).rectangle)
        }
    }

    protected fun setCurrentPosition(entity: Entity) {
        currentPosition.set(nextPosition)
        entity.sendMessage(Component.MESSAGE.CURRENT_POSITION, json.toJson(currentPosition))
    }

    protected fun calculateNextPosition(deltaTime: Float) {
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

}