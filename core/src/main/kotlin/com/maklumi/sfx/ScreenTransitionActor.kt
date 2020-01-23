package com.maklumi.sfx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

class ScreenTransitionActor(transitionColor: Color) : Image() {

    init {
        toFront()
        setFillParent(true)

        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(transitionColor)
        pixmap.fill()
        drawable = TextureRegionDrawable(TextureRegion(Texture(pixmap)))
        clearListeners()
        touchable = Touchable.disabled
    }

}