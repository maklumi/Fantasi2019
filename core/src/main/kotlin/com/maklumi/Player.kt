package com.maklumi

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class Player {

    private val frameWidth = 16
    private val frameHeight = 16
    private val spritePath = "sprites/characters/Warrior.png"

    lateinit var frameSprite: Sprite

    init {
        loadDefaultSprite()
    }

    private var velocity = Vector2(10f, 10f)
    var playerPosition = Vector2()

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;
    }

    private fun loadDefaultSprite() {
        Utility.loadTextureAsset(spritePath)
        val texture = Utility.getTextureAsset(spritePath)
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)
        frameSprite = Sprite(textureFrames[0][0], 0, 0, frameWidth, frameHeight)
    }

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
    }

}