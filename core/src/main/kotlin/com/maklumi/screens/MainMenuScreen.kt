package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.maklumi.Fantasi
import com.maklumi.Fantasi.ScreenType
import com.maklumi.Utility.STATUSUI_SKIN
import com.maklumi.Utility.STATUSUI_TEXTUREATLAS
import com.maklumi.audio.AudioManager
import com.maklumi.audio.AudioObserver.AudioCommand.*
import com.maklumi.audio.AudioObserver.AudioTypeEvent.MUSIC_TITLE
import com.maklumi.audio.AudioSubject

class MainMenuScreen(fantasi: Fantasi) : GameScreen(), AudioSubject {

    private val stage = Stage()

    init {
        val layout = Table()
        layout.setFillParent(true)

        val title = Image(STATUSUI_TEXTUREATLAS.findRegion("bludbourne_title"))
        val newGameButton = TextButton("New Game", STATUSUI_SKIN)
        val loadGameButton = TextButton("Load Game", STATUSUI_SKIN)
        val watchIntroButton = TextButton("Watch Intro", STATUSUI_SKIN)
        val creditsButton = TextButton("Credits", STATUSUI_SKIN)
        val exitButton = TextButton("Exit", STATUSUI_SKIN)

        layout.add(title).spaceBottom(75f).row()
        layout.add(newGameButton).spaceBottom(10f).row()
        layout.add(loadGameButton).spaceBottom(10f).row()
        layout.add(watchIntroButton).spaceBottom(10f).row()
        layout.add(creditsButton).spaceBottom(10f).row()
        layout.add(exitButton).spaceBottom(10f).row()

        stage.addActor(layout)

        //Listeners
        newGameButton.onTouchDown { fantasi.screen = fantasi.getScreenType(ScreenType.NewGame) }
        loadGameButton.onTouchDown { fantasi.screen = fantasi.getScreenType(ScreenType.LoadGame) }
        watchIntroButton.onTouchDown { fantasi.screen = fantasi.getScreenType(ScreenType.WatchIntro) }
        creditsButton.onTouchDown { fantasi.screen = fantasi.getScreenType(ScreenType.Credits) }
        exitButton.onTouchDown { Gdx.app.exit() }
        
        audioObservers.add(AudioManager)
        notify(MUSIC_LOAD, MUSIC_TITLE)

    }

    private inline fun Actor.onTouchDown(crossinline listener: () -> Unit): InputListener {
        val touchListener = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                listener()
                return true
            }
        }
        this.addListener(touchListener)
        return touchListener
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
        stage.viewport.setScreenSize(width, height)
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
        notify(MUSIC_STOP, MUSIC_TITLE)
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        stage.dispose()
    }
}