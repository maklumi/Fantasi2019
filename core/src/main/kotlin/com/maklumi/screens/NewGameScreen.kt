package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.maklumi.Fantasi
import com.maklumi.Fantasi.ScreenType
import com.maklumi.Utility
import com.maklumi.profile.ProfileManager

class NewGameScreen(fantasi: Fantasi) : Screen {

    private val stage = Stage()

    init {
        val profileNameLabel = Label("Enter Profile Name: ", Utility.STATUSUI_SKIN)
        val textField = TextField("", Utility.STATUSUI_SKIN, "inventory")
        textField.maxLength = 20

        val overwriteDialog = Dialog("Overwrite?", Utility.STATUSUI_SKIN, "solidbackground")
        val overwriteLabel = Label("Overwrite existing profile name?", Utility.STATUSUI_SKIN)
        val cancelButton = TextButton("Cancel", Utility.STATUSUI_SKIN, "inventory")
        val overwriteButton = TextButton("Overwrite", Utility.STATUSUI_SKIN, "inventory")
        overwriteDialog.setKeepWithinStage(true)
        overwriteDialog.isModal = true
        overwriteDialog.isMovable = false
        overwriteDialog.text(overwriteLabel)

        val startButton = TextButton("Start", Utility.STATUSUI_SKIN)
        val backButton = TextButton("Back", Utility.STATUSUI_SKIN)

        overwriteDialog.row()
        overwriteDialog.button(overwriteButton).bottom().left()
        overwriteDialog.button(cancelButton).bottom().right()

        val topTable = Table()
        topTable.add(profileNameLabel).center()
        topTable.add(textField).center()
        topTable.padBottom(backButton.height)

        val bottomTable = Table()
        bottomTable.height = startButton.height
        bottomTable.width = Gdx.graphics.width.toFloat()
        bottomTable.center()
        bottomTable.add(startButton).padRight(50f)
        bottomTable.add(backButton)

        val layout = Table()
        layout.setFillParent(true)
        layout.add(topTable)
        layout.row()
        layout.add(bottomTable)
        stage.addActor(layout)

        //button listeners
        cancelButton.onTouchDown {
            overwriteDialog.hide()
        }

        overwriteButton.onTouchDown {
            val messageText = textField.text
            ProfileManager.writeProfileToStorage(messageText, "", true)
            ProfileManager.profileName = messageText
            ProfileManager.saveProfile()
            fantasi.screen = fantasi.getScreenType(ScreenType.MainGame)
        }

        startButton.onTouchDown {
            val messageText = textField.text
            //check to see if the current profile matches one that already exists
            val exists = ProfileManager.doesProfileExist(messageText)

            if (exists) {
                //Pop up dialog for Overwrite
                overwriteDialog.show(stage)
            } else if (messageText.isNotEmpty()) {
                ProfileManager.writeProfileToStorage(messageText, "", false)
                ProfileManager.profileName = messageText
                ProfileManager.saveProfile()
                fantasi.screen = fantasi.getScreenType(ScreenType.MainGame)
            }
        }

        backButton.onTouchDown {
            fantasi.screen = fantasi.getScreenType(ScreenType.MainMenu)
        }

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
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        stage.dispose()
    }
}