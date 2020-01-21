package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.maklumi.Fantasi
import com.maklumi.Utility

class CreditScreen(fantasi: Fantasi) : GameScreen() {

    private val stage = Stage()
    private val scrollPane: ScrollPane

    init {
        val creditsPath = "licenses/credits.txt"
        val textString = Gdx.files.internal(creditsPath).readString()
        val textLabel = Label(textString, Utility.STATUSUI_SKIN, "credits")
        textLabel.setAlignment(Align.top or Align.center)
        textLabel.setWrap(true)

        scrollPane = ScrollPane(textLabel)
        scrollPane.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                scrollPane.scrollY = 0f
                scrollPane.updateVisualScroll()
                fantasi.screen = fantasi.getScreenType(Fantasi.ScreenType.MainMenu)
            }
        }
        )

        val table = Table()
        table.center()
        table.setFillParent(true)
        table.defaults().width(Gdx.graphics.width.toFloat())
        table.add(scrollPane)

        stage.addActor(table)
    }

    override fun show() {
        scrollPane.isVisible = true
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        scrollPane.scrollY += delta * 20f
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.setScreenSize(width, height)
    }

    override fun hide() {
        scrollPane.isVisible = false
        scrollPane.scrollY = 0f
        scrollPane.updateVisualScroll()
        Gdx.input.inputProcessor = null
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        stage.dispose()
    }
}