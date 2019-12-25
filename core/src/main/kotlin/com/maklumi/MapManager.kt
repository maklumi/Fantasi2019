package com.maklumi

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array as gdxArray

object MapManager {

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

    val playerStartUnitScaled: Vector2
        get() = gameMap.startUnitScaled

    lateinit var camera: OrthographicCamera
    lateinit var player: Entity

    fun loadMap(mapType: MapFactory.MapType) {
        gameMap = MapFactory.getMap(mapType)
        isNewMapLoaded = true
    }

    fun setClosestStartPosition(pos: Vector2) { // in world unit
        gameMap.setClosestStartPosition(pos)
    }

    fun updateMapEntities(batch: Batch, delta: Float) {
        gameMap.updateMapEntities(batch, delta)
    }

    fun getCurrentMapEntities(): gdxArray<Entity> = gameMap.mapEntities

}