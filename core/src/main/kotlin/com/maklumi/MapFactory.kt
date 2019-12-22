package com.maklumi

import com.maklumi.Map as GameMap

object MapFactory {

    private val mapTable = hashMapOf<MapType, GameMap>()

    enum class MapType { TOWN, TOP_WORLD, CASTLE_OF_DOOM }

    fun getMap(mapType: MapType): GameMap {
        return when (mapType) {
            MapType.TOP_WORLD -> {
                if (!mapTable.containsKey(mapType)) mapTable[MapType.TOP_WORLD] = TopWorldMap()
                mapTable[MapType.TOP_WORLD]!!
            }
            MapType.TOWN -> {
                if (!mapTable.containsKey(mapType)) mapTable[MapType.TOWN] = TownMap()
                mapTable[MapType.TOWN]!!
            }
            MapType.CASTLE_OF_DOOM -> {
                if (!mapTable.containsKey(mapType)) mapTable[MapType.CASTLE_OF_DOOM] = CastleDoomMap()
                mapTable[MapType.CASTLE_OF_DOOM]!!
            }
        }
    }

}