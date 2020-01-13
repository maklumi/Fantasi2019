package com.maklumi.battle

import com.maklumi.Entity
import java.util.*

object MonsterFactory {

    enum class MonsterEntityType {
        MONSTER001,
    }

    private val configs = Entity.getEntityConfigs("scripts/monsters.json")
    private val entities: Hashtable<String, Entity> = Entity.initEntities(configs)

    fun getMonster(type: MonsterEntityType): Entity? {
        val entity = entities[type.toString()] ?: return null
        return Entity(entity)
    }

}