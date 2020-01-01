package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.maklumi.Component.MESSAGE
import com.maklumi.EntityFactory
import com.maklumi.MapManager
import com.maklumi.MapManager.camera
import com.maklumi.MapManager.collisionLayer
import com.maklumi.MapManager.currentMap
import com.maklumi.MapManager.isNewMapLoaded
import com.maklumi.MapManager.playerStartUnitScaled
import com.maklumi.MapManager.portalLayer
import com.maklumi.MapManager.spawnsLayer
import com.maklumi.MapManager.unitScale
import com.maklumi.MapManager.updateMapEntities
import com.maklumi.json
import com.maklumi.profile.ProfileManager
import com.maklumi.ui.PlayerHUD


class MainGameScreen : Screen {

    private val shapeRenderer = ShapeRenderer()

    private lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer

    private var viewportWidth: Float = 0f
    private var viewportHeight: Float = 0f
    private var physicalWidth: Float = 0f
    private var physicalHeight: Float = 0f
    private var aspectRatio: Float = 0f

    private val player = EntityFactory.getEntity(EntityFactory.EntityType.PLAYER)
    private val hudCamera = OrthographicCamera()
    private lateinit var playerHUD: PlayerHUD
    private var debugHUD = false

    enum class GameState {
        RUNNING, PAUSED;

        fun toggle(): GameState {
            return when (this) {
                RUNNING -> PAUSED
                PAUSED -> RUNNING
            }
        }
    }

    companion object {
        var gameState: GameState = GameState.RUNNING
    }

    @Override
    override fun show() {
        setupViewport()

        camera = OrthographicCamera(viewportWidth, viewportHeight)
        camera.setToOrtho(false, 40f, 40f)
        ProfileManager.profileObservers.add(MapManager)
        MapManager.player = player
        playerHUD = PlayerHUD(hudCamera)
        ProfileManager.profileObservers.add(playerHUD.inventoryUI)
        ProfileManager.loadProfile()
        tiledMapRenderer = OrthogonalTiledMapRenderer(currentMap, unitScale)
        // to prevent initial flicker
        camera.position.set(playerStartUnitScaled, 0f)
        camera.update()
        hudCamera.setToOrtho(false, physicalWidth, physicalHeight)

        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(playerHUD.stage)
        multiplexer.addProcessor(player.inputComponent)
        Gdx.input.inputProcessor = multiplexer
    }

    @Override
    override fun render(delta: Float) {
        if (gameState == GameState.PAUSED) {
            player.updateInput(delta)
            return
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (debugHUD) {
            renderPlayerHUD(delta)
            return
        }

        // world map and camera
        tiledMapRenderer.setView(camera)
        if (isNewMapLoaded) {
            tiledMapRenderer.map = currentMap
            player.sendMessage(MESSAGE.INIT_START_POSITION, json.toJson(playerStartUnitScaled))
            isNewMapLoaded = false
        }
        tiledMapRenderer.render()

        // debug
        drawBoundingBox()
        // map entities
        updateMapEntities(tiledMapRenderer.batch, delta)
        // player entity
        player.update(tiledMapRenderer.batch, delta)
        // head up display
        playerHUD.render(delta)
    }

    private fun renderPlayerHUD(delta: Float) {
        playerHUD.render(delta)
        player.inputComponent.update(player, delta)
    }


    private fun drawBoundingBox() {
        shapeRenderer.apply {
            projectionMatrix = camera.combined
            begin(ShapeRenderer.ShapeType.Line)
            fun debugLayer(layer: MapLayer, clr: Color) {
                layer.objects.forEach {
                    it as RectangleMapObject
                    shapeRenderer.color = clr
                    shapeRenderer.rect(it.rectangle.x * unitScale, it.rectangle.y * unitScale,
                            it.rectangle.width * unitScale, it.rectangle.height * unitScale)

                }
            }
            if (collisionLayer != null) debugLayer(collisionLayer!!, Color.WHITE)
            if (portalLayer != null) debugLayer(portalLayer!!, Color.YELLOW)
            if (spawnsLayer != null) debugLayer(spawnsLayer!!, Color.GOLD)
            end()
        }

    }

    @Override
    override fun resize(width: Int, height: Int) {
        playerHUD.resize(width, height)
    }

    @Override
    override fun pause() {
        gameState = GameState.PAUSED
        ProfileManager.saveProfile()
    }

    @Override
    override fun resume() {
        gameState = GameState.RUNNING
    }

    @Override
    override fun hide() {
    }

    @Override
    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    private fun setupViewport() {
        // set requested virtual size
        val virtualWidth = 4f
        val virtualHeight = 3f

        //pixel dimensions of display
        physicalWidth = Gdx.graphics.width.toFloat()
        physicalHeight = Gdx.graphics.height.toFloat()

        //aspect ratio for current viewport
        aspectRatio = virtualWidth / virtualHeight

        //update viewport if there could be skewing
        if (physicalWidth / physicalHeight >= aspectRatio) {
            //Letterbox left and right
            viewportWidth = viewportHeight * (physicalWidth / physicalHeight)
            viewportHeight = virtualHeight
        } else {
            //letterbox above and below
            viewportWidth = virtualWidth
            viewportHeight = viewportWidth * (physicalHeight / physicalWidth)
        }
    }
}