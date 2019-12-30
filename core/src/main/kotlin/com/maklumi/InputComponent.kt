package com.maklumi

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector3

abstract class InputComponent : Component, InputProcessor {

    protected var currentDirection = Entity.Direction.nextRandom()

    enum class Keys { Left, Right, Up, Down, Quit, Pause }
    enum class Mouse { SELECT, DOACTION }

    protected val lastMouseCoordinates = Vector3()

    protected val mouseButtons = mutableMapOf(
            Mouse.SELECT to false,
            Mouse.DOACTION to false)

    protected val keys = mutableMapOf(
            Keys.Left to false,
            Keys.Right to false,
            Keys.Up to false,
            Keys.Down to false,
            Keys.Quit to false,
            Keys.Pause to false)

    abstract fun update(entity: Entity, delta: Float)

}