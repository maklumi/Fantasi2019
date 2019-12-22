package com.maklumi

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2

object MapManager {

    const val unitScale = 1f / 16f

    var isNewMapLoaded = false

    lateinit var gameMap: Map

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

    fun loadMap(mapType: MapFactory.MapType) {
        gameMap = MapFactory.getMap(mapType)
        isNewMapLoaded = true
    }

    fun setClosestStartPosition(pos: Vector2) { // in world unit
        gameMap.setClosestStartPosition(pos)
    }

}