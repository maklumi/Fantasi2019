package com.maklumi

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

abstract class PhysicsComponent : Component {

    protected val velocity = Vector2(4f, 4f)
    protected val nextPosition = Vector2()
    protected val currentPosition = Vector2()
    protected var currentState = Entity.State.IDLE
    protected var currentDirection = Entity.Direction.DOWN
    val currentBound: Rectangle
        get() = Rectangle(currentPosition.x, currentPosition.y, 16f, 8f)


    abstract fun update(entity: Entity, deltaTime: Float)

}