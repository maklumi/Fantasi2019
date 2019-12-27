package com.maklumi.ui

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport

class PlayerHUD(camera: Camera) : Screen {

    private val viewport = ScreenViewport(camera)
    val stage = Stage(viewport)
    private val statusUI = StatusUI()

    init {
        // debug camera parameters while setting up status ui table
        (stage.camera as OrthographicCamera).zoom = 0.5f
        (stage.camera as OrthographicCamera).position.set(0f, 0f, 0f)

        stage.addActor(statusUI)
    }

    override fun show() {}

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
        stage.dispose()
    }
}