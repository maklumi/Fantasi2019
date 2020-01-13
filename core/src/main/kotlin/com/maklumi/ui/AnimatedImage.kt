package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class AnimatedImage : Image() {
    private var animation: Animation<TextureRegion>? = null
    private var frameTime = 0f

    override fun act(delta: Float) {
        animation ?: return
        val tex = drawable as TextureRegionDrawable? ?: return
        frameTime = (frameTime + delta) % 5
        tex.region = animation!!.getKeyFrame(frameTime, true)
        super.act(delta) // last to draw the drawable
    }

    fun setAnim(anim: Animation<TextureRegion>?) {
        anim ?: return
        drawable = TextureRegionDrawable(anim.getKeyFrame(0f))
        animation = anim
    }
}