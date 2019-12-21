package com.maklumi

abstract class InputComponent : Component {

    protected var currentDirection = Entity.Direction.nextRandom()

    abstract fun update(entity: Entity)

}