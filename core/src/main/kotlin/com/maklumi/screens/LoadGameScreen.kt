package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.maklumi.Fantasi
import com.maklumi.Fantasi.ScreenType
import com.maklumi.Utility.STATUSUI_SKIN
import com.maklumi.profile.ProfileManager
import com.badlogic.gdx.scenes.scene2d.ui.List as ListBox

class LoadGameScreen(fantasi: Fantasi) : Screen {

    private val stage: Stage = Stage()

    init {
        val loadButton = TextButton("Load", STATUSUI_SKIN)
        val backButton = TextButton("Back", STATUSUI_SKIN)

        ProfileManager.storeAllProfiles()
        val array = ProfileManager.getProfileList()
        val listBox = ListBox<String>(STATUSUI_SKIN, "inventory")
        listBox.setItems(array)
        val scrollPane = ScrollPane(listBox)
        scrollPane.setOverscroll(false, false)
        scrollPane.fadeScrollBars = false
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setScrollbarsOnTop(true)

        val layout = Table()
        layout.center()
        layout.setFillParent(true)

        val topTable = Table()
        topTable.padBottom(loadButton.height)
        topTable.add(scrollPane).center()

        val bottomTable = Table()
        bottomTable.add(loadButton).padRight(50f)
        bottomTable.add(backButton)

        layout.add(topTable)
        layout.row()
        layout.add(bottomTable)

        stage.addActor(layout)

        //Listeners
        backButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                fantasi.screen = fantasi.getScreenType(ScreenType.MainMenu)
                return true
            }
        }
        )

        loadButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val fileName = listBox.selected ?: return false
                val file = ProfileManager.getProfileFile(fileName)
                if (file != null) {
                    ProfileManager.profileName = fileName
                    fantasi.screen = fantasi.getScreenType(ScreenType.MainGame)
                }
                return true
            }
        }
        )
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
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