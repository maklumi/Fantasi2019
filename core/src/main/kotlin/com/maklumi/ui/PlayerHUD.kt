package com.maklumi.ui

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick

class PlayerHUD(camera: Camera) : Screen {

    private val viewport = ScreenViewport(camera)
    val stage = Stage(viewport)
    private val statusUI = StatusUI()
    val inventoryUI = InventoryUI()

    init {
        stage.addActor(statusUI)

        val x = statusUI.width
        val y = statusUI.height
        inventoryUI.setPosition(x, y)
        inventoryUI.isVisible = false
        statusUI.inventoryButton.onClick { inventoryUI.isVisible = !inventoryUI.isVisible }
        stage.addActor(inventoryUI)

        //add tooltips to the stage
        stage.addActor(inventoryUI.tooltip)
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