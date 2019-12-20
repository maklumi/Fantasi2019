package com.maklumi

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2

object MapManager {

    const val unitScale = 1f / 16f
    private const val MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER"
    private const val MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER"
    private const val MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER"
    private const val PLAYER_START = "PLAYER_START"
    private const val TOWN = "TOWN"
    private const val TOP_WORLD = "TOP_WORLD"
    private const val CASTLE_OF_DOOM = "CASTLE_OF_DOOM"

    var currentMap: TiledMap? = null
    var collisionLayer: MapLayer? = null
    var portalLayer: MapLayer? = null
    var spawnsLayer: MapLayer? = null

    private val startPositionTable = HashMap<String, Vector2>(3) // key=map name, value = in pixels coord
    var currentMapName: String = TOWN
    private val mapPathTable = hashMapOf(
            TOWN to "maps/town.tmx",
            TOP_WORLD to "maps/topworld.tmx",
            CASTLE_OF_DOOM to "maps/castle_of_doom.tmx")

    private val playerStart = Vector2()
    val playerStartUnitScaled: Vector2
        get() = playerStart.cpy().scl(unitScale)


    fun setClosestStartPosition(fromInWorldUnit: Vector2) {
        val testPosition = Vector2()
        val closestStartPosition = Vector2()
        var shortest = Float.MAX_VALUE
        val fromInPixel = fromInWorldUnit.scl(1 / unitScale)

        spawnsLayer!!.objects
                .filter { it.name.equals(PLAYER_START, true) }
                .forEach {
                    (it as RectangleMapObject).rectangle.getPosition(testPosition)
                    val dist = fromInPixel.dst2(testPosition)
                    if (dist < shortest) {
                        closestStartPosition.set(testPosition)
                        shortest = dist
                    }
                }

        startPositionTable[currentMapName] = closestStartPosition.cpy()
    }

    fun loadMap(name: String) {
        // prepare the map
        currentMapName = name

        val path = mapPathTable[name] as String
        Utility.loadMapAsset(path)
        currentMap = Utility.getMapAsset(path)
        collisionLayer = currentMap?.layers?.get(MAP_COLLISION_LAYER)
        portalLayer = currentMap?.layers?.get(MAP_PORTAL_LAYER)
        spawnsLayer = currentMap?.layers?.get(MAP_SPAWNS_LAYER)

        // prepare the player
        if (!startPositionTable.containsKey(currentMapName) || playerStartUnitScaled == Vector2()) {
            setClosestStartPosition(Vector2())
        }
        val start = startPositionTable[currentMapName]
        playerStart.set(start)
    }

}