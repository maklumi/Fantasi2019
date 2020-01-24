package com.maklumi.sfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.*

object ParticleEffectFactory {

    enum class ParticleEffectType(val fullFilePath: String) {
        CANDLE_FIRE("sfx/candle.p"),
        LANTERN_FIRE("sfx/candle.p"),
        LAVA_SMOKE("sfx/smoke.p"),
        WAND_ATTACK(""),
    }

    fun get(type: ParticleEffectType, position: Vector2): ParticleEffect? {
        val effect = ParticleEffect()
        effect.load(Gdx.files.internal(type.fullFilePath), Gdx.files.internal("sfx"))
        effect.setPosition(position.x, position.y)
        when (type) {
            CANDLE_FIRE -> effect.scaleEffect(.04f)
            LANTERN_FIRE -> effect.scaleEffect(.02f)
            LAVA_SMOKE -> effect.scaleEffect(.04f)
            WAND_ATTACK -> {
            }
        }
        effect.start()
        return effect
    }
}