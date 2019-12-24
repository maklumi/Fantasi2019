package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array as gdxArray

abstract class Map(private var mapType: MapFactory.MapType, path: String) {

    companion object {
        const val unitScale = 1f / 16f
        private const val MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER"
        private const val MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER"
        private const val MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER"
        private const val PLAYER_START = "PLAYER_START"
        private const val NPC_START = "NPC_START"
    }

    var currentMap: TiledMap? = null
    var collisionLayer: MapLayer? = null
    var portalLayer: MapLayer? = null
    var spawnsLayer: MapLayer? = null

    private val start = Vector2() // last known position on this map in pixels
    val startUnitScaled: Vector2  // in world unit
        get() = Vector2(start).scl(unitScale)

    val npcStartPositions: gdxArray<Vector2>
    protected val specialNPCStartPositions: MutableMap<String, Vector2>

    init {
        loadMap(path)
        npcStartPositions = getNPCStartPositions()
        specialNPCStartPositions = getOtherNPCStartPositions()
//        print("Map-init: $specialNPCStartPositions")
    }

    private fun loadMap(path: String) {
        Utility.loadMapAsset(path)
        currentMap = Utility.getMapAsset(path)
        collisionLayer = currentMap?.layers?.get(MAP_COLLISION_LAYER)
        portalLayer = currentMap?.layers?.get(MAP_PORTAL_LAYER)
        spawnsLayer = currentMap?.layers?.get(MAP_SPAWNS_LAYER)
        setClosestStartPosition(Vector2())
        println("Map-loadmap: loadmap($mapType)")
    }

    fun setClosestStartPosition(worldOrigin: Vector2) {
        val origin = worldOrigin.scl(1 / unitScale) // pixel
        val mapStart = Vector2() // pixel
        val closestStartPosition = Vector2() // pixel
        var shortest = Float.MAX_VALUE

        spawnsLayer!!.objects
                .filter { it.name.equals(PLAYER_START, true) }
                .forEach {
                    (it as RectangleMapObject).rectangle.getPosition(mapStart)
                    val distance = origin.dst2(mapStart)
                    if (distance < shortest) {
                        closestStartPosition.set(mapStart)
                        shortest = distance
                    }
                }

        start.set(closestStartPosition)
    }

    abstract fun updateMapEntities(batch: Batch, delta: Float)

    private fun getNPCStartPositions(): gdxArray<Vector2> {
        val positions = gdxArray<Vector2>()

        spawnsLayer?.objects?.filter { it.name.equals(NPC_START, true) }
                ?.forEach {
                    val rectCenter = Vector2()
                    (it as RectangleMapObject).rectangle.getCenter(rectCenter)
                    // convert from map coordinates
                    rectCenter.scl(unitScale)
                    positions.add(rectCenter)
                }
        return positions
    }

    private fun getOtherNPCStartPositions(): MutableMap<String, Vector2> {
        val positions = mutableMapOf<String, Vector2>()

        spawnsLayer?.objects?.filter {
            !it.name.equals(NPC_START, true) &&
                    !it.name.equals(PLAYER_START, true)
        }
                ?.forEach {
                    val rectCenter = Vector2()
                    (it as RectangleMapObject).rectangle.getCenter(rectCenter)
                    // convert from map coordinates
                    rectCenter.scl(unitScale)
                    positions[it.name] = rectCenter
                }
        return positions
    }
}