package com.maklumi

import com.badlogic.gdx.math.Vector2
import com.maklumi.Component.MESSAGE.INIT_START_POSITION
import com.maklumi.EntityFactory.EntityName.TOWN_GUARD_WALKING
import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TOWN
import com.maklumi.sfx.ParticleEffectFactory
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.LANTERN_FIRE

class TownMap : Map(MapFactory.MapType.TOWN, "maps/town.tmx") {

    init {
        // init NPC
        npcStartPositions.forEach { position ->
            val guard = EntityFactory.getEntityByName(TOWN_GUARD_WALKING)
            guard.sendMessage(INIT_START_POSITION, json.toJson(position))
            mapEntities.add(guard)
        }
        // init other special NPC
        // except player puppet and town guard
        val others = EntityFactory.EntityName.values().drop(2)
        others.forEach { name ->
            val folk = EntityFactory.getEntityByName(name)
            initSpecialEntityPosition(folk)
            mapEntities.add(folk)
        }

        getParticleEffectSpawnPositions(CANDLE_FIRE).forEach { position ->
            mapParticleEffects.add(ParticleEffectFactory.get(CANDLE_FIRE, position))
        }

        getParticleEffectSpawnPositions(LANTERN_FIRE).forEach { position ->
            mapParticleEffects.add(ParticleEffectFactory.get(LANTERN_FIRE, position))
        }
    }

    private fun initSpecialEntityPosition(entity: Entity) {
        val position =
                if (specialNPCStartPositions.containsKey(entity.entityConfig.entityID))
                    specialNPCStartPositions[entity.entityConfig.entityID]!!
                else
                    Vector2()
        entity.sendMessage(INIT_START_POSITION, json.toJson(position))
    }

    override fun playMusic() {
        notify(MUSIC_LOAD, MUSIC_TOWN)
        notify(MUSIC_PLAY_LOOP, MUSIC_TOWN)
    }

    override fun stopMusic() {
        notify(MUSIC_STOP, MUSIC_TOWN)
    }

}