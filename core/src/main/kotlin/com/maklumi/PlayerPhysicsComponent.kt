package com.maklumi

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.maklumi.Component.MESSAGE
import com.maklumi.MapManager.camera
import com.maklumi.MapManager.getCurrentMapEntities
import com.maklumi.dialog.ComponentObserver.ComponentEvent
import ktx.json.fromJson
import com.badlogic.gdx.utils.Array as gdxArray

class PlayerPhysicsComponent : PhysicsComponent() {

    private var mouseSelectCoordinates = Vector3()
    private var isMouseSelectEnabled = false
    private var previousDiscovery = ""

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

        updateDiscoverLayerActivation(currentBound)

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
        val mapEntities = getCurrentMapEntities()
        val questEntities = MapManager.getCurrentMapQuestEntities()
        val currentEntities = gdxArray<Entity>()
        currentEntities.addAll(mapEntities)
        currentEntities.addAll(questEntities)
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
                    notify(json.toJson(mapEntity.entityConfig), ComponentEvent.LOAD_CONVERSATION)
                }
            }
        }

        isMouseSelectEnabled = false
        currentEntities.clear()
    }

    private fun updateDiscoverLayerActivation(rect: Rectangle): Boolean {
        val mapDiscoverLayer = MapManager.questDiscoverLayer ?: return false

        var rectangle: Rectangle?

        for (mapObject in mapDiscoverLayer.objects) {
            if (mapObject is RectangleMapObject) {
                rectangle = mapObject.rectangle
                rect.convertRectWorldToPixel()
                if (rect.overlaps(rectangle)) {
                    val questID = mapObject.getName() ?: return false
                    val questTaskID = mapObject.getProperties().get("taskID") as String?
                    val msg = questID + MESSAGE_TOKEN + questTaskID
                    // make sure only notify once
                    if (previousDiscovery.equals(msg, true)) return true
                    notify(json.toJson(msg), ComponentEvent.QUEST_LOCATION_DISCOVERED)
                    previousDiscovery = msg
                    println("PlayerPhysicComp126 Discover Area Activated")
                    return true
                }
            }
        }
        return false
    }
}