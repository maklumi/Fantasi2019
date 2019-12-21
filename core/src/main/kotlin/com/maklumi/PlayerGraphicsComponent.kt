package com.maklumi

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import ktx.graphics.use
import ktx.json.fromJson

class PlayerGraphicsComponent : GraphicsComponent() {
    private val frameWidth = 16
    private val frameHeight = 16
    private val spritePath = "sprites/characters/Warrior.png"

    private val walkLeftFrames = Array<TextureRegion>(4)
    private val walkRightFrames = Array<TextureRegion>(4)
    private val walkUpFrames = Array<TextureRegion>(4)
    private val walkDownFrames = Array<TextureRegion>(4)
    private lateinit var walkLeftAnimation: Animation<TextureRegion>
    private lateinit var walkRightAnimation: Animation<TextureRegion>
    private lateinit var walkUpAnimation: Animation<TextureRegion>
    private lateinit var walkDownAnimation: Animation<TextureRegion>


    private var frameTime = 0f
    private var currentFrame = TextureRegion()
    private val currentPosition = Vector2()
    private var currentState = Entity.State.IDLE
    private var currentDirection = Entity.Direction.DOWN

    init {
        loadAllAnimations()
    }

    override fun update(batch: Batch, delta: Float) {
        frameTime = (frameTime + delta) % 10

        if (currentState == Entity.State.IDLE) {
            currentFrame = when (currentDirection) {
                Entity.Direction.DOWN -> walkDownFrames[0]
                Entity.Direction.LEFT -> walkLeftFrames[0]
                Entity.Direction.UP -> walkUpFrames[0]
                Entity.Direction.RIGHT -> walkRightFrames[0]
            }
        } else if (currentState == Entity.State.WALKING) {
            currentFrame = when (currentDirection) {
                Entity.Direction.DOWN -> walkDownAnimation.getKeyFrame(frameTime)
                Entity.Direction.LEFT -> walkLeftAnimation.getKeyFrame(frameTime)
                Entity.Direction.UP -> walkUpAnimation.getKeyFrame(frameTime)
                Entity.Direction.RIGHT -> walkRightAnimation.getKeyFrame(frameTime)
            }
        }

        batch.use {
            it.draw(currentFrame,
                    currentPosition.x,
                    currentPosition.y, 1f, 1f)
        }
    }

    private fun loadAllAnimations() {
        Utility.loadTextureAsset(spritePath)
        val texture = Utility.getTextureAsset(spritePath)
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)

        for (row in 0..3) {
            for (col in 0..3) {
                val region = textureFrames[row][col]
                when (row) {
                    0 -> walkDownFrames.insert(col, region)
                    1 -> walkLeftFrames.insert(col, region)
                    2 -> walkRightFrames.insert(col, region)
                    3 -> walkUpFrames.insert(col, region)
                }
            }
        }

        walkDownAnimation = Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP)
        walkLeftAnimation = Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP)
        walkRightAnimation = Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP)
        walkUpAnimation = Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP)
    }

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
        }
    }

}