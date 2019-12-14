package com.maklumi

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Player {

    private val frameWidth = 16
    private val frameHeight = 16
    private val spritePath = "sprites/characters/Warrior.png"

    lateinit var frameSprite: Sprite

    init {
        loadDefaultSprite()
    }

    private fun loadDefaultSprite() {
        Utility.loadTextureAsset(spritePath)
        val texture = Utility.getTextureAsset(spritePath)
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)
        frameSprite = Sprite(textureFrames[0][0], 0, 0, frameWidth, frameHeight)
    }


}