package com.maklumi

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.maklumi.Component.MESSAGE
import com.maklumi.MapManager.camera
import com.maklumi.MapManager.getCurrentMapEntities
import com.maklumi.dialog.UIObserver.UIEvent
import ktx.json.fromJson

class PlayerPhysicsComponent : PhysicsComponent() {

    private var mouseSelectCoordinates = Vector3()
    private var isMouseSelectEnabled = false

    override fun update(entity: Entity, deltaTime: Float) {
        if (isCollisionWithMapLayer(entity, nextBound) == null
                && !isCollisionWithMapEntities(entity)
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
        camera.position.set(currentPosition.x + 5, currentPosition.y + 15, 0f) // bring map abit down
        camera.update()

        if (isMouseSelectEnabled) selectMapEntityCandidate()
    }

    override fun receiveMessage(message: String) {
        val string: List<String> = message.split(MESSAGE_TOKEN)
        //for message with 1 pair of object payload
        if (string.size != 2) return
        when {
            MESSAGE.valueOf(string[0]) == MESSAGE.INIT_START_POSITION -> {
                val pos = json.fromJson(string[1]) as Vector2
                currentPosition.set(pos)
                nextPosition.set(pos)
            }
            MESSAGE.valueOf(string[0]) == MESSAGE.CURRENT_STATE -> {
                currentState = json.fromJson(string[1])
            }
            MESSAGE.valueOf(string[0]) == MESSAGE.CURRENT_DIRECTION -> {
                currentDirection = json.fromJson(string[1])
            }
            MESSAGE.valueOf(string[0]) == MESSAGE.INIT_SELECT_ENTITY -> {
                mouseSelectCoordinates = json.fromJson(string[1])
                isMouseSelectEnabled = true
            }
        }
    }

    private fun selectMapEntityCandidate() {
        val currentEntities = getCurrentMapEntities()

        //Convert screen coordinates to world coordinates
        camera.unproject(mouseSelectCoordinates)
//        println(" PPC: Mouse Coordinates $mouseSelectCoordinates ")

        currentEntities.forEach { mapEntity ->
            //Don't break, reset all entities
            mapEntity.sendMessage(MESSAGE.ENTITY_DESELECTED)
            val entityBoundingBox = mapEntity.getCurrentBoundingBox()
            entityBoundingBox.setSize(2f) // larger area
//            println("PPC-76: Entity Candidate Location  ${mapEntityBoundingBox.getPosition(Vector2())}")
            if (entityBoundingBox.contains(mouseSelectCoordinates.x, mouseSelectCoordinates.y)) {
                //Check distance
                val source = Vector3(currentBound.x, currentBound.y, 0.0f)
                val target = Vector3(entityBoundingBox.x, entityBoundingBox.y, 0.0f)
                selectionRay.set(source, target)
                val distance = selectionRay.origin.dst(selectionRay.direction)

                if (distance <= selectRayMaximumDistance) {
                    //We have a valid entity selection
                    //Picked/Selected
//                    println("PPC87: Selected Entity ${mapEntity.entityConfig.entityID}")
                    mapEntity.sendMessage(MESSAGE.ENTITY_SELECTED)
                    notify(json.toJson(mapEntity.entityConfig), UIEvent.LOAD_CONVERSATION)
                }
            }
        }

        isMouseSelectEnabled = false
    }
}