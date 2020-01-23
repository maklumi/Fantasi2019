package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.maklumi.*
import com.maklumi.EntityFactory.EntityName.*
import com.maklumi.EntityFactory.getEntityByName
import com.maklumi.MapManager.currentMap
import com.maklumi.MapManager.isNewMapLoaded
import com.maklumi.audio.AudioObserver.AudioCommand
import com.maklumi.audio.AudioObserver.AudioTypeEvent
import com.maklumi.battle.MonsterFactory
import com.maklumi.sfx.ScreenTransitionAction
import com.maklumi.sfx.ScreenTransitionActor
import com.maklumi.ui.AnimatedImage

class CutSceneScreen(fantasi: Fantasi) : GameScreen() {

    init {
        MapManager.loadMap(MapFactory.MapType.TOWN)
        MapManager.disableCurrentmapMusic()
    }

    private val screenRatio = Gdx.graphics.width / Gdx.graphics.height * 1f
    private val tiledMapRenderer = OrthogonalTiledMapRenderer(currentMap, 1 / 16f)
    private val camera = OrthographicCamera(Gdx.graphics.width * 1f, Gdx.graphics.height * screenRatio)
    private val viewport = ScreenViewport(camera)
    private val stage = Stage(viewport)
    private var mainChar = Actor()
    private val uiCamera = OrthographicCamera()
    private val uiViewport = ScreenViewport(uiCamera)
    private val uiStage = Stage(uiViewport)
    private val dialog: Dialog = Dialog("", Utility.STATUSUI_SKIN, "solidbackground")
    private val label = Label("", Utility.STATUSUI_SKIN)
    private val transition = ScreenTransitionActor(Color.GREEN)
    private val fadeOut = ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_OUT, 3f)
    private val fadeIn = ScreenTransitionAction(ScreenTransitionAction.ScreenTransitionType.FADE_IN, 3f)

    private var shouldFollow = false

    private val animBlackSmith = getAnimatedImageFor(TOWN_BLACKSMITH)
    private val animInnKeeper = getAnimatedImageFor(TOWN_INNKEEPER)
    private val animMage = getAnimatedImageFor(TOWN_MAGE)
    private val animFire = getAnimatedImageFor(FIRE)
    private val demon = MonsterFactory.getMonster(MonsterFactory.MonsterEntityType.MONSTER042)
    private val animDemon = AnimatedImage()

    private fun customAction(fn: () -> Unit): RunnableAction {
        return object : RunnableAction() {
            override fun run() {
                fn.invoke()
            }
        }
    }

    private val scene01: Action = customAction {
        hideDialog()
        MapManager.loadMap(MapFactory.MapType.TOWN)
        MapManager.disableCurrentmapMusic()
        animBlackSmith.isVisible = true
        animInnKeeper.isVisible = true
        animMage.isVisible = true

        setCameraPosition(10f, 16f)
        showMessage("BLACKSMITH: \nWe have planned this long enough. The time is now! I have had enough talk...")
    }

    private val scene02: Action = customAction {
        hideDialog()
        MapManager.loadMap(MapFactory.MapType.TOP_WORLD)
        MapManager.disableCurrentmapMusic()
        setCameraPosition(50f, 30f)
        animBlackSmith.setPosition(50f, 30f)
        animInnKeeper.setPosition(52f, 30f)
        animMage.setPosition(50f, 28f)
        animFire.setPosition(52f, 28f)
    }

    private val scene03 = customAction {
        hideDialog()
        animDemon.setPosition(52f, 28f)
        animDemon.isVisible = true
    }

    private val scene04 = customAction {
        hideDialog()
        animBlackSmith.isVisible = false
        animInnKeeper.isVisible = false
        animMage.isVisible = false
        animFire.isVisible = false

        MapManager.loadMap(MapFactory.MapType.TOP_WORLD)
        MapManager.disableCurrentmapMusic()

        animDemon.isVisible = true
        animDemon.setScale(1f, 1f)
        animDemon.setPosition(50f, 40f)

        cameraFollow(animDemon)
    }

    private val scene05 = customAction {
        hideDialog()
        animBlackSmith.isVisible = false
        animInnKeeper.isVisible = false
        animMage.isVisible = false
        animFire.isVisible = false

        MapManager.loadMap(MapFactory.MapType.CASTLE_OF_DOOM)
        MapManager.disableCurrentmapMusic()
        animDemon.isVisible = true
        animDemon.setPosition(15f, 1f)
        cameraFollow(animDemon)
    }

    private val switchScreen = customAction {
        fantasi.screen = fantasi.getScreenType(Fantasi.ScreenType.MainMenu)
    }

    private var scenes = getCutsceneAction()

    private fun initialize() {
        stage.addActor(animBlackSmith)
        stage.addActor(animInnKeeper)
        stage.addActor(animMage)
        stage.addActor(animFire)
        stage.addActor(animDemon)
        stage.addActor(transition)
        transition.isVisible = false
        uiStage.addActor(dialog)
        transition.toFront()

        label.setWrap(true)
        dialog.contentTable.add(label).width(stage.width / 2).pad(10f, 10f, 10f, 10f)
        camera.setToOrtho(false, screenRatio * 12, screenRatio * 12f)

        animBlackSmith.setPosition(10f, 16f)
        animInnKeeper.setPosition(12f, 15f)
        animMage.setPosition(11f, 17f)

        animDemon.entity = demon
        animDemon.setSize(1f, 1f)
        animDemon.isVisible = false
    }

    private fun getCutsceneAction(): Action {
        scene01.reset()
        scene02.reset()
        scene03.reset()
        scene04.reset()
        scene05.reset()
        switchScreen.reset()
        fadeIn.reset()
        fadeOut.reset()

        return Actions.sequence(
                Actions.addAction(scene01),
                Actions.delay(3.5f),
                Actions.run {
                    showMessage("MAGE: \nThis is dark magic you fool. We must proceed with caution, or this could end badly for all of us")
                },
                Actions.delay(3.5f),
                Actions.run {
                    showMessage("INNKEEPER: Both of you need to keep it down. If we get caught using black magic, we will all be hanged!")
                },
                Actions.delay(2.5f),
                Actions.addAction(fadeOut, transition),
                Actions.delay(3f),
                Actions.addAction(scene02),
                Actions.addAction(fadeIn, transition),
                Actions.delay(3f),
                Actions.run {
                    showMessage("BLACKSMITH: Now, let's get on with this. I don't like the cemeteries very much...")
                },
                Actions.delay(3.5f),
                Actions.run {
                    showMessage("MAGE: I told you, we can't rush the spell. Bringing someone back to life isn't simple!")
                },
                Actions.delay(3.5f),
                Actions.run {
                    showMessage("INNKEEPER: I know you loved your daughter, but this just isn't right...")
                },
                Actions.delay(3.5f),
                Actions.run {
                    showMessage("BLACKSMITH: You have never had a child of your own. You just don't understand!")
                },
                Actions.delay(3.5f),
                Actions.run {
                    showMessage("MAGE: You both need to concentrate, wait...Oh no, something is wrong!!")
                },
                Actions.delay(3.5f),
                Actions.addAction(scene03),
                Actions.addAction(Actions.fadeOut(2f), animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeIn(2f), animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeOut(2f), animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeIn(2f), animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeOut(2f), animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.fadeIn(2f), animDemon),
                Actions.delay(2f),
                Actions.addAction(Actions.scaleBy(20f, 20f, 5f, Interpolation.bounce), animDemon),
                Actions.delay(5f),
                Actions.addAction(Actions.moveBy(20f, 0f), animDemon),
                Actions.delay(2f),
                Actions.run {
                    showMessage("BLACKSMITH: What...What have we done...")
                },
                Actions.addAction(fadeOut, transition),
                Actions.delay(3f),
                Actions.addAction(scene04),
                Actions.addAction(fadeIn, transition),
                Actions.delay(3f),
                Actions.addAction(Actions.moveTo(54f, 65f, 13f, Interpolation.linear), animDemon),
                Actions.delay(10f),
                Actions.addAction(fadeOut, transition),
                Actions.delay(3f),
                Actions.addAction(fadeIn, transition),
                Actions.delay(3f),
                Actions.addAction(scene05),
                Actions.addAction(Actions.moveTo(15f, 76f, 7.5f, Interpolation.linear), animDemon),
                Actions.delay(7.5f),
                Actions.run {
                    showMessage("DEMON: I will now send my legions of demons to destroy these sacks of meat!")
                },
                Actions.delay(5f),
                Actions.addAction(fadeOut, transition),
                Actions.delay(3f),
                Actions.run {
                    hideDialog()
                },
                Actions.after(switchScreen))
    }

    override fun show() {
        notify(AudioCommand.MUSIC_STOP_ALL, AudioTypeEvent.NONE)
        notify(AudioCommand.MUSIC_LOAD, AudioTypeEvent.MUSIC_INTRO_CUTSCENE)
        notify(AudioCommand.MUSIC_PLAY_LOOP, AudioTypeEvent.MUSIC_INTRO_CUTSCENE)
        stage.clear()
        uiStage.clear()
        dialog.contentTable.reset()
        initialize()
        scenes = getCutsceneAction()
        stage.addAction(scenes)
        Gdx.input.inputProcessor = stage
    }

    private fun getAnimatedImageFor(name: EntityFactory.EntityName): AnimatedImage {
        val animatedImage = AnimatedImage()
        animatedImage.entity = getEntityByName(name)
        animatedImage.setSize(1f, 1f)
        return animatedImage
    }

    private fun cameraFollow(actor: Actor) {
        mainChar = actor
        shouldFollow = true
    }

    private fun setCameraPosition(x: Float, y: Float) {
        camera.position.set(x, y, 0f)
        shouldFollow = false
    }

    private fun showMessage(message: String) {
        label.setText(message)
        dialog.pack()
        dialog.setPosition(stage.width / 2 - dialog.width / 2, stage.height / 2 + dialog.height * 2)
        dialog.isVisible = true
    }

    private fun hideDialog() {
        dialog.isVisible = false
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) Gdx.app.exit()

        if (shouldFollow) {
            camera.position.set(mainChar.x, mainChar.y, 0f)
            camera.update()
        }
        tiledMapRenderer.batch.enableBlending()
        tiledMapRenderer.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        tiledMapRenderer.setView(camera)

        if (isNewMapLoaded) {
            tiledMapRenderer.map = currentMap
            isNewMapLoaded = false
        }
        tiledMapRenderer.render()

        stage.act(delta)
        stage.draw()

        uiStage.act(delta)
        uiStage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.setScreenSize(width, height)
        uiStage.viewport.setScreenSize(width, height)
    }

    override fun hide() {
        notify(AudioCommand.MUSIC_STOP, AudioTypeEvent.MUSIC_INTRO_CUTSCENE)
        Gdx.input.inputProcessor = null
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        stage.dispose()
        uiStage.dispose()
    }
}