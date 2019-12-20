package com.maklumi

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class PhysicsComponent {

    private val velocity = Vector2(4f, 4f)
    val currentPosition = Vector2()
    private val nextPosition = Vector2()
    private val temp = Rectangle()

    val currentBound: Rectangle
        get() = Rectangle(currentPosition.x, currentPosition.y, 16f, 8f)
    private val nextBound: Rectangle
        get() = Rectangle(nextPosition.x, nextPosition.y, 16f, 8f)

    fun initStartPosition(pos: Vector2) {
        currentPosition.set(pos)
        nextPosition.set(pos)
    }

    fun calculateNextPosition(currentDirection: Entity.Direction, deltaTime: Float) {
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

    private fun setCurrentPosition() {
        currentPosition.set(nextPosition)
    }

    fun update() {
        if (isCollisionWithMapLayer(MapManager.collisionLayer, nextBound) == null) {
            setCurrentPosition()
        }
    }

    fun isCollisionWithMapLayer(mapLayer: MapLayer?, rect: Rectangle): MapObject? {
        if (mapLayer == null) return null

        //Convert rectangle (in world unit) to mapLayer coordinates (in pixels)
        temp.setPosition(rect.x / MapManager.unitScale, rect.y / MapManager.unitScale)
        temp.setSize(rect.width, rect.height)

        return mapLayer.objects.firstOrNull {
            temp.overlaps((it as RectangleMapObject).rectangle)
        }
    }
}