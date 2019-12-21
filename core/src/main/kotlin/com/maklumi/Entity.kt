package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import kotlin.random.Random
import com.badlogic.gdx.utils.Array as gdxArray

class Entity(private val inputComponent: InputComponent,
             val physicsComponent: PhysicsComponent,
             private val graphicsComponent: GraphicsComponent) {


    private val components = gdxArray<Component>(5).also {
        it.add(inputComponent)
        it.add(physicsComponent)
        it.add(graphicsComponent)
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        companion object {
            fun nextRandom(): Direction = values().random()
        }
    }

    enum class State {
        IDLE, WALKING, IMMOBILE;

        companion object {
            fun nextRandom(): State = values()[Random.nextInt(0, 3)]
        }
    }

    fun update(batch: Batch, delta: Float) {
        inputComponent.update(this, delta)
        physicsComponent.update(this, delta)
        graphicsComponent.update(batch, delta)
    }

    fun sendMessage(message: Component.MESSAGE, vararg args: String) {
        var fullMessage = message.toString()
        args.forEach { fullMessage += MESSAGE_TOKEN + it }

        components.forEach { it.receiveMessage(fullMessage) }
    }

}