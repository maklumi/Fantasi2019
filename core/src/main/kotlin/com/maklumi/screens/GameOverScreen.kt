package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.maklumi.Fantasi
import com.maklumi.Utility
import com.maklumi.audio.AudioObserver.AudioCommand.MUSIC_LOAD
import com.maklumi.audio.AudioObserver.AudioCommand.MUSIC_PLAY_LOOP
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TITLE

class GameOverScreen(fantasi: Fantasi) : GameScreen() {

    private val stage = Stage()
    private val deathMessage = "You have fought bravely, but alas, " +
            "you have fallen during your epic struggle."
    private val gameOver = "Game Over"

    init {
        val messageLabel = Label(deathMessage, Utility.STATUSUI_SKIN)
        messageLabel.setWrap(true)

        val gameOverLabel = Label(gameOver, Utility.STATUSUI_SKIN)
        gameOverLabel.setAlignment(Align.center)

        val continueButton = TextButton("Continue", Utility.STATUSUI_SKIN)

        val table = Table()
        table.setFillParent(true)
        table.add(messageLabel).pad(50f, 50f, 50f, 50f).expandX().fillX()
        table.row()
        table.add(gameOverLabel)
        table.row()
        table.add(continueButton).pad(50f, 50f, 50f, 50f)
        val mainMenuButton = TextButton("Main Menu", Utility.STATUSUI_SKIN)
        table.add(mainMenuButton).pad(50f, 50f, 50f, 50f)

        stage.addActor(table)

        continueButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                fantasi.screen = fantasi.getScreenType(Fantasi.ScreenType.LoadGame)
                return true
            }
        }
        )

        mainMenuButton.addListener(object : ClickListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                fantasi.screen = fantasi.getScreenType(Fantasi.ScreenType.MainMenu)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return true
            }
        }
        )

        notify(MUSIC_LOAD, MUSIC_TITLE)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        notify(MUSIC_PLAY_LOOP, MUSIC_TITLE)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun hide() {
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