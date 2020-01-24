package com.maklumi

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
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

    fun loadMap(mapType: MapType) {
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

    fun getCurrentLightMapLayer(timeOfDay: ClockActor.TimeOfDay): MapLayer? {
        return when (timeOfDay) {
            DAWN -> gameMap.lightMapDawnLayer
            AFTERNOON -> null
            DUSK -> gameMap.lightMapDuskLayer
            NIGHT -> gameMap.lightMapNightLayer
        }
    }

}