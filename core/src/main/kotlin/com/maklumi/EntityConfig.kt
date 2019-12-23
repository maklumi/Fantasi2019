package com.maklumi

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array

class EntityConfig {
    val animationConfig = Array<AnimationConfig>()
}

data class AnimationConfig(
        var animationType: AnimationType = AnimationType.IDLE,
        var texturePaths: Array<String> = Array(),
        var gridPoints: Array<GridPoint2> = Array()
)

enum class AnimationType {
    WALK_DOWN, WALK_LEFT, WALK_RIGHT, WALK_UP,
    IDLE, IMMOBILE
}