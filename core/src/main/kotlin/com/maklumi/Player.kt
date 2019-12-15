package com.maklumi

import com.badlogic.gdx.graphics.g2d.Animation
//import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array as GdxArray

class Player {

    private val frameWidth = 16
    private val frameHeight = 16
    private val spritePath = "sprites/characters/Warrior.png"

    //    lateinit var frameSprite: Sprite
    private val walkLeftFrames = GdxArray<TextureRegion>(4)
    private val walkRightFrames = GdxArray<TextureRegion>(4)
    private val walkUpFrames = GdxArray<TextureRegion>(4)
    private val walkDownFrames = GdxArray<TextureRegion>(4)
    private lateinit var walkLeftAnimation: Animation<TextureRegion>
    private lateinit var walkRightAnimation: Animation<TextureRegion>
    private lateinit var walkUpAnimation: Animation<TextureRegion>
    private lateinit var walkDownAnimation: Animation<TextureRegion>

    private var frameTime = 0f
    lateinit var currentFrame: TextureRegion
    private var currentDirection = Direction.LEFT

    init {
//        loadDefaultSprite()
        loadAllAnimations()
    }

    private var velocity = Vector2(4f, 4f)
    val playerPosition = Vector2(7f, 6f)

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
    }

//    private fun loadDefaultSprite() {
//        Utility.loadTextureAsset(spritePath)
//        val texture = Utility.getTextureAsset(spritePath)
//        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)
//        frameSprite = Sprite(textureFrames[0][0], 0, 0, frameWidth, frameHeight)
//    }

    fun calculateNextPosition(currentDirection: Direction, deltaTime: Float) {
        var tempX = playerPosition.x
        var tempY = playerPosition.y

        velocity.scl(deltaTime)

        when (currentDirection) {
            Direction.LEFT -> tempX -= velocity.x
            Direction.RIGHT -> tempX += velocity.x
            Direction.UP -> tempY += velocity.y
            Direction.DOWN -> tempY -= velocity.y
        }

        playerPosition.set(tempX, tempY)

        velocity.scl(1 / deltaTime)

        // also set direction
        this.currentDirection = currentDirection
        // then set current frame
        setCurrentFrame()
    }

    fun update(delta: Float) {
        frameTime = (frameTime + delta) % 10
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

        // also set default first frame
        currentFrame = textureFrames[0][0]
    }

    private fun setCurrentFrame() {
        currentFrame = when (currentDirection) {
            Direction.DOWN -> walkDownAnimation.getKeyFrame(frameTime)
            Direction.LEFT -> walkLeftAnimation.getKeyFrame(frameTime)
            Direction.UP -> walkUpAnimation.getKeyFrame(frameTime)
            Direction.RIGHT -> walkRightAnimation.getKeyFrame(frameTime)
        }
    }

}