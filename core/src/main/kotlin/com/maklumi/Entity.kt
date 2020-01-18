package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.maklumi.Component.MESSAGE.*
import com.maklumi.dialog.ComponentObserver
import com.maklumi.profile.ProfileManager
import ktx.json.fromJson
import ktx.json.readValue
import java.util.*
import kotlin.random.Random
import com.badlogic.gdx.utils.Array as gdxArray

class Entity(val inputComponent: InputComponent,
             val physicsComponent: PhysicsComponent,
             private val graphicsComponent: GraphicsComponent) {

    constructor(entity: Entity) : this(entity.inputComponent, entity.physicsComponent, entity.graphicsComponent) {
        entityConfig = entity.entityConfig
        components.clear()
        components.add(inputComponent)
        components.add(physicsComponent)
        components.add(graphicsComponent)
    }

    var entityConfig = EntityConfig()

    val currentPosition: Vector2
        get() = graphicsComponent.currentPosition

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

    fun registerObserver(conversationObserver: ComponentObserver) {
        physicsComponent.uiObservers.add(conversationObserver)
        graphicsComponent.uiObservers.add(conversationObserver)
    }

    fun unregisterObservers() {
        physicsComponent.uiObservers.clear()
        graphicsComponent.uiObservers.clear()
    }

    fun getCurrentBoundingBox(): Rectangle = physicsComponent.currentBound

    fun getAnimation(type: AnimationType): Animation<TextureRegion>? = graphicsComponent.animations[type]

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

        fun loadEntityConfigBy(configPath: String): EntityConfig {
            val entityConfig = getEntityConfig(configPath)
            val serializedConfig = ProfileManager.getProperty<EntityConfig>(entityConfig.entityID)
            return serializedConfig ?: entityConfig
        }

//        fun loadEntityConfig(entityConfig: EntityConfig): EntityConfig {
//            val serializedConfig = ProfileManager.getProperty<EntityConfig>(entityConfig.entityID)
//            return serializedConfig ?: entityConfig
//        }

        fun initEntityNPC(position: Vector2, config: EntityConfig): Entity {
            val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
            entity.apply {
                entityConfig = config
                sendMessage(LOAD_ANIMATIONS, json.toJson(config))
                sendMessage(INIT_START_POSITION, json.toJson(position))
                sendMessage(INIT_STATE, json.toJson(config.state))
                sendMessage(INIT_DIRECTION, json.toJson(config.direction))
            }
            return entity
        }

        fun initEntities(configs: gdxArray<EntityConfig>): Hashtable<String, Entity> {
            val entities = Hashtable<String, Entity>()
            configs.forEach { config ->
                val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
                entity.entityConfig = config
                entity.sendMessage(LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
                entity.sendMessage(INIT_START_POSITION, json.toJson(Vector2()))
                entity.sendMessage(INIT_STATE, json.toJson(entity.entityConfig.state))
                entity.sendMessage(INIT_DIRECTION, json.toJson(entity.entityConfig.direction))

                entities[entity.entityConfig.entityID] = entity
            }
            return entities
        }

        fun initEntity(entityConfig: EntityConfig): Entity {
            val json = Json()
            val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
            entity.entityConfig = entityConfig

            entity.sendMessage(LOAD_ANIMATIONS, json.toJson(entity.entityConfig))
            entity.sendMessage(INIT_START_POSITION, json.toJson(Vector2.Zero))
            entity.sendMessage(INIT_STATE, json.toJson(entity.entityConfig.state))
            entity.sendMessage(INIT_DIRECTION, json.toJson(entity.entityConfig.direction))

            return entity
        }
    }
}