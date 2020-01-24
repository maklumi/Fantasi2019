package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.maklumi.Component.MESSAGE
import com.maklumi.EntityFactory
import com.maklumi.Fantasi
import com.maklumi.Map.Companion.BACKGROUND_LAYER
import com.maklumi.Map.Companion.DECORATION_LAYER
import com.maklumi.Map.Companion.GROUND_LAYER
import com.maklumi.MapManager
import com.maklumi.MapManager.camera
import com.maklumi.MapManager.collisionLayer
import com.maklumi.MapManager.currentMap
import com.maklumi.MapManager.enemySpawnLayer
import com.maklumi.MapManager.isNewMapLoaded
import com.maklumi.MapManager.playerStartUnitScaled
import com.maklumi.MapManager.portalLayer
import com.maklumi.MapManager.spawnsLayer
import com.maklumi.MapManager.unitScale
import com.maklumi.MapManager.updateMapEntities
import com.maklumi.audio.AudioManager
import com.maklumi.json
import com.maklumi.profile.ProfileManager
import com.maklumi.ui.PlayerHUD


class MainGameScreen(private val fantasi: Fantasi) : GameScreen() {

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
        SAVING, LOADING, RUNNING, PAUSED, GAME_OVER;

        fun toggle(): GameState {
            return when (this) {
                RUNNING -> PAUSED
                PAUSED -> RUNNING
                else -> this
            }
        }
    }

    companion object {
        var gameState: GameState = GameState.RUNNING
            set(value) {
                when (field) {
                    GameState.RUNNING -> field = value
                    GameState.PAUSED -> {
                        if (field == GameState.PAUSED) field = GameState.RUNNING
                        else if (field == GameState.RUNNING) field = GameState.PAUSED
                    }
                    GameState.LOADING -> {
                        field = GameState.RUNNING
                        ProfileManager.loadProfile()
                    }
                    GameState.SAVING -> {
                        field = value
                        ProfileManager.saveProfile()
                    }
                    GameState.GAME_OVER -> {
                        field = value
                    }
                }
            }
    }

    @Override
    override fun show() {
        setupViewport()

        camera = OrthographicCamera(viewportWidth, viewportHeight)
        camera.setToOrtho(false, 40f, 40f)
        ProfileManager.profileObservers.add(MapManager)
        MapManager.player = player
        playerHUD = PlayerHUD(hudCamera)
        ProfileManager.profileObservers.add(playerHUD)
        ProfileManager.loadProfile()
        player.registerObserver(playerHUD)
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
        if (gameState == GameState.GAME_OVER) {
            gameState = GameState.RUNNING
            fantasi.screen = fantasi.getScreenType(Fantasi.ScreenType.GameOver)
            return
        }
        if (gameState == GameState.PAUSED) {
            player.updateInput(delta)
            playerHUD.render(delta)
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
            // also register conversation observer for others
            MapManager.getCurrentMapEntities().forEach {
                it.registerObserver(playerHUD)
            }
            playerHUD.updateEntityObservers()
            playerHUD.addTransitionToStage()
        }

        tiledMapRenderer.batch.enableBlending()
        tiledMapRenderer.batch.setBlendFunction(GL20.GL_BLEND_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        val lightMap = MapManager.lightMapLayer as TiledMapImageLayer?
        if (lightMap != null) {
            renderTiles()
            renderEntities(delta)
            renderLightMap(lightMap)
        } else {
            tiledMapRenderer.render()
            renderEntities(delta)
        }

        // debug
        drawBoundingBox()
        // head up display
        playerHUD.render(delta)
    }

    private fun renderEntities(delta: Float) {
        // map entities
        updateMapEntities(tiledMapRenderer.batch, delta)
        // player entity
        player.update(tiledMapRenderer.batch, delta)
    }

    private fun renderTiles() {
        if (currentMap == null) return
        val backMapLayer = currentMap!!.layers[BACKGROUND_LAYER] as TiledMapTileLayer?
        val groundMapLayer = currentMap!!.layers[GROUND_LAYER] as TiledMapTileLayer?
        val decoMapLayer = currentMap!!.layers[DECORATION_LAYER] as TiledMapTileLayer?

        tiledMapRenderer.apply {
            batch.begin()
            if (backMapLayer != null) renderTileLayer(backMapLayer)
            if (groundMapLayer != null) renderTileLayer(groundMapLayer)
            if (decoMapLayer != null) renderTileLayer(decoMapLayer)
            batch.end()
        }
    }

    private fun renderLightMap(lightMap: TiledMapImageLayer) {
        tiledMapRenderer.apply {
            batch.begin()
            batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA)
            renderImageLayer(lightMap)
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            batch.end()
        }
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
            if (enemySpawnLayer != null) debugLayer(enemySpawnLayer!!, Color.ORANGE)
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
        gameState = GameState.LOADING
    }

    @Override
    override fun hide() {
        if (gameState != GameState.GAME_OVER) {
            gameState = GameState.SAVING
        }
        Gdx.input.inputProcessor = null
    }

    @Override
    override fun dispose() {
        Gdx.input.inputProcessor = null
        AudioManager.dispose()
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