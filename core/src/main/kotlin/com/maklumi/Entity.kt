package com.maklumi

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class Entity {

    val inputComponent = InputComponent()
    val graphicsComponent = GraphicsComponent()
    var direction = Direction.LEFT
    var state = State.IDLE

    private val velocity = Vector2(4f, 4f)
    val currentPosition = Vector2()
    private val nextPosition = Vector2()
    val currentBound: Rectangle
        get() = Rectangle(currentPosition.x, currentPosition.y, 16f, 8f)
    val nextBound: Rectangle
        get() = Rectangle(nextPosition.x, nextPosition.y, 16f, 8f)

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
    }

    enum class State {
        IDLE, WALKING, PAUSE
    }

    fun update(delta: Float) {
        inputComponent.update(this, delta)
        graphicsComponent.update(this, delta)
    }

    fun initStartPosition(pos: Vector2) {
        currentPosition.set(pos)
        nextPosition.set(pos)
    }

    fun calculateNextPosition(currentDirection: Direction, deltaTime: Float) {
        var tempX = currentPosition.x
        var tempY = currentPosition.y
        nextPosition.set(tempX, tempY)

        velocity.scl(deltaTime)

        when (currentDirection) {
            Direction.LEFT -> tempX -= velocity.x
            Direction.RIGHT -> tempX += velocity.x
            Direction.UP -> tempY += velocity.y
            Direction.DOWN -> tempY -= velocity.y
        }

        nextPosition.set(tempX, tempY)

        velocity.scl(1 / deltaTime)

        // also set direction
        this.direction = currentDirection
    }

    fun setCurrentPosition() {
        currentPosition.set(nextPosition)
    }


}