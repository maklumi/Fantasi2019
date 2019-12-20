package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.maklumi.Entity
import com.maklumi.MapManager.collisionLayer
import com.maklumi.MapManager.currentMap
import com.maklumi.MapManager.currentMapName
import com.maklumi.MapManager.loadMap
import com.maklumi.MapManager.playerStartUnitScaled
import com.maklumi.MapManager.portalLayer
import com.maklumi.MapManager.setClosestStartPosition
import com.maklumi.MapManager.spawnsLayer
import com.maklumi.MapManager.unitScale
import ktx.graphics.use


class MainGameScreen : Screen {

    private val shapeRenderer = ShapeRenderer()

    private lateinit var orthoCamera: OrthographicCamera
    private lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer

    private var viewportWidth: Float = 0f
    private var viewportHeight: Float = 0f
    private var physicalWidth: Float = 0f
    private var physicalHeight: Float = 0f
    private var aspectRatio: Float = 0f

    private val player = Entity()
    private val controller = player.inputComponent
    private val temp = Rectangle()

    @Override
    override fun show() {
        setupViewport()

        loadMap(currentMapName)
        player.initStartPosition(playerStartUnitScaled)

        orthoCamera = OrthographicCamera(viewportWidth, viewportHeight)
        orthoCamera.setToOrtho(false, 20f, 14f)

        tiledMapRenderer = OrthogonalTiledMapRenderer(currentMap, unitScale)

        Gdx.input.inputProcessor = controller
    }

    @Override
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        controller.update(player, delta)

        isCollisionWithPortalLayer(player.currentBound)

        player.update(delta)
        if (isCollisionWithMapLayer(collisionLayer, player.nextBound) == null) {
            player.setCurrentPosition()
        }

        // lock and center the camera to player's position
        orthoCamera.position.set(player.currentPosition.x, player.currentPosition.y, 0f)
        orthoCamera.update()
        tiledMapRenderer.setView(orthoCamera)
        tiledMapRenderer.render()

        tiledMapRenderer.batch.use {
            it.draw(player.graphicsComponent.currentFrame,
                    player.currentPosition.x,
                    player.currentPosition.y, 1f, 1f)
        }

        drawBoundingBox()
    }

    private fun isCollisionWithPortalLayer(rect: Rectangle) {
        val portalHit = isCollisionWithMapLayer(portalLayer, rect)

        if (portalHit != null) {
            // before leaving, cache closest start position from current player position
            setClosestStartPosition(player.currentPosition)
            // then
            loadMap(portalHit.name)
            tiledMapRenderer.map = currentMap
            player.initStartPosition(playerStartUnitScaled)
        }
    }

    private fun isCollisionWithMapLayer(mapLayer: MapLayer?, rect: Rectangle): MapObject? {
        if (mapLayer == null) return null

        //Convert rectangle (in world unit) to mapLayer coordinates (in pixels)
        temp.setPosition(rect.x / unitScale, rect.y / unitScale)
        temp.setSize(rect.width, rect.height)

        return mapLayer.objects.firstOrNull {
            temp.overlaps((it as RectangleMapObject).rectangle)
        }
    }

    private fun drawBoundingBox() {
//        val b = player.nextBound
        shapeRenderer.apply {
            projectionMatrix = orthoCamera.combined
            begin(ShapeRenderer.ShapeType.Filled)
            color = Color.YELLOW
            //            rect(b.x, b.y, b.width * unitScale, b.height * unitScale)
            fun debugLayer(layer: MapLayer, clr: Color) {
                layer.objects.forEach {
                    it as RectangleMapObject
                    shapeRenderer.color = clr
                    shapeRenderer.rect(it.rectangle.x * unitScale, it.rectangle.y * unitScale,
                            it.rectangle.width * unitScale, it.rectangle.height * unitScale)

                }
            }
            if (collisionLayer != null) debugLayer(collisionLayer!!, Color.BLUE)
            if (portalLayer != null) debugLayer(portalLayer!!, Color.DARK_GRAY)
//            if (spawnsLayer != null) debugLayer(spawnsLayer!!, Color.LIME)
            end()
        }

    }

    @Override
    override fun resize(width: Int, height: Int) {
    }

    @Override
    override fun pause() {
    }

    @Override
    override fun resume() {
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