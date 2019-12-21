package com.maklumi

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array as gdxArray

abstract class GraphicsComponent : Component {

    private val frameWidth = 16
    private val frameHeight = 16

    abstract fun update(batch: Batch, delta: Float)

    protected fun loadAnimation(tex1: Texture, tex2: Texture, frameIndex: GridPoint2): Animation<TextureRegion> {
        val texture1Frames = TextureRegion.split(tex1, frameWidth, frameHeight)
        val texture2Frames = TextureRegion.split(tex2, frameWidth, frameHeight)

        val keyFrames = gdxArray<TextureRegion>(2)

        keyFrames.add(texture1Frames[frameIndex.x][frameIndex.y])
        keyFrames.add(texture2Frames[frameIndex.x][frameIndex.y])

        return Animation(0.25f, keyFrames, Animation.PlayMode.LOOP)
    }

    protected fun loadAnimation(texture: Texture, points: gdxArray<GridPoint2>): Animation<TextureRegion> {
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)

        val keyFrames = gdxArray<TextureRegion>(points.size)

        for (point in points) {
            keyFrames.add(textureFrames[point.x][point.y])
        }

        return Animation(0.25f, keyFrames, Animation.PlayMode.LOOP)
    }
}