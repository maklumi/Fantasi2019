package com.maklumi

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.maklumi.dialog.ComponentObserver

class PlayerGraphicsComponent : GraphicsComponent() {

    private val prevPos = Vector2()

    override fun update(entity: Entity, batch: Batch, delta: Float) {
        //Player has moved
        if (prevPos.x != currentPosition.x || prevPos.y != currentPosition.y) {
            notify("", ComponentObserver.ComponentEvent.PLAYER_HAS_MOVED)
            prevPos.set(currentPosition)
        }
        super.update(entity, batch, delta)
    }
}