package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.maklumi.*
import com.maklumi.AnimationType.*
import com.maklumi.MapManager.currentMap
import com.maklumi.MapManager.isNewMapLoaded
import com.maklumi.ui.AnimatedImage

class CutSceneScreen : Screen {

    init {
        MapManager.loadMap(MapFactory.MapType.TOWN)
    }

    private val b = 4 // multiplication factor for images
    private val tiledMapRenderer = OrthogonalTiledMapRenderer(currentMap, b * 1f)
    private val camera = OrthographicCamera()
    private val viewport = ScreenViewport(camera)
    private val stage = Stage(viewport)
    private val entity = EntityFactory.getEntity(EntityFactory.EntityType.NPC)
    private val animImage = AnimatedImage()
    private val j = Json()
    private val wh = 16f * b
    private var mainChar = Actor()

    override fun show() {
        entity.entityConfig = Entity.loadEntityConfigBy(EntityFactory.PLAYER_CONFIG)
        entity.sendMessage(Component.MESSAGE.INIT_STATE, j.toJson(Entity.State.WALKING))
        entity.sendMessage(Component.MESSAGE.LOAD_ANIMATIONS, j.toJson(entity.entityConfig))

        animImage.setAnim(WALK_RIGHT)
        animImage.setPosition(16f, 16f)
        animImage.setSize(wh, wh)

        animImage.addAction(
                Actions.sequence(
                        Actions.run { mainChar = animImage },
                        Actions.run { animImage.setAnim(WALK_RIGHT) },
                        Actions.moveTo(16f * 40, 16f, 5f),
                        Actions.run { animImage.setAnim(WALK_UP) },
                        Actions.moveTo(16f * 40, 16f * 52, 5f),
                        Actions.run { MapManager.loadMap(MapFactory.MapType.CASTLE_OF_DOOM) },
                        Actions.run { animImage.setAnim(WALK_DOWN) }
                )
        )
        stage.addActor(animImage)
        Gdx.input.inputProcessor = stage
    }

    private fun AnimatedImage.setAnim(animationType: AnimationType) {
        this.setAnim(entity.getAnimation(animationType))
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) Gdx.app.exit()

        camera.position.set(mainChar.x, mainChar.y, 0f)
        camera.update()
        tiledMapRenderer.setView(camera)

        if (isNewMapLoaded) {
            tiledMapRenderer.map = currentMap
            isNewMapLoaded = false
        }
        tiledMapRenderer.render()

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