package com.maklumi

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array

class EntityConfig {
    var entityID: String = "no name"
    var state = Entity.State.IDLE
    var direction = Entity.Direction.DOWN
    val animationConfig = Array<AnimationConfig>()
    var inventory = Array<InventoryItem.ItemTypeID>()
    var conversationConfigPath: String = ""
    var questConfigPath = ""
}

data class AnimationConfig(
        var frameDuration: Float = 1.0f,
        var animationType: AnimationType = AnimationType.IDLE,
        var texturePaths: Array<String> = Array(),
        var gridPoints: Array<GridPoint2> = Array()
)

enum class AnimationType {
    WALK_DOWN, WALK_LEFT, WALK_RIGHT, WALK_UP,
    IDLE, IMMOBILE
}