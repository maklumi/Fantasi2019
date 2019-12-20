package com.maklumi

class Entity {

    val inputComponent = InputComponent()
    val physicsComponent = PhysicsComponent()
    val graphicsComponent = GraphicsComponent()
    var direction = Direction.LEFT
    var state = State.IDLE


    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
    }

    enum class State {
        IDLE, WALKING, PAUSE
    }

    fun update(delta: Float) {
        inputComponent.update(this, delta)
        physicsComponent.update()
        graphicsComponent.update(this, delta)
    }


}