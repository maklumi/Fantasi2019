package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import ktx.json.fromJson
import ktx.json.readValue
import kotlin.random.Random
import com.badlogic.gdx.utils.Array as gdxArray

class Entity(val inputComponent: InputComponent,
             val physicsComponent: PhysicsComponent,
             private val graphicsComponent: GraphicsComponent) {

    var entityConfig = EntityConfig()

    private val components = gdxArray<Component>(MAX_COMPONENTS).also {
        it.add(inputComponent)
        it.add(physicsComponent)
        it.add(graphicsComponent)
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        fun getOpposite(): Direction {
            return when (this) {
                LEFT -> RIGHT
                RIGHT -> LEFT
                UP -> DOWN
                else -> UP
            }
        }

        companion object {
            fun nextRandom(): Direction = values().random()
        }
    }

    enum class State {
        IDLE, WALKING, IMMOBILE;

        companion object {
            fun nextRandom(): State = values()[Random.nextInt(0, 2)] // IMMOBILE stays immobile
        }
    }

    fun update(batch: Batch, delta: Float) {
        inputComponent.update(this, delta)
        physicsComponent.update(this, delta)
        graphicsComponent.update(this, batch, delta)
    }

    fun updateInput(delta: Float) {
        inputComponent.update(this, delta)
    }

    fun sendMessage(message: Component.MESSAGE, vararg args: String) {
        var fullMessage = message.toString()
        args.forEach { fullMessage += MESSAGE_TOKEN + it }

        components.forEach { it.receiveMessage(fullMessage) }
    }

    fun getCurrentBoundingBox(): Rectangle = physicsComponent.currentBound

    companion object {

        const val MAX_COMPONENTS = 5

        fun getEntityConfig(configFilePath: String): EntityConfig {
            return json.fromJson(Gdx.files.internal(configFilePath))
        }

        fun getEntityConfigs(configFilePath: String): gdxArray<EntityConfig> {
            val configs = gdxArray<EntityConfig>()
            val jsonValues = json.fromJson<ArrayList<JsonValue>>(Gdx.files.internal(configFilePath))
            jsonValues.forEach { configs.add(json.readValue<EntityConfig>(it)) }
            return configs
        }
    }
}