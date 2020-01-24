package com.maklumi.sfx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.maklumi.sfx.ParticleEffectFactory.ParticleEffectType.*

object ParticleEffectFactory {

    enum class ParticleEffectType(val fullFilePath: String) {
        CANDLE_FIRE("sfx/candle.p"),
        LAVA_SMOKE(""),
        WAND_ATTACK(""),
    }

    fun getParticleEffect(type: ParticleEffectType, position: Vector2): ParticleEffect? {
        val effect = ParticleEffect()
        effect.load(Gdx.files.internal(type.fullFilePath), Gdx.files.internal("sfx"))
        effect.setPosition(position.x, position.y)
        when (type) {
            CANDLE_FIRE -> {
                effect.scaleEffect(.04f)
            }
            LAVA_SMOKE -> {
            }
            WAND_ATTACK -> {
            }
        }
        effect.start()
        return effect
    }
}