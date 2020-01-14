package com.maklumi.battle

import com.maklumi.Entity
import java.util.*

object MonsterFactory {

    enum class MonsterEntityType {
        MONSTER001, MONSTER002, MONSTER003, MONSTER004, MONSTER005,
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