package com.maklumi.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import com.maklumi.Player
import com.maklumi.PlayerController
import com.maklumi.Utility
import ktx.graphics.use


class MainGameScreen : Screen {

    private val overviewMap = "maps/town.tmx" //castle_of_doom town topworld
    private val unitScale = 1f / 16f
    private var townMap: TiledMap? = null
    private var collisionLayer: MapLayer? = null
    private val shapeRenderer = ShapeRenderer()
    private lateinit var currBoundingBox: Rectangle

    private lateinit var orthoCamera: OrthographicCamera
    private lateinit var tiledMapRenderer: OrthogonalTiledMapRenderer

    private var viewportWidth: Float = 0f
    private var viewportHeight: Float = 0f
    private var physicalWidth: Float = 0f
    private var physicalHeight: Float = 0f
    private var aspectRatio: Float = 0f

    private val player = Player()
    private val controller = PlayerController(player)

    @Override
    override fun show() {
        setupViewport()

        Utility.loadMapAsset(overviewMap)
        townMap = Utility.getMapAsset(overviewMap)
        collisionLayer = townMap?.layers?.get("MAP_COLLISION_LAYER")
        currBoundingBox = player.boundingBox

        orthoCamera = OrthographicCamera(viewportWidth, viewportHeight)
        orthoCamera.setToOrtho(false, 20f, 14f)

        tiledMapRenderer = OrthogonalTiledMapRenderer(townMap, unitScale)

        Gdx.input.inputProcessor = controller
    }

    @Override
    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        controller.processInput(delta)

        player.update(delta)
        if (!isCollisionWithMapLayer(player.nextBoundingBox)) {
            player.setCurrentPosition()
        }

        // lock and center the camera to player's position
        orthoCamera.position.set(player.currentPosition.x, player.currentPosition.y, 0f)
        orthoCamera.update()
        tiledMapRenderer.setView(orthoCamera)
        tiledMapRenderer.render()

        tiledMapRenderer.batch.use {
            it.draw(player.currentFrame,
                    player.currentPosition.x,
                    player.currentPosition.y, 1f, 1f)
        }

        drawBoundingBox()
    }

    private fun isCollisionWithMapLayer(rect: Rectangle): Boolean {
        if (collisionLayer == null) return false

        //Convert rectangle to map coordinates in pixels
        // rect = (x=10,y=10,w=16,h=8) => (10*16,10*16,16,8) pixels
        val temp = Rectangle(rect.x / unitScale, rect.y / unitScale,
                rect.width, rect.height)

        return collisionLayer!!.objects.any {
            it as RectangleMapObject
            temp.overlaps(it.rectangle)
        }
    }

    private fun drawBoundingBox() {
        val b = player.boundingBox
        shapeRenderer.apply {
            projectionMatrix = orthoCamera.combined
            begin(ShapeRenderer.ShapeType.Filled)
            color = Color.YELLOW
            rect(b.x, b.y, b.width * unitScale, b.height * unitScale)

            collisionLayer?.objects?.forEach {
                it as RectangleMapObject
                color = Color.BLUE
                rect(it.rectangle.x * unitScale, it.rectangle.y * unitScale,
                        it.rectangle.width * unitScale, it.rectangle.height * unitScale)
            }
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