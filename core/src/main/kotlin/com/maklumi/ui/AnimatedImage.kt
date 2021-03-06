package com.maklumi.ui

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.maklumi.AnimationType
import com.maklumi.Entity

class AnimatedImage : Image() {
    private var animation: Animation<TextureRegion>? = null
    private var frameTime = 0f
    var entity: Entity? = null
        set(value) {
            field = value
            setAnim(AnimationType.IDLE)
        }

    override fun act(delta: Float) {
        animation ?: return
        val tex = drawable as TextureRegionDrawable? ?: return
        frameTime = (frameTime + delta) % 5
        tex.region = animation!!.getKeyFrame(frameTime, true)
        super.act(delta) // last to draw the drawable
    }

    fun setAnim(animationType: AnimationType) {
        val anim = entity?.getAnimation(animationType) ?: return
        drawable = TextureRegionDrawable(anim.getKeyFrame(0f))
        animation = anim
    }
}