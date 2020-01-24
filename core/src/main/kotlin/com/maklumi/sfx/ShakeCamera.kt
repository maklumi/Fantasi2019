package com.maklumi.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

class ShakeCamera(x: Float, y: Float, private val radius: Float) {

    val position: Vector2 = Vector2()
        get() {
            if (shakeRadius < 2f) {
                // reset
                shakeRadius = radius
                shouldShake = false
                return Vector2(oriPosition.x, oriPosition.y)
            }
            // diminish shake
            shakeRadius *= 0.9f
            field.set(oriPosition.x + offset.x, oriPosition.y + offset.y)
            return field
        }

    var shouldShake = false

    val oriPosition = Vector2(x, y)

    private var shakeRadius = radius

    private val randomAngle: Float
        get() = MathUtils.random(1f, 360f)

    private val offset: Vector2 = Vector2()
        get() {
            field.x = MathUtils.sinDeg(randomAngle) * shakeRadius
            field.y = MathUtils.cosDeg(randomAngle) * shakeRadius
            return field
        }

}