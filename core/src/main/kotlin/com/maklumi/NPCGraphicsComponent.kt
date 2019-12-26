package com.maklumi

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.maklumi.Component.MESSAGE
import com.maklumi.MapManager.camera

class NPCGraphicsComponent : GraphicsComponent() {

    private var isSelected = false
    private val shapeRenderer = ShapeRenderer()

    override fun receiveMessage(message: String) {
        super.receiveMessage(message)

        val string = message.split(MESSAGE_TOKEN)
        if (string.size == 1) {
            if (MESSAGE.ENTITY_SELECTED == MESSAGE.valueOf(string[0])) isSelected = true
            if (MESSAGE.ENTITY_DESELECTED == MESSAGE.valueOf(string[0])) isSelected = false
        }
    }

    override fun drawSelected(entity: Entity) {
        if (!isSelected) return
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        val rect = entity.getCurrentBoundingBox()
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.setColor(0.0f, 1.0f, 1.0f, 0.5f)

        val x = rect.x - rect.width / 2
        val y = rect.y - rect.height

        shapeRenderer.ellipse(x, y, rect.width * 2, rect.height)
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }
}