package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.maklumi.EntityFactory
import com.maklumi.EntityFactory.EntityName.*
import com.maklumi.EntityFactory.getEntityByName
import com.maklumi.MapFactory
import com.maklumi.MapManager
import com.maklumi.MapManager.currentMap
import com.maklumi.MapManager.isNewMapLoaded
import com.maklumi.Utility
import com.maklumi.battle.MonsterFactory
import com.maklumi.ui.AnimatedImage

class CutSceneScreen : Screen {

    init {
        MapManager.loadMap(MapFactory.MapType.TOWN)
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
    private val transition = Image()
    private val fadeOut = Fade(0f)
    private val fadeIn = Fade(1f)

    inner class Fade(val float: Float) : Action() {
        override fun act(delta: Float): Boolean {
            if (float > 0.5f)
                transition.addAction(Actions.sequence(Actions.alpha(1f), Actions.fadeOut(3f)))
            else
                transition.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(3f)))
            transition.setFillParent(true)
            val bg = Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("generic_background"))
            transition.drawable = bg.drawable
            return true
        }
    }

    private var shouldFollow = false

    override fun show() {
        val animBlackSmith = getAnimatedImageFor(TOWN_BLACKSMITH)
        animBlackSmith.setPosition(10f, 16f)

        val animInnKeeper = getAnimatedImageFor(TOWN_INNKEEPER)
        animInnKeeper.setPosition(12f, 15f)

        val animMage = getAnimatedImageFor(TOWN_MAGE)
        animMage.setPosition(11f, 17f)

        val animFire = getAnimatedImageFor(FIRE)

        val demon = MonsterFactory.getMonster(MonsterFactory.MonsterEntityType.MONSTER042)
        val animDemon = AnimatedImage()
        animDemon.entity = demon
        animDemon.setSize(1f, 1f)
        animDemon.isVisible = false

        stage.addAction(
                Actions.sequence(
                        Actions.run {
                            setCameraPosition(10f, 16f)
                            showMessage("BLACKSMITH: \nWe have planned this long enough. The time is now! I have had enough talk...")
                        },
                        Actions.delay(3.5f),
                        Actions.run {
                            showMessage("MAGE: \nThis is dark magic you fool. We must proceed with caution, or this could end badly for all of us")
                        },
                        Actions.delay(3.5f),
                        Actions.run {
                            showMessage("INNKEEPER: Both of you need to keep it down. If we get caught using black magic, we will all be hanged!")
                        },
                        Actions.delay(2.5f),
                        Actions.addAction(fadeOut),
                        Actions.delay(3f),
                        Actions.run {
                            hideDialog()
                            MapManager.loadMap(MapFactory.MapType.TOP_WORLD)
                            setCameraPosition(50f, 30f)
                            animBlackSmith.setPosition(50f, 30f)
                            animInnKeeper.setPosition(52f, 30f)
                            animMage.setPosition(50f, 28f)
                            animFire.setPosition(52f, 28f)
                        },
                        Actions.addAction(fadeIn),
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
                        Actions.run {
                            hideDialog()
                            animDemon.setPosition(52f, 28f)
                            animDemon.isVisible = true
                        },
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
                        Actions.delay(3f),
                        Actions.addAction(fadeOut),
                        Actions.run {
                            hideDialog()
                            animBlackSmith.isVisible = false
                            animInnKeeper.isVisible = false
                            animMage.isVisible = false
                            animFire.isVisible = false

                            MapManager.loadMap(MapFactory.MapType.TOP_WORLD)

                            animDemon.isVisible = true
                            animDemon.setScale(1f, 1f)
                            animDemon.setPosition(50f, 40f)

                            cameraFollow(animDemon)
                        },
                        Actions.addAction(fadeIn),
                        Actions.addAction(Actions.moveTo(54f, 65f, 13f, Interpolation.linear), animDemon),
                        Actions.delay(10f),
                        Actions.addAction(fadeOut),
                        Actions.delay(3f),
                        Actions.addAction(fadeIn),
                        Actions.delay(3f),
                        Actions.run {
                            hideDialog()
                            animBlackSmith.isVisible = false
                            animInnKeeper.isVisible = false
                            animMage.isVisible = false
                            animFire.isVisible = false

                            MapManager.loadMap(MapFactory.MapType.CASTLE_OF_DOOM)
                            animDemon.isVisible = true
                            animDemon.setPosition(15f, 1f)
                            cameraFollow(animDemon)
                        },
                        Actions.addAction(Actions.moveTo(15f, 76f, 7.5f, Interpolation.linear), animDemon),
                        Actions.delay(7.5f),
                        Actions.run {
                            showMessage("DEMON: I will now send my legions of demons to destroy these sacks of meat!")
                        },
                        Actions.delay(5f),
                        Actions.addAction(fadeOut),
                        Actions.delay(10f),
                        Actions.run { hideDialog() }
                )
        )

        stage.addActor(animBlackSmith)
        stage.addActor(animInnKeeper)
        stage.addActor(animMage)
        stage.addActor(animFire)
        stage.addActor(animDemon)
        stage.addActor(transition)
        transition.toFront()
        uiStage.addActor(dialog)
        label.setWrap(true)
        dialog.contentTable.add(label).width(stage.width / 2).pad(10f, 10f, 10f, 10f)
        camera.setToOrtho(false, screenRatio * 12, screenRatio * 12f)

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