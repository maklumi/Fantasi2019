package com.maklumi

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.graphics.use
import ktx.json.fromJson
import com.badlogic.gdx.math.GridPoint2 as Point
import com.badlogic.gdx.utils.Array as gdxArray

class NPCGraphicsComponent : GraphicsComponent() {

    private val walkSpriteSheetPath = "sprites/characters/Engineer.png"
    private val immobileSpritePath1 = "sprites/characters/Player0.png"
    private val immobileSpritePath2 = "sprites/characters/Player1.png"

    private val currentPosition = Vector2(0f, 0f)
    private var currentState = Entity.State.WALKING
    private var currentDirection = Entity.Direction.DOWN

    private val walkDownAnimation: Animation<TextureRegion>
    private val walkLeftAnimation: Animation<TextureRegion>
    private val walkRightAnimation: Animation<TextureRegion>
    private val walkUpAnimation: Animation<TextureRegion>
    private val immobileAnimation: Animation<TextureRegion>

    private var frameTime = 0f
    private var currentFrame: TextureRegion? = null

    init {
        Utility.loadTextureAsset(walkSpriteSheetPath)
        Utility.loadTextureAsset(immobileSpritePath1)
        Utility.loadTextureAsset(immobileSpritePath2)

        val walkTexture = Utility.getTextureAsset(walkSpriteSheetPath) as Texture
        val immobileTexture1 = Utility.getTextureAsset(immobileSpritePath1) as Texture
        val immobileTexture2 = Utility.getTextureAsset(immobileSpritePath2) as Texture

        val downGrid = gdxArray<Point>()
        val leftGrid = gdxArray<Point>()
        val rightGrid = gdxArray<Point>()
        val upGrid = gdxArray<Point>()

        // Point(x,y) is actually Point(row, column)
        downGrid.add(Point(0, 0))
        downGrid.add(Point(0, 1))
        downGrid.add(Point(0, 2))
        downGrid.add(Point(0, 3))
        walkDownAnimation = loadAnimation(walkTexture, downGrid)

        leftGrid.add(Point(1, 0))
        leftGrid.add(Point(1, 1))
        leftGrid.add(Point(1, 2))
        leftGrid.add(Point(1, 3))
        walkLeftAnimation = loadAnimation(walkTexture, leftGrid)

        rightGrid.add(Point(2, 0))
        rightGrid.add(Point(2, 1))
        rightGrid.add(Point(2, 2))
        rightGrid.add(Point(2, 3))
        walkRightAnimation = loadAnimation(walkTexture, rightGrid)

        upGrid.add(Point(3, 0))
        upGrid.add(Point(3, 1))
        upGrid.add(Point(3, 2))
        upGrid.add(Point(3, 3))
        walkUpAnimation = loadAnimation(walkTexture, upGrid)

        val point = Point(14, 7) // gargoyle
        immobileAnimation = loadAnimation(immobileTexture1, immobileTexture2, point)
    }

    override fun update(batch: Batch, delta: Float) {
        frameTime = (frameTime + delta) % 10

        when (currentState) {
            Entity.State.IDLE -> {
                currentFrame = when (currentDirection) {
                    Entity.Direction.DOWN -> walkDownAnimation.getKeyFrame(0f)
                    Entity.Direction.LEFT -> walkLeftAnimation.getKeyFrame(0f)
                    Entity.Direction.UP -> walkUpAnimation.getKeyFrame(0f)
                    Entity.Direction.RIGHT -> walkRightAnimation.getKeyFrame(0f)
                }
            }
            Entity.State.WALKING -> {
                currentFrame = when (currentDirection) {
                    Entity.Direction.DOWN -> walkDownAnimation.getKeyFrame(frameTime)
                    Entity.Direction.LEFT -> walkLeftAnimation.getKeyFrame(frameTime)
                    Entity.Direction.UP -> walkUpAnimation.getKeyFrame(frameTime)
                    Entity.Direction.RIGHT -> walkRightAnimation.getKeyFrame(frameTime)
                }
            }
            Entity.State.IMMOBILE -> {
                currentFrame = immobileAnimation.getKeyFrame(frameTime)
            }
        }

        batch.use {
            it.draw(currentFrame,
                    currentPosition.x,
                    currentPosition.y, 1f, 1f)
        }
    }

    override fun receiveMessage(message: String) {
        val string: List<String> = message.split(MESSAGE_TOKEN)
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