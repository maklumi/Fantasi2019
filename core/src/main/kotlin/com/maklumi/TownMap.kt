package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.maklumi.Component.MESSAGE.INIT_START_POSITION
import com.maklumi.EntityFactory.EntityName.TOWN_GUARD_WALKING
import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TOWN
import com.maklumi.sfx.ParticleEffectFactory
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.CANDLE_FIRE
import com.badlogic.gdx.utils.Array as gdxArray

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

        val candlePositions = getParticleEffectSpawnPositions(CANDLE_FIRE)
        val iterable = gdxArray.ArrayIterable<Vector2>(candlePositions)
        for (position in iterable) {
            val effect = ParticleEffectFactory.getParticleEffect(CANDLE_FIRE, position)
            mapParticleEffects.add(effect)
        }
    }

    override fun updateMapEntities(batch: Batch, delta: Float) {
        // #iterator() cannot be used nested, so use own index
        for (i in 0 until mapEntities.size) {
            mapEntities[i].update(batch, delta)
        }
        mapQuestEntities.forEach {
            it.update(batch, delta)
        }
        mapParticleEffects.forEach { effect ->
            batch.begin()
            effect.draw(batch, delta)
            batch.end()
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