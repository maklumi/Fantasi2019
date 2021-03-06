package com.maklumi

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.Ray
import com.maklumi.Component.MESSAGE
import com.maklumi.MapManager.unitScale
import com.maklumi.dialog.ComponentSubject
import com.badlogic.gdx.utils.Array as gdxArray

abstract class PhysicsComponent : Component, ComponentSubject() {

    protected open val velocity = Vector2(4f, 4f)
    protected val nextPosition = Vector2()
    protected val currentPosition = Vector2()
    protected var currentState = Entity.State.IDLE
    protected var currentDirection = Entity.Direction.DOWN
    val currentBound: Rectangle
        get() = Rectangle(currentPosition.x, currentPosition.y, 1f, 0.5f)
    protected val nextBound: Rectangle
        get() = Rectangle(nextPosition.x, nextPosition.y, 1f, 0.5f)

    protected var selectionRay = Ray(Vector3.Zero, Vector3.Zero)
    protected var selectRayMaximumDistance = 32.0f // 32 world units

    abstract fun update(entity: Entity, deltaTime: Float)

    protected fun isCollisionWithMapLayer(entity: Entity, rect: Rectangle): MapObject? {
        val mapLayer = MapManager.collisionLayer ?: return null
        rect.convertRectWorldToPixel()
        return mapLayer.objects.firstOrNull {
            rect.overlaps((it as RectangleMapObject).rectangle)
        }.also {
            if (it != null) {
                entity.sendMessage(MESSAGE.COLLISION_WITH_MAP)
            }
        }
    }

    protected fun isCollisionWithPortalLayer(rect: Rectangle): MapObject? {
        val mapLayer = MapManager.portalLayer ?: return null
        rect.convertRectWorldToPixel()
        return mapLayer.objects.firstOrNull {
            rect.overlaps((it as RectangleMapObject).rectangle)
        }
    }

    protected open fun isCollisionWithMapEntities(entity: Entity): Boolean {
        val mapEntities = MapManager.getCurrentMapEntities()
        val questEntities = MapManager.getCurrentMapQuestEntities()
        val entities = gdxArray<Entity>()
        entities.addAll(mapEntities)
        entities.addAll(questEntities)
        var isCollisionWithMapEntities = false

        val iterable = gdxArray.ArrayIterable(entities)
        for (mapEntity in iterable) {
            // check for testing against self
            if (mapEntity == entity) continue

            if (nextBound.overlaps(mapEntity.getCurrentBoundingBox())) {
                entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY)
                isCollisionWithMapEntities = true
                break
            }
        }
        entities.clear()
        return isCollisionWithMapEntities
    }

    fun isCollisionWithPlayer(entity: Entity): Boolean {
        val player = MapManager.player
        if (entity == player) return false
        val entityBound = entity.physicsComponent.nextBound
        val playerBound = player.getCurrentBoundingBox()
        if (entityBound.overlaps(playerBound)) {
            entity.sendMessage(MESSAGE.COLLISION_WITH_ENTITY)
            return true
        }
        return false
    }

    protected fun Rectangle.convertRectWorldToPixel() {
        this.setPosition(this.x / unitScale, this.y / unitScale)
        this.setSize(this.width / unitScale, this.height / unitScale)
    }

    protected fun setCurrentPosition(entity: Entity) {
        currentPosition.set(nextPosition)
        entity.sendMessage(MESSAGE.CURRENT_POSITION, json.toJson(currentPosition))
    }

    protected fun calculateNextPosition(deltaTime: Float) {
        if (deltaTime > .7f) return
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

    override fun dispose() {
    }
}