package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.maklumi.Player
import com.maklumi.PlayerController
import com.maklumi.Utility
import ktx.graphics.use


class MainGameScreen : Screen {
    private val tag = javaClass.simpleName

    private val overviewMap = "sprites/tmx/Town.tmx"
    private val unitScale = 1f / 16f
    private var townMap: TiledMap? = null

    private lateinit var orthoCamera: OrthographicCamera
    private lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer

    private var viewportWidth: Float = 0f
    private var viewportHeight: Float = 0f
    private var physicalWidth: Float = 0f
    private var physicalHeight: Float = 0f
    private var aspectRatio: Float = 0f

    private lateinit var playerSprite: Sprite

    private val player = Player()
    private var controller = PlayerController(player)

    @Override
    override fun show() {
        setupViewport()

        Utility.loadMapAsset(overviewMap)
        townMap = Utility.getMapAsset(overviewMap)

        orthoCamera = OrthographicCamera(viewportWidth, viewportHeight)
        orthoCamera.setToOrtho(false, 20f, 14f)

        tiledMapRenderer = OrthogonalTiledMapRenderer(townMap, unitScale)

        playerSprite = player.frameSprite

        Gdx.input.inputProcessor = controller
    }

    @Override
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        controller.processInput(delta)

        // lock and center the camera to player's position
        orthoCamera.position.set(playerSprite.x, playerSprite.y, 0f)
        orthoCamera.update()
        tiledMapRenderer.setView(orthoCamera)
        tiledMapRenderer.render()

        tiledMapRenderer.batch.use {
            it.draw(playerSprite,
                    player.playerPosition.x,
                    player.playerPosition.y, 1f, 1f)
        }
    }

    @Override
    override fun resize(width: Int, height: Int) { // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    override fun pause() { // Invoked when your application is paused.
    }

    @Override
    override fun resume() { // Invoked when your application is resumed after pause.
    }

    @Override
    override fun hide() { // This method is called when another screen replaces this one.
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

        Gdx.app.debug(tag, "WorldRenderer: virtual: ($virtualWidth,$virtualHeight)")
        Gdx.app.debug(tag, "WorldRenderer: viewport: ($viewportWidth,$viewportHeight)")
        Gdx.app.debug(tag, "WorldRenderer: physical: ($physicalWidth,$physicalHeight)")
    }
}