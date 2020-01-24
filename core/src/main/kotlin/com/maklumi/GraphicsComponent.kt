package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.math.Vector2
import com.maklumi.MapManager.camera
import com.maklumi.dialog.ComponentSubject
import ktx.graphics.use
import ktx.json.fromJson
import com.badlogic.gdx.utils.Array as gdxArray

abstract class GraphicsComponent : Component, ComponentSubject() {

    private val frameWidth = 16
    private val frameHeight = 16
    val currentPosition = Vector2(0f, 0f)
    private var currentState = Entity.State.WALKING
    private var currentDirection = Entity.Direction.DOWN

    private var frameTime = 0f
    private var currentFrame: TextureRegion? = null
    var animations = mutableMapOf<AnimationType, Animation<TextureRegion>>()

    private val shapeRenderer = ShapeRenderer()

    open fun update(entity: Entity, batch: Batch, delta: Float) {
        frameTime = (frameTime + delta) % 10

        when (currentState) {
            Entity.State.IDLE -> {
                currentFrame = when (currentDirection) {
                    Entity.Direction.DOWN -> animations[AnimationType.WALK_DOWN]?.getKeyFrame(0f)
                    Entity.Direction.LEFT -> animations[AnimationType.WALK_LEFT]?.getKeyFrame(0f)
                    Entity.Direction.UP -> animations[AnimationType.WALK_UP]?.getKeyFrame(0f)
                    Entity.Direction.RIGHT -> animations[AnimationType.WALK_RIGHT]?.getKeyFrame(0f)
                }
            }
            Entity.State.WALKING -> {
                currentFrame = when (currentDirection) {
                    Entity.Direction.DOWN -> animations[AnimationType.WALK_DOWN]?.getKeyFrame(frameTime)
                    Entity.Direction.LEFT -> animations[AnimationType.WALK_LEFT]?.getKeyFrame(frameTime)
                    Entity.Direction.UP -> animations[AnimationType.WALK_UP]?.getKeyFrame(frameTime)
                    Entity.Direction.RIGHT -> animations[AnimationType.WALK_RIGHT]?.getKeyFrame(frameTime)
                }
            }
            Entity.State.IMMOBILE -> {
                currentFrame = animations[AnimationType.IMMOBILE]?.getKeyFrame(frameTime)
            }
        }

        drawSelected(entity)

        batch.use {
            it.draw(currentFrame,
                    currentPosition.x,
                    currentPosition.y, 1f, 1f)
        }

        val myColor = if (entity.physicsComponent is PlayerPhysicsComponent) Color.FIREBRICK
        else Color.CHARTREUSE
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        val rect = entity.getCurrentBoundingBox()
        shapeRenderer.apply {
            projectionMatrix = camera.combined
            begin(ShapeRenderer.ShapeType.Filled)
            myColor.a = 0.4f
            color = myColor
            rect(rect.x, rect.y, rect.width, rect.height)
            end()
        }
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    open fun drawSelected(entity: Entity) {}

    override fun receiveMessage(message: String) {
        val string: List<String> = message.split(MESSAGE_TOKEN)
        //for message with 1 pair of object payload
        if (string.size != 2) return
        when {
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.INIT_START_POSITION -> {
                currentPosition.set(json.fromJson(string[1]))
            }
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.CURRENT_POSITION -> {
                currentPosition.set(json.fromJson(string[1]))
            }
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.CURRENT_STATE -> {
                currentState = json.fromJson(string[1])
            }
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.CURRENT_DIRECTION -> {
                currentDirection = json.fromJson(string[1])
            }
            Component.MESSAGE.valueOf(string[0]) == Component.MESSAGE.LOAD_ANIMATIONS -> {
                val entityConfig = json.fromJson<EntityConfig>(string[1])
                val animConfigs = entityConfig.animationConfig
                animConfigs.forEach { (dur, type, paths, points) ->
                    if (paths.size == 1)
                        animations[type] = loadAnimation(paths[0], points, dur)
                    if (paths.size == 2)
                        animations[type] = loadAnimation(paths[0], paths[1], points, dur)
                }
            }
        }
    }

    private fun loadAnimation(path1: String, path2: String, points: gdxArray<GridPoint2>, duration: Float): Animation<TextureRegion> {
        Utility.loadTextureAsset(path1)
        Utility.loadTextureAsset(path2)
        val tex1 = Utility.getTextureAsset(path1)
        val tex2 = Utility.getTextureAsset(path2)

        val texture1Frames = TextureRegion.split(tex1, frameWidth, frameHeight)
        val texture2Frames = TextureRegion.split(tex2, frameWidth, frameHeight)

        val keyFrames = gdxArray<TextureRegion>(2)

        keyFrames.add(texture1Frames[points[0].x][points[0].y])
        keyFrames.add(texture2Frames[points[0].x][points[0].y])

        return Animation(duration, keyFrames, Animation.PlayMode.LOOP)
    }

    private fun loadAnimation(path: String, points: gdxArray<GridPoint2>, duration: Float): Animation<TextureRegion> {
        Utility.loadTextureAsset(path)
        val texture = Utility.getTextureAsset(path)
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)

        val keyFrames = gdxArray<TextureRegion>(points.size)

        points.forEach { p -> keyFrames.add(textureFrames[p.x][p.y]) }

        return Animation(duration, keyFrames, Animation.PlayMode.LOOP)
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }
}