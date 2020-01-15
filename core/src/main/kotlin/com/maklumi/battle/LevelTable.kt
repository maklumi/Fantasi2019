package com.maklumi.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.fromJson
import ktx.json.readValue
import java.util.*

class LevelTable {
    var levelID: String = "1"
    var xpMax: Int = 0
    var hpMax: Int = 0
    var mpMax: Int = 0

    companion object {

        fun getLevelTable(path: String): List<LevelTable> {
            val json = Json()
            val list = json.fromJson<ArrayList<JsonValue>>(Gdx.files.internal(path))
            return list.map { json.readValue<LevelTable>(it) }
        }
    }
}