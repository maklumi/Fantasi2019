package com.maklumi

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array

class GraphicsComponent {
    private val frameWidth = 16
    private val frameHeight = 16
    private val spritePath = "sprites/characters/Warrior.png"

    //    lateinit var frameSprite: Sprite
    private val walkLeftFrames = Array<TextureRegion>(4)
    private val walkRightFrames = Array<TextureRegion>(4)
    private val walkUpFrames = Array<TextureRegion>(4)
    private val walkDownFrames = Array<TextureRegion>(4)
    private lateinit var walkLeftAnimation: Animation<TextureRegion>
    private lateinit var walkRightAnimation: Animation<TextureRegion>
    private lateinit var walkUpAnimation: Animation<TextureRegion>
    private lateinit var walkDownAnimation: Animation<TextureRegion>


    private var frameTime = 0f
    private var frameSprite = Sprite()
    var currentFrame = TextureRegion()

    init {
        loadDefaultSprite()
        loadAllAnimations()
    }

    fun update(entity: Entity, delta: Float) {
        frameTime = (frameTime + delta) % 10

        if (entity.state == Entity.State.IDLE) {
            currentFrame = when (entity.direction) {
                Entity.Direction.DOWN -> walkDownFrames[0]
                Entity.Direction.LEFT -> walkLeftFrames[0]
                Entity.Direction.UP -> walkUpFrames[0]
                Entity.Direction.RIGHT -> walkRightFrames[0]
            }
        } else if (entity.state == Entity.State.WALKING) {
            currentFrame = when (entity.direction) {
                Entity.Direction.DOWN -> walkDownAnimation.getKeyFrame(frameTime)
                Entity.Direction.LEFT -> walkLeftAnimation.getKeyFrame(frameTime)
                Entity.Direction.UP -> walkUpAnimation.getKeyFrame(frameTime)
                Entity.Direction.RIGHT -> walkRightAnimation.getKeyFrame(frameTime)
            }
        }
    }

    private fun loadDefaultSprite() {
        Utility.loadTextureAsset(spritePath)
        val texture = Utility.getTextureAsset(spritePath)
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)
        frameSprite = Sprite(textureFrames[0][0], 0, 0, frameWidth, frameHeight)
        currentFrame = textureFrames[0][0]
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

}