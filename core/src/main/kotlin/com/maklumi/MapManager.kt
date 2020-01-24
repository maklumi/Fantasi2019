package com.maklumi

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.maklumi.MapFactory.MapType
import com.maklumi.MapFactory.MapType.*
import com.maklumi.dialog.ComponentObserver
import com.maklumi.profile.ProfileEvent
import com.maklumi.profile.ProfileEvent.*
import com.maklumi.profile.ProfileManager
import com.maklumi.profile.ProfileObserver
import com.maklumi.sfx.ClockActor
import com.maklumi.sfx.ClockActor.TimeOfDay.*
import com.badlogic.gdx.utils.Array as gdxArray

object MapManager : ProfileObserver {

    const val unitScale = 1f / 16f

    var isNewMapLoaded = false

    private lateinit var gameMap: Map

    val currentMap: TiledMap?
        get() = gameMap.currentMap

    val collisionLayer: MapLayer?
        get() = gameMap.collisionLayer

    val portalLayer: MapLayer?
        get() = gameMap.portalLayer

    val spawnsLayer: MapLayer?
        get() = gameMap.spawnsLayer

    val questDiscoverLayer: MapLayer?
        get() = gameMap.questDiscoverLayer

    val enemySpawnLayer: MapLayer?
        get() = gameMap.enemySpawnLayer

    val currentMapType: MapType
        get() = gameMap.mapType

    val playerStartUnitScaled: Vector2
        get() = gameMap.startUnitScaled

    lateinit var camera: OrthographicCamera
    lateinit var player: Entity
    var currentSelectedEntity: Entity? = null

    var currentLightMap: MapLayer? = null
    var previousLightMap: MapLayer? = null
    private var period: ClockActor.TimeOfDay = AFTERNOON
    private var currentOpacity = 0f
    private var previousOpacity = 1f
    private var periodChanged = false

    fun loadMap(mapType: MapType) {
        previousLightMap?.opacity = 0f
        currentLightMap?.opacity = 1f
        previousLightMap = null
        currentLightMap = null
        if (this::gameMap.isInitialized) disableCurrentmapMusic()
        gameMap = MapFactory.getMap(mapType)
        enableCurrentmapMusic()

        isNewMapLoaded = true
        // unregister observers
        getCurrentMapEntities().forEach(Entity::unregisterObservers)
        clearCurrentSelectedEntity()
    }

    fun clearCurrentSelectedEntity() {
        currentSelectedEntity?.let {
            it.sendMessage(Component.MESSAGE.ENTITY_DESELECTED)
            currentSelectedEntity = null
        }
    }

    fun setClosestStartPosition(pos: Vector2) { // in world unit
        gameMap.setClosestStartPosition(pos)
    }

    fun updateMapEntities(batch: Batch, delta: Float) {
        gameMap.updateMapEntities(batch, delta)
    }

    fun updateParticleEffects(batch: Batch, delta: Float) {
        gameMap.updateParticleEffects(batch, delta)
    }

    fun getCurrentMapEntities(): gdxArray<Entity> = gameMap.mapEntities

    override fun onNotify(event: ProfileEvent) {
        when (event) {
            PROFILE_LOADED -> {
                val currentMap = ProfileManager.getProperty<MapType>("currentMapType")
                if (currentMap == null) loadMap(TOWN) else loadMap(currentMap)

                // Persisted the closest player position values for different maps
                val topWorldMapStartPosition = ProfileManager.getProperty<Vector2>("topWorldMapStartPosition")
                if (topWorldMapStartPosition != null) MapFactory.getMap(TOP_WORLD).start.set(topWorldMapStartPosition)

                val castleOfDoomMapStartPosition = ProfileManager.getProperty<Vector2>("castleOfDoomMapStartPosition")
                if (castleOfDoomMapStartPosition != null) MapFactory.getMap(CASTLE_OF_DOOM).start.set(castleOfDoomMapStartPosition)

                val townMapStartPosition = ProfileManager.getProperty<Vector2>("townMapStartPosition")
                if (townMapStartPosition != null) MapFactory.getMap(TOWN).start.set(townMapStartPosition)
            }
            SAVING_PROFILE -> {
                ProfileManager.setProperty("currentMapType", gameMap.mapType)
                ProfileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(TOP_WORLD).start)
                ProfileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(CASTLE_OF_DOOM).start)
                ProfileManager.setProperty("townMapStartPosition", MapFactory.getMap(TOWN).start)
            }
            CLEAR_CURRENT_PROFILE -> {
                MapFactory.mapTable.clear()
                ProfileManager.setProperty("currentMapType", TOWN.toString())
                ProfileManager.setProperty("topWorldMapStartPosition", MapFactory.getMap(TOP_WORLD).start)
                ProfileManager.setProperty("castleOfDoomMapStartPosition", MapFactory.getMap(CASTLE_OF_DOOM).start)
                ProfileManager.setProperty("townMapStartPosition", MapFactory.getMap(TOWN).start)
            }
        }
    }

    fun getQuestItemSpawnPositions(objectName: String, objectTaskID: String): gdxArray<Vector2> {
        return gameMap.getQuestItemSpawnPositions(objectName, objectTaskID)
    }

    fun addMapQuestEntities(entities: gdxArray<Entity>) {
        gameMap.mapQuestEntities.addAll(entities)
    }

    fun clearAllMapQuestEntities() {
        gameMap.mapQuestEntities.clear()
    }

    fun registerCurrentMapEntityObservers(observer: ComponentObserver) {
        gameMap.mapEntities.forEach { it.registerObserver(observer) }
        gameMap.mapQuestEntities.forEach { it.registerObserver(observer) }
    }

    fun unregisterCurrentMapEntityObservers() {
        gameMap.mapEntities.forEach(Entity::unregisterObservers)
        gameMap.mapQuestEntities.forEach(Entity::unregisterObservers)
    }

    fun removeMapQuestEntity(entity: Entity) {
        entity.unregisterObservers()
        @Suppress("UNCHECKED_CAST")
        val positions = ProfileManager.properties.get(entity.entityConfig.itemTypeID.toString()) as gdxArray<Vector2>?
        if (positions != null) {
            val temp = positions.first { it.x == entity.currentPosition.x && it.y == entity.currentPosition.y }
            positions.removeValue(temp, true)
            ProfileManager.properties.put(entity.entityConfig.itemTypeID.toString(), positions)
        }
        gameMap.mapQuestEntities.removeValue(entity, true)
    }

    fun getCurrentMapQuestEntities(): gdxArray<Entity> = gameMap.mapQuestEntities

    fun disableCurrentmapMusic() = gameMap.stopMusic()

    fun enableCurrentmapMusic() = gameMap.playMusic()

    private fun getCurrentLightMapLayer(timeOfDay: ClockActor.TimeOfDay): MapLayer? {
        return when (timeOfDay) {
            DAWN -> gameMap.lightMapDawnLayer
            AFTERNOON -> gameMap.lightMapAfternoonLayer
            DUSK -> gameMap.lightMapDuskLayer
            NIGHT -> gameMap.lightMapNightLayer
        }
    }

    fun updateLightMap(timeOfDay: ClockActor.TimeOfDay) {
        if (period != timeOfDay) {
            // reset
            currentOpacity = 0f
            previousOpacity = 1f
            period = timeOfDay
            periodChanged = true
            previousLightMap = currentLightMap

//            println("Time of Day CHANGED")
        }

        currentLightMap = getCurrentLightMapLayer(timeOfDay)

        if (periodChanged) {
            if (previousLightMap != null && previousOpacity != 0f) {
                previousLightMap!!.opacity = previousOpacity
                previousOpacity -= .05f
                previousOpacity = MathUtils.clamp(previousOpacity, 0f, 1f)
                if (previousOpacity <= 0f) previousLightMap = null
            }

            if (currentLightMap != null && currentOpacity != 1f) {
                currentLightMap!!.opacity = currentOpacity
                currentOpacity += .01f
                currentOpacity = MathUtils.clamp(currentOpacity, 0f, 1f)
            }
        } else {
            periodChanged = false
        }
    }
}