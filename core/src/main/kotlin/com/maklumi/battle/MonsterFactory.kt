package com.maklumi.battle

import com.maklumi.Entity
import java.util.*

object MonsterFactory {

    enum class MonsterEntityType {
        MONSTER001, MONSTER002, MONSTER003, MONSTER004, MONSTER005,
        MONSTER006, MONSTER007, MONSTER008, MONSTER009, MONSTER010,
        MONSTER011, MONSTER012, MONSTER013, MONSTER014, MONSTER015,
        MONSTER016, MONSTER017, MONSTER018, MONSTER019, MONSTER020,
        MONSTER021, MONSTER022, MONSTER023, MONSTER024, MONSTER025,
        MONSTER026, MONSTER027, MONSTER028, MONSTER029, MONSTER030,
        MONSTER031, MONSTER032, MONSTER033, MONSTER034, MONSTER035,
        MONSTER036, MONSTER037, MONSTER038, MONSTER039, MONSTER040,
        MONSTER041, MONSTER042, NONE
    }

    private val configs = Entity.getEntityConfigs("scripts/monsters.json")
    private val entities: Hashtable<String, Entity> = Entity.initEntities(configs)
    private val monsterZones = MonsterZone.getMonsterZones("scripts/monster_zones.json")

    private fun getMonster(type: MonsterEntityType): Entity? {
        val entity = entities[type.toString()] ?: return null
        return Entity(entity)
    }

    fun getRandomMonster(monsterZoneID: Int): Entity? {
        val monsters = monsterZones[monsterZoneID.toString()]
        val monsterType = monsters?.random() ?: return null
        return getMonster(monsterType)
    }
}