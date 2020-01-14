package com.maklumi.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.maklumi.battle.MonsterFactory.MonsterEntityType
import ktx.json.fromJson
import ktx.json.readValue
import java.util.*

class MonsterZone {
    var zoneID: String? = null
    var monsters: Array<MonsterEntityType>? = null

    companion object {
        fun getMonsterZones(path: String): Hashtable<String, Array<MonsterEntityType>> {
            val json = Json()
            val monsterZones = Hashtable<String, Array<MonsterEntityType>>()
            val list = json.fromJson<ArrayList<JsonValue>>(Gdx.files.internal(path))
            for (jsonVal in list) {
                val zone = json.readValue<MonsterZone>(jsonVal)
                monsterZones[zone.zoneID] = zone.monsters
            }
            return monsterZones
        }
    }
}