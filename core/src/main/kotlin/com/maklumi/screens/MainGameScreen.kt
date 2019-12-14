package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.maklumi.Utility


class MainGameScreen : Screen {
    private val tag = javaClass.simpleName

    private val overviewMap = "maps/tmx/Town.tmx"
    private val unitScale = 1f / 16f
    private var townMap: TiledMap? = null

    lateinit var orthoCamera: OrthographicCamera
    lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer

    private var viewportWidth: Float = 0f
    private var viewportHeight: Float = 0f
    private var physicalWidth: Float = 0f
    private var physicalHeight: Float = 0f
    private var aspectRatio: Float = 0f

    @Override
    override fun show() {
        setupViewport()

        Utility.loadMapAsset(overviewMap)
        if (Utility.assetManager.isLoaded(overviewMap)) {
            townMap = Utility.getMapAsset(overviewMap)
        }

        orthoCamera = OrthographicCamera(viewportWidth, viewportHeight)
        orthoCamera.setToOrtho(false, 20f, 14f)
        orthoCamera.update()

        tiledMapRenderer = OrthogonalTiledMapRenderer(townMap, unitScale)
        tiledMapRenderer.setView(orthoCamera)
    }

    @Override
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        tiledMapRenderer.render()
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
    override fun dispose() { // Destroy screen's assets here.
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